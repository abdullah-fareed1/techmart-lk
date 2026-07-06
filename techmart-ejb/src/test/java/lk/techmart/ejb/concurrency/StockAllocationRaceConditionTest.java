package lk.techmart.ejb.concurrency;

import lk.techmart.ejb.EjbTestUtils;
import lk.techmart.ejb.InsufficientStockException;
import lk.techmart.ejb.NotificationServiceBean;
import lk.techmart.ejb.OrderServiceBean;
import lk.techmart.model.Product;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class StockAllocationRaceConditionTest {

    @SuppressWarnings("unchecked")
    private OrderServiceBean buildOrderService(AtomicLong sharedAvailableStock, Long productId) {
        EntityManager em = mock(EntityManager.class);
        NotificationServiceBean notificationService = mock(NotificationServiceBean.class);

        TypedQuery<Number> stockQuery = mock(TypedQuery.class);
        when(em.createQuery(
                "SELECT COALESCE(SUM(i.quantity), 0) FROM Inventory i WHERE i.productId = :pid", Number.class))
                .thenReturn(stockQuery);
        when(stockQuery.setParameter("pid", productId)).thenReturn(stockQuery);
        when(stockQuery.getSingleResult()).thenAnswer(inv -> sharedAvailableStock.get());

        OrderServiceBean orderService = new OrderServiceBean();
        EjbTestUtils.setField(orderService, "entityManager", em);
        EjbTestUtils.setField(orderService, "notificationService", notificationService);
        return orderService;
    }

    @Test
    void twoConcurrentOrders_readingSameStockSnapshot_canBothPassValidation() throws Exception {
        Product product = new Product();
        product.setId(1L);
        product.setName("Contested Item");
        AtomicLong sharedAvailableStock = new AtomicLong(10);

        OrderServiceBean orderServiceForThread1 = buildOrderService(sharedAvailableStock, 1L);
        OrderServiceBean orderServiceForThread2 = buildOrderService(sharedAvailableStock, 1L);

        Map<Product, Integer> cart = Map.of(product, 6);

        CyclicBarrier barrier = new CyclicBarrier(2);
        ExecutorService pool = Executors.newFixedThreadPool(2);

        Callable<Boolean> attempt1 = () -> {
            barrier.await(5, TimeUnit.SECONDS);
            try {
                orderServiceForThread1.createOrder(cart, null);
                return true;
            } catch (InsufficientStockException e) {
                return false;
            }
        };
        Callable<Boolean> attempt2 = () -> {
            barrier.await(5, TimeUnit.SECONDS);
            try {
                orderServiceForThread2.createOrder(cart, null);
                return true;
            } catch (InsufficientStockException e) {
                return false;
            }
        };

        Future<Boolean> result1 = pool.submit(attempt1);
        Future<Boolean> result2 = pool.submit(attempt2);

        boolean order1Passed = result1.get(5, TimeUnit.SECONDS);
        boolean order2Passed = result2.get(5, TimeUnit.SECONDS);
        pool.shutdown();

        assertTrue(order1Passed && order2Passed,
                "Both concurrent orders passed stock validation despite combined demand (12) "
                        + "exceeding the truly available stock (10) - this confirms the check-then-act "
                        + "race named in TID sec1.5/sec1.8. Real deduction happens later in "
                        + "OrderProcessingMDB, so nothing here has yet prevented an oversell.");
    }
}
