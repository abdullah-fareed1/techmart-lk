package lk.techmart.ejb;

import jakarta.ejb.*;

@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class MetricsBean {


    private int totalOrders = 0;
    private int processedMessages = 0;

    private long totalProcessingTimes = 0;
    private long totalProductFetchTime = 0;

    public synchronized void incrementOrders() {
        totalOrders++;
    }

    public synchronized void incrementMessages() {
        processedMessages++;
    }

    public synchronized void addProcessingTime(long mills) {
        this.totalProcessingTimes += mills;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public synchronized void addProductFetchTime(long mills) {
        this.totalProductFetchTime += mills;
    }


    public int getTotalOrders() {
        return totalOrders;
    }

    public int getProcessedMessages() {
        return processedMessages;
    }

    public double getAverageProcessingTimes() {
        if (processedMessages == 0) return 0.0;
        return (double) totalProcessingTimes / processedMessages;
    }

    public long getTotalProductFetchTime() {
        return totalProductFetchTime;
    }

}

