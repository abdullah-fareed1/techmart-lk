package lk.techmart.ejb;

import lk.techmart.model.Customer;
import lk.techmart.model.Order;
import lk.techmart.model.Product;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceBeanTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private NotificationServiceBean notificationService;

    private OrderServiceBean orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderServiceBean();
        EjbTestUtils.setField(orderService, "entityManager", entityManager);
        EjbTestUtils.setField(orderService, "notificationService", notificationService);
    }

    @SuppressWarnings("unchecked")
    private void stubStockQuery(Long productId, long availableQty) {
        TypedQuery<Number> query = mock(TypedQuery.class);
        when(entityManager.createQuery(
                "SELECT COALESCE(SUM(i.quantity), 0) FROM Inventory i WHERE i.productId = :pid", Number.class))
                .thenReturn(query);
        when(query.setParameter("pid", productId)).thenReturn(query);
        when(query.getSingleResult()).thenReturn(availableQty);
    }

    @Test
    void createOrder_sufficientStock_persistsOrderPendingWithCorrectTotalAndTriggersNotification() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Dell XPS 13 Laptop");
        product.setPrice(899.99);

        stubStockQuery(1L, 50L);

        Customer customer = new Customer();
        customer.setId(10L);
        customer.setName("Nimal Perera");
        when(entityManager.find(Customer.class, 10L)).thenReturn(customer);

        doAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(500L);
            return null;
        }).when(entityManager).persist(any(Order.class));

        Map<Product, Integer> cart = new LinkedHashMap<>();
        cart.put(product, 2);

        Order result = orderService.createOrder(cart, 10L);

        assertNotNull(result);
        assertEquals("PENDING", result.getStatus());
        assertEquals(1799.98, result.getTotalAmount(), 0.001);
        assertEquals(customer, result.getCustomer());
        assertEquals(1, result.getItems().size());

        verify(entityManager).persist(any(Order.class));
        verify(entityManager).flush();

        ArgumentCaptor<Double> amountCaptor = ArgumentCaptor.forClass(Double.class);
        verify(notificationService).sendOrderConfirmationEmail(eq(500L), amountCaptor.capture());
        assertEquals(1799.98, amountCaptor.getValue(), 0.001);
    }

    @Test
    void createOrder_requestedQtyExceedsSummedWarehouseStock_throwsAndPersistsNothing() {
        Product product = new Product();
        product.setId(2L);
        product.setName("Sony WH-1000XM5 Headphones");
        product.setPrice(349.00);

        stubStockQuery(2L, 3L);

        Map<Product, Integer> cart = new LinkedHashMap<>();
        cart.put(product, 10);

        InsufficientStockException ex = assertThrows(InsufficientStockException.class,
                () -> orderService.createOrder(cart, 10L));

        assertEquals("Sony WH-1000XM5 Headphones", ex.getProductName());
        assertEquals(10, ex.getRequestedQuantity());
        assertEquals(3L, ex.getAvailableQuantity());

        verify(entityManager, never()).persist(any(Order.class));
        verifyNoInteractions(notificationService);
    }

    @SuppressWarnings("unchecked")
    @Test
    void getRecentOrders_appliesTheGivenLimit() {
        TypedQuery<Order> query = mock(TypedQuery.class);
        when(entityManager.createQuery("SELECT o FROM Order o ORDER BY o.orderDate DESC", Order.class))
                .thenReturn(query);
        when(query.setMaxResults(5)).thenReturn(query);
        Order o1 = new Order();
        Order o2 = new Order();
        when(query.getResultList()).thenReturn(List.of(o1, o2));

        List<Order> result = orderService.getRecentOrders(5);

        assertEquals(2, result.size());
        verify(query).setMaxResults(5);
    }
}
