package lk.techmart.ejb;

import lk.techmart.model.Customer;
import lk.techmart.model.Order;
import lk.techmart.model.OrderItem;
import lk.techmart.model.Product;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

@Stateless
@jakarta.interceptor.Interceptors(PerformanceInterCeptor.class)
public class OrderServiceBean {

    @PersistenceContext(unitName = "techmartPU")
    private EntityManager entityManager;

    @EJB
    private NotificationServiceBean notificationService;

    public Order createOrder(Map<Product, Integer> cart, Long customerId) {

        for (Map.Entry<Product, Integer> entry : cart.entrySet()) {
            Product p = entry.getKey();
            int requestedQty = entry.getValue();

            Number totalAvailable = entityManager.createQuery(
                            "SELECT COALESCE(SUM(i.quantity), 0) FROM Inventory i WHERE i.productId = :pid", Number.class)
                    .setParameter("pid", p.getId())
                    .getSingleResult();

            long available = totalAvailable == null ? 0L : totalAvailable.longValue();

            if (available < requestedQty) {
                throw new InsufficientStockException(p.getName(), requestedQty, available);
            }
        }

        Order order = new Order();

        if (customerId != null) {
            Customer customer = entityManager.find(Customer.class, customerId);
            order.setCustomer(customer);
        }

        order.setStatus("PENDING");
        order.setOrderDate(LocalDateTime.now());

        List<OrderItem> items = new ArrayList<>();
        double total = 0.0;

        for (Map.Entry<Product, Integer> entry : cart.entrySet()) {
            Product p = entry.getKey();

            System.out.println("PRODUCT ID = " + p.getId());

            int qty = entry.getValue();

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProduct(p);
            item.setQuantity(qty);
            item.setPrice(p.getPrice());

            items.add(item);
            total += p.getPrice() * qty;
        }

        order.setItems(items);
        order.setTotalAmount(total);

        entityManager.persist(order);
        entityManager.flush();

        System.out.println("ORDER ID = " + order.getId());

        Future<String> asyncResult = notificationService.sendOrderConfirmationEmail(order.getId(), total);

        try {
            System.out.println("Non-blocking main flow continues...");
        } catch (Exception e) {
            System.out.println("Async handling encountered an error");
        }

        return order;
    }

    public List<Order> getRecentOrders(int limit) {
        return entityManager.createQuery("SELECT o FROM Order o ORDER BY o.orderDate DESC", Order.class)
                .setMaxResults(limit)
                .getResultList();
    }
}