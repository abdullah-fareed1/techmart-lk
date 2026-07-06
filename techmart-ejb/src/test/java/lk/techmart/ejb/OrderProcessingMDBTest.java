package lk.techmart.ejb;

import jakarta.jms.JMSException;
import lk.techmart.model.Inventory;
import lk.techmart.model.Notification;
import lk.techmart.model.Order;
import lk.techmart.model.OrderItem;
import lk.techmart.model.Product;
import lk.techmart.model.Warehouse;
import jakarta.jms.TextMessage;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderProcessingMDBTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private MetricsBean metrics;

    @Mock
    private TextMessage textMessage;

    private OrderProcessingMDB mdb;

    @BeforeEach
    void setUp() {
        mdb = new OrderProcessingMDB();
        EjbTestUtils.setField(mdb, "entityManager", entityManager);
        EjbTestUtils.setField(mdb, "metrics", metrics);
    }

    @SuppressWarnings("unchecked")
    @Test
    void onMessage_validOrderId_setsStatusConfirmedDeductsStockAndPersistsNotification() throws Exception {
        Product product = new Product();
        product.setId(7L);
        product.setName("Kingston 1TB NVMe SSD");

        OrderItem item = new OrderItem();
        item.setProduct(product);
        item.setQuantity(3);

        Order order = new Order();
        order.setId(42L);
        order.setStatus("PENDING");
        order.setItems(List.of(item));

        when(textMessage.getText()).thenReturn("42");
        when(entityManager.find(Order.class, 42L)).thenReturn(order);

        Warehouse warehouse = new Warehouse();
        warehouse.setId(1L);
        Inventory inventory = new Inventory();
        inventory.setId(100L);
        inventory.setProductId(7L);
        inventory.setWarehouse(warehouse);
        inventory.setQuantity(10);

        TypedQuery<Inventory> inventoryQuery = mock(TypedQuery.class);
        when(entityManager.createQuery("SELECT i FROM Inventory i WHERE i.productId = :pid", Inventory.class))
                .thenReturn(inventoryQuery);
        when(inventoryQuery.setParameter("pid", 7L)).thenReturn(inventoryQuery);
        when(inventoryQuery.setLockMode(LockModeType.PESSIMISTIC_WRITE)).thenReturn(inventoryQuery);
        when(inventoryQuery.getResultList()).thenReturn(List.of(inventory));

        mdb.onMessage(textMessage);

        assertEquals("CONFIRMED", order.getStatus());
        assertEquals(7, inventory.getQuantity(),
                "single-warehouse product should have the full ordered qty (3) deducted from 10");

        ArgumentCaptor<Notification> notificationCaptor = ArgumentCaptor.forClass(Notification.class);
        verify(entityManager).persist(notificationCaptor.capture());
        Notification persisted = notificationCaptor.getValue();
        assertEquals("SUCCESS", persisted.getStatus());
        assertEquals(order, persisted.getOrder());
        assertTrue(persisted.getMessage().contains("42"));

        verify(metrics).incrementMessages();
        verify(metrics).addProcessingTime(anyLong());
    }

    @Test
    void onMessage_malformedOrderId_isCaughtAndOrderIsLeftUntouched() throws JMSException {
        when(textMessage.getText()).thenReturn("not-a-number");

        assertDoesNotThrow(() -> mdb.onMessage(textMessage));

        verify(entityManager, never()).find(any(), any());
        verify(entityManager, never()).persist(any());
        verify(metrics, never()).incrementMessages();
        verify(metrics).addProcessingTime(anyLong());
    }

    @SuppressWarnings("unchecked")
    @Test
    void onMessage_deductingStock_requestsPessimisticWriteLockOnInventoryRead() throws Exception {
        Product product = new Product();
        product.setId(9L);

        OrderItem item = new OrderItem();
        item.setProduct(product);
        item.setQuantity(1);

        Order order = new Order();
        order.setId(100L);
        order.setStatus("PENDING");
        order.setItems(List.of(item));

        when(textMessage.getText()).thenReturn("100");
        when(entityManager.find(Order.class, 100L)).thenReturn(order);

        Inventory inventory = new Inventory();
        inventory.setProductId(9L);
        inventory.setQuantity(5);

        TypedQuery<Inventory> inventoryQuery = mock(TypedQuery.class);
        when(entityManager.createQuery("SELECT i FROM Inventory i WHERE i.productId = :pid", Inventory.class))
                .thenReturn(inventoryQuery);
        when(inventoryQuery.setParameter("pid", 9L)).thenReturn(inventoryQuery);
        when(inventoryQuery.setLockMode(LockModeType.PESSIMISTIC_WRITE)).thenReturn(inventoryQuery);
        when(inventoryQuery.getResultList()).thenReturn(List.of(inventory));

        mdb.onMessage(textMessage);

        verify(inventoryQuery).setLockMode(LockModeType.PESSIMISTIC_WRITE);
    }
}