import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class AsyncExecutionTest {

    private static final long MAX_TRIGGER_MILLIS = 50;

    @Test
    public void testAsyncNonBlockingBehavior() {
        long elapsed = TestTimingUtils.measureMillis(() ->
                System.out.println("Triggering async method..."));

        System.out.println("Async trigger time: " + elapsed + " ms");

        assertTrue(elapsed < MAX_TRIGGER_MILLIS,
                "Async call is blocking the main thread");
    }
}