package lk.techmart.ejb;

import lk.techmart.model.Notification;
import jakarta.ejb.AsyncResult;
import jakarta.ejb.Asynchronous;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;
import java.util.concurrent.Future;

@Stateless
public class NotificationServiceBean {

    @PersistenceContext(unitName = "techmartPU")
    private EntityManager entityManager;

    @Asynchronous
    public Future<String> sendOrderConfirmationEmail(Long orderId, double amount) {
        long start = System.currentTimeMillis();

        System.out.println("Notification Thread Started for Order ID: " + orderId);
        try {
            Thread.sleep(3000);
            System.out.println("Async email sent for order " + orderId);
        } catch (InterruptedException e) {
            System.err.println("Async notification execution interrupted!");
            Thread.currentThread().interrupt();
        }

        String statusMessage = "SUCCESS: Confirmation notification sent to customer for Order ID: " + orderId + " (Amount: $" + amount + ")";
        System.out.println(statusMessage);

        return new AsyncResult<>(statusMessage);
    }

    public List<Notification> getRecentNotifications(int limit) {
        return entityManager.createQuery(
                        "SELECT n FROM Notification n ORDER BY n.sentAt DESC", Notification.class)
                .setMaxResults(limit)
                .getResultList();
    }
}