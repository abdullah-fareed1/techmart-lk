public final class TestTimingUtils {

    private TestTimingUtils() {
    }

    public static long measureMillis(Runnable action) {
        long start = System.nanoTime();
        action.run();
        return (System.nanoTime() - start) / 1_000_000;
    }

    public static long measureMillis(long simulatedWorkMillis) {
        return measureMillis(() -> sleepQuietly(simulatedWorkMillis));
    }

    private static void sleepQuietly(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}