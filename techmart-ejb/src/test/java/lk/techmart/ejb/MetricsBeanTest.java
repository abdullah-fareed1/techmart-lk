package lk.techmart.ejb;

import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MetricsBeanTest {

    @Test
    void getAverageProcessingTimes_whenNoMessagesProcessed_returnsZeroWithNoException() {
        MetricsBean metrics = new MetricsBean();
        assertEquals(0.0, metrics.getAverageProcessingTimes());
    }

    @Test
    void concurrentIncrementCalls_produceNoLostUpdates() throws InterruptedException {
        MetricsBean metrics = new MetricsBean();
        int threadCount = 16;
        int incrementsPerThread = 2000;

        ExecutorService pool = Executors.newFixedThreadPool(threadCount);
        CountDownLatch done = new CountDownLatch(threadCount);

        for (int t = 0; t < threadCount; t++) {
            pool.submit(() -> {
                try {
                    for (int i = 0; i < incrementsPerThread; i++) {
                        metrics.incrementOrders();
                        metrics.incrementMessages();
                    }
                } finally {
                    done.countDown();
                }
            });
        }

        assertTrueWithinTimeout(done);
        pool.shutdown();
        pool.awaitTermination(30, TimeUnit.SECONDS);

        int expected = threadCount * incrementsPerThread;
        assertEquals(expected, metrics.getTotalOrders(),
                "@Singleton + CONTAINER concurrency + synchronized methods should serialise correctly");
        assertEquals(expected, metrics.getProcessedMessages());
    }

    private static void assertTrueWithinTimeout(CountDownLatch latch) throws InterruptedException {
        if (!latch.await(30, TimeUnit.SECONDS)) {
            throw new AssertionError("Timed out waiting for concurrent increments to finish");
        }
    }
}
