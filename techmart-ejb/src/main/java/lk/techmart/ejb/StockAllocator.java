package lk.techmart.ejb;

import java.util.List;

public final class StockAllocator {

    private StockAllocator() {
    }

    public static int[] computeShares(List<Integer> warehouseQuantities, int orderedQty) {
        int size = warehouseQuantities.size();
        int[] shares = new int[size];

        if (size == 0) {
            return shares;
        }

        int totalStock = warehouseQuantities.stream().mapToInt(Integer::intValue).sum();

        if (totalStock <= 0) {
            return shares;
        }

        int allocated = 0;
        int maxIdx = 0;
        int maxOriginalQty = -1;

        for (int idx = 0; idx < size; idx++) {
            int qty = warehouseQuantities.get(idx);
            int share = (int) Math.round(orderedQty * ((double) qty / totalStock));
            shares[idx] = share;
            allocated += share;

            if (qty > maxOriginalQty) {
                maxOriginalQty = qty;
                maxIdx = idx;
            }
        }

        int remainder = orderedQty - allocated;
        shares[maxIdx] += remainder;

        for (int idx = 0; idx < size; idx++) {
            int qty = warehouseQuantities.get(idx);
            shares[idx] = Math.max(0, Math.min(shares[idx], qty));
        }

        return shares;
    }
}
