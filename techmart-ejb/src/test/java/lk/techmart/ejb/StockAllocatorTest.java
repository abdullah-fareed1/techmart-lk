package lk.techmart.ejb;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StockAllocatorTest {

    @Test
    void singleWarehouse_fullQuantityDeductedFromThatWarehouse() {
        int[] shares = StockAllocator.computeShares(List.of(10), 5);
        assertArrayEquals(new int[]{5}, shares);
    }

    @Test
    void stockSplitAcrossWarehouses_deductionProportionalToEachShare() {
        int[] shares = StockAllocator.computeShares(List.of(80, 20), 10);
        assertArrayEquals(new int[]{8, 2}, shares);
    }

    @Test
    void unevenDivision_remainderLandsOnTheLargestStockWarehouse() {
        int[] shares = StockAllocator.computeShares(List.of(50, 30, 21), 7);
        assertArrayEquals(new int[]{4, 2, 1}, shares);
        assertEquals(7, shares[0] + shares[1] + shares[2], "total deducted must equal ordered qty exactly");
    }

    @Test
    void totalStockAcrossAllWarehousesIsZero_noDeductionAndNoException() {
        int[] shares = StockAllocator.computeShares(List.of(0, 0, 0), 5);
        assertArrayEquals(new int[]{0, 0, 0}, shares);
    }

    @Test
    void noInventoryRows_returnsEmptyArray() {
        int[] shares = StockAllocator.computeShares(List.of(), 5);
        assertArrayEquals(new int[]{}, shares);
    }

    @Test
    void deductionNeverExceedsAWarehousesOwnQuantity_evenWithExtremeSkew() {

        int[] shares = StockAllocator.computeShares(List.of(1, 999), 500);
        assertTrue(shares[0] <= 1, "warehouse 0 only has 1 unit, deduction must be clamped to at most 1");
        assertTrue(shares[1] <= 999, "warehouse 1 deduction must be clamped to at most its own stock");
    }
}
