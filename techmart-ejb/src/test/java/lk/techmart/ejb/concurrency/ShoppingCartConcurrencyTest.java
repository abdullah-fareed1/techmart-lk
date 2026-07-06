package lk.techmart.ejb.concurrency;

import lk.techmart.ejb.ShoppingCartBean;
import lk.techmart.model.Product;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ShoppingCartConcurrencyTest {

    @Test
    void concurrentAddItem_onASharedCartInstance_noLongerLosesUpdates() throws InterruptedException {
        ShoppingCartBean cart = new ShoppingCartBean();
        Product product = new Product();
        product.setId(1L);

        int threadCount = 8;
        int incrementsPerThread = 2000;
        int expectedTotal = threadCount * incrementsPerThread;

        ExecutorService pool = Executors.newFixedThreadPool(threadCount);
        CountDownLatch ready = new CountDownLatch(threadCount);
        CountDownLatch start = new CountDownLatch(1);

        for (int t = 0; t < threadCount; t++) {
            pool.submit(() -> {
                ready.countDown();
                try {
                    start.await();
                } catch (InterruptedException ignored) {
                    Thread.currentThread().interrupt();
                }
                for (int i = 0; i < incrementsPerThread; i++) {
                    cart.addItem(product, 1);
                }
            });
        }

        ready.await();
        start.countDown();
        pool.shutdown();
        pool.awaitTermination(30, TimeUnit.SECONDS);

        Integer actualTotal = cart.getCart().get(product);
        System.out.println("Expected quantity: " + expectedTotal + " | Actual quantity: " + actualTotal);

        assertEquals(expectedTotal, actualTotal,
                "Regression check for the T-20 defect (TID Section 1.4/Section 2.7): "
                        + "ConcurrentHashMap.merge() should serialise per-key updates with no lost writes.");
    }
}