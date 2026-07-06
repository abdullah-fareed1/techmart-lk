package lk.techmart.ejb;

public class InsufficientStockException extends RuntimeException {

    private final String productName;
    private final int requestedQuantity;
    private final long availableQuantity;

    public InsufficientStockException(String productName, int requestedQuantity, long availableQuantity) {
        super("Insufficient stock for '" + productName + "': requested " + requestedQuantity
                + ", only " + availableQuantity + " available.");
        this.productName = productName;
        this.requestedQuantity = requestedQuantity;
        this.availableQuantity = availableQuantity;
    }

    public String getProductName() {
        return productName;
    }

    public int getRequestedQuantity() {
        return requestedQuantity;
    }

    public long getAvailableQuantity() {
        return availableQuantity;
    }
}