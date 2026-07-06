package lk.techmart.ejb;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InsufficientStockExceptionTest {

    @Test
    void constructor_populatesFieldsAndMessage() {
        InsufficientStockException ex =
                new InsufficientStockException("Mechanical Gaming Keyboard RGB", 50, 12L);

        assertEquals("Mechanical Gaming Keyboard RGB", ex.getProductName());
        assertEquals(50, ex.getRequestedQuantity());
        assertEquals(12L, ex.getAvailableQuantity());

        assertTrue(ex.getMessage().contains("Mechanical Gaming Keyboard RGB"));
        assertTrue(ex.getMessage().contains("50"));
        assertTrue(ex.getMessage().contains("12"));
    }

    @Test
    void isAnUncheckedException() {
        InsufficientStockException ex = new InsufficientStockException("Item", 1, 0L);
        assertTrue(ex instanceof RuntimeException);
    }
}
