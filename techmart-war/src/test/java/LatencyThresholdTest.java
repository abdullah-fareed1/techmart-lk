import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class LatencyThresholdTest {

    @ParameterizedTest(name = "{0}: {1} ms simulated work must stay under {2} ms")
    @CsvSource({
            "Database Query Latency, 40, 200",
            "MDB Processing Time, 30, 300",
            "Order Processing Latency (NFR), 50, 500"
    })
    public void testLatencyWithinThreshold(String scenario, long simulatedWorkMillis, long thresholdMillis) {
        long elapsed = TestTimingUtils.measureMillis(simulatedWorkMillis);

        System.out.println(scenario + ": " + elapsed + " ms (threshold " + thresholdMillis + " ms)");

        assertTrue(elapsed < thresholdMillis,
                scenario + " exceeded threshold: " + elapsed + " ms >= " + thresholdMillis + " ms");
    }
}