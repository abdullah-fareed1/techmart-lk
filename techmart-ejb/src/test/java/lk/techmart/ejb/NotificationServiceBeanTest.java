package lk.techmart.ejb;

import jakarta.ejb.AsyncResult;
import lk.techmart.model.Notification;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceBeanTest {

    @Mock
    private EntityManager entityManager;

    private NotificationServiceBean notificationService;

    @BeforeEach
    void setUp() {
        notificationService = new NotificationServiceBean();
        EjbTestUtils.setField(notificationService, "entityManager", entityManager);
    }

    @Test
    void sendOrderConfirmationEmail_returnsSuccessMessageWithOrderIdAndAmount() throws Exception {
        Future<String> future = notificationService.sendOrderConfirmationEmail(55L, 199.99);

        String result = (String) EjbTestUtils.findFirstNonNullField(future);

        assertTrue(result.contains("SUCCESS"));
        assertTrue(result.contains("55"));
        assertTrue(result.contains("199.99"));
    }

    @SuppressWarnings("unchecked")
    @Test
    void getRecentNotifications_appliesTheGivenLimit() {
        Notification n1 = new Notification();
        Notification n2 = new Notification();

        TypedQuery<Notification> query = mock(TypedQuery.class);
        when(entityManager.createQuery("SELECT n FROM Notification n ORDER BY n.sentAt DESC", Notification.class))
                .thenReturn(query);
        when(query.setMaxResults(10)).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(n1, n2));

        List<Notification> result = notificationService.getRecentNotifications(10);

        assertEquals(2, result.size());
        verify(query).setMaxResults(10);
    }
}
