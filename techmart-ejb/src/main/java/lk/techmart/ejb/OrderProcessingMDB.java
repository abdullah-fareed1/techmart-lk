package lk.techmart.ejb;

import lk.techmart.model.Inventory;
import lk.techmart.model.Notification;
import lk.techmart.model.Order;
import lk.techmart.model.OrderItem;
import jakarta.ejb.ActivationConfigProperty;
import jakarta.ejb.EJB;
import jakarta.ejb.MessageDriven;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import jakarta.jms.TextMessage;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@MessageDriven(
        activationConfig = {
                @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "jms/TechmartOrderQueue"),
                @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "jakarta.jms.Queue")
        }
)
public class OrderProcessingMDB implements MessageListener {

    public OrderProcessingMDB() {
        System.out.println("MDB LOADED");
    }

    @PersistenceContext(unitName = "techmartPU")
    private EntityManager entityManager;

    @EJB
    private MetricsBean metrics;

    @Override
    public void onMessage(Message message) {
        long start = System.currentTimeMillis();
        System.out.println("MDB TRIGGERED");
        try {
            String orderIdStr = ((TextMessage) message).getText();
            Long orderId = Long.valueOf(orderIdStr);

            Order order = entityManager.find(Order.class, orderId);

            if (order != null) {
                for (OrderItem item : order.getItems()) {
                    Long productId = item.getProduct().getId();
                    deductStockProportionally(productId, item.getQuantity());
                }

                order.setStatus("CONFIRMED");

                Notification notification = new Notification();
                notification.setOrder(order);
                notification.setMessage("Order #" + order.getId() + " confirmed and inventory updated.");
                notification.setStatus("SUCCESS");
                notification.setSentAt(LocalDateTime.now());
                entityManager.persist(notification);
            }

            metrics.incrementMessages();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            long end = System.currentTimeMillis();
            metrics.addProcessingTime(end - start);
        }
    }

    private void deductStockProportionally(Long productId, int orderedQty) {
        List<Inventory> invList = entityManager.createQuery(
                        "SELECT i FROM Inventory i WHERE i.productId = :pid", Inventory.class)
                .setParameter("pid", productId)
                .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                .getResultList();

        if (invList.isEmpty()) {
            System.out.println("Warning: No inventory records found for Product ID: " + productId);
            return;
        }

        List<Integer> quantities = invList.stream()
                .map(Inventory::getQuantity)
                .collect(Collectors.toList());

        int totalStock = quantities.stream().mapToInt(Integer::intValue).sum();

        if (totalStock <= 0) {
            System.out.println("Warning: Zero total stock across warehouses for Product ID: " + productId);
            return;
        }

        int[] shares = StockAllocator.computeShares(quantities, orderedQty);

        for (int idx = 0; idx < invList.size(); idx++) {
            Inventory inv = invList.get(idx);
            int deduction = shares[idx];
            inv.setQuantity(inv.getQuantity() - deduction);

            Long warehouseId = inv.getWarehouse() != null ? inv.getWarehouse().getId() : null;
            System.out.println("Warehouse " + warehouseId + " stock reduced by " + deduction
                    + " for Product ID: " + productId);
        }
    }
}