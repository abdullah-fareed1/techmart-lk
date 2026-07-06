package lk.techmart.controller;

import lk.techmart.ejb.InsufficientStockException;
import lk.techmart.ejb.MetricsBean;
import lk.techmart.ejb.OrderServiceBean;
import lk.techmart.ejb.ShoppingCartBean;
import lk.techmart.model.Order;
import lk.techmart.model.Product;
import jakarta.annotation.Resource;
import jakarta.ejb.EJB;
import jakarta.inject.Inject;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSContext;
import jakarta.jms.Queue;
import jakarta.jms.TextMessage;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/placeOrder")
public class PlaceOrderServlet extends HttpServlet {

    @Inject
    private ShoppingCartBean cart;

    @EJB
    private OrderServiceBean orderService;

    @EJB
    private MetricsBean metrics;

    @Resource(lookup = "jms/TechmartConnectionFactory")
    private ConnectionFactory connectionFactory;

    @Resource(lookup = "jms/TechmartOrderQueue")
    private Queue orderQueue;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        long start = System.currentTimeMillis();
        System.out.println("PLACE ORDER SERVLET HIT");

        String customerIdParam = req.getParameter("customerId");
        if (customerIdParam == null || customerIdParam.isBlank()) {
            System.out.println("No customer selected! Redirecting back to POS with error flag...");
            resp.sendRedirect("pos?error=nocustomer");
            return;
        }
        Long customerId = Long.valueOf(customerIdParam);

        Map<Product, Integer> currentCart;
        synchronized (cart) {
            if (cart.getCart().isEmpty()) {
                System.out.println("Cart bean was empty! Redirecting back...");
                resp.sendRedirect("pos?error=emptycart");
                return;
            }
            currentCart = new HashMap<>(cart.getCart());
            cart.getCart().clear();
        }

        try {
            Order order = orderService.createOrder(currentCart, customerId);
            metrics.incrementOrders();

            try (JMSContext context = connectionFactory.createContext()) {
                TextMessage msg = context.createTextMessage(order.getId().toString());
                context.createProducer().send(orderQueue, msg);
                System.out.println("JMS MESSAGE SENT SUCCESSFULLY FOR ORDER ID: " + order.getId());
            } catch (Exception e) {
                System.err.println("JMS Execution failed!");
                e.printStackTrace();
            }
        } catch (InsufficientStockException e) {
            System.out.println("Order rejected: " + e.getMessage());
            synchronized (cart) {
                cart.getCart().putAll(currentCart);
            }
            String redirectUrl = "pos?error=stock"
                    + "&item=" + URLEncoder.encode(e.getProductName(), StandardCharsets.UTF_8)
                    + "&available=" + e.getAvailableQuantity();
            resp.sendRedirect(redirectUrl);
            return;
        } catch (Exception e) {
            System.err.println("Order Creation Failed inside Servlet!");
            e.printStackTrace();
            synchronized (cart) {
                cart.getCart().putAll(currentCart);
            }
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            return;
        }

        resp.sendRedirect("pos");
        long end = System.currentTimeMillis();
        System.out.println("Request Latency (" + req.getRequestURI() + "): " + (end - start) + " ms");
    }
}