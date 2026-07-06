package lk.techmart.ejb;


import jakarta.ejb.EJB;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.InvocationContext;

public class PerformanceInterCeptor {

    @EJB
    private  MetricsBean metrics;

    @AroundInvoke
    public Object interceptPerformance(InvocationContext context) throws Exception{
        long startTime = System.currentTimeMillis();

        try {
            return  context.proceed();
        }finally {
            long executionTime = System.currentTimeMillis() - startTime;
            String methodName = context.getMethod().getName();
            String className =  context.getTarget().getClass().getSimpleName();

            System.out.println("⏱️ [METRIC] " + className + "." + methodName + " executed in " + executionTime + " ms");

            if (methodName.equals("createOrder")){
                metrics.addProcessingTime(executionTime);
            } else if (methodName.equals("getAllProducts") || methodName.equals("getProductById")) {
                metrics.addProductFetchTime(executionTime);
            }
        }
    }
}
