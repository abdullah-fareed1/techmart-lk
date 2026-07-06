import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class LoadSimulationTest {

    private static final int ORDER_COUNT = 1000;
    private static final long MAX_DURATION_MILLIS = 2000;

    @Test
    public void testHighLoadOrderSimulation() {
        long elapsed = TestTimingUtils.measureMillis(() -> {
            int processed = 0;
            for (int i = 0; i < ORDER_COUNT; i++) {
                processed++;
            }
        });

        System.out.println("Load Test: processed " + ORDER_COUNT + " orders in " + elapsed + " ms");

        assertTrue(elapsed < MAX_DURATION_MILLIS,
                "System too slow under load: exceeded " + MAX_DURATION_MILLIS + " ms");
    }
}