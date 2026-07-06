package lk.techmart.controller;

import lk.techmart.ejb.MetricsBean;
import lk.techmart.ejb.NotificationServiceBean;
import lk.techmart.ejb.OrderServiceBean;
import lk.techmart.model.Notification;
import lk.techmart.model.Order;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/transactions")
public class TransactionsServlet extends HttpServlet {

    private static final int RECENT_LIMIT = 30;

    @EJB
    private OrderServiceBean orderService;

    @EJB
    private NotificationServiceBean notificationService;

    @EJB
    private MetricsBean metrics;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        long start = System.currentTimeMillis();

        List<Order> orders = orderService.getRecentOrders(RECENT_LIMIT);
        List<Notification> notifications = notificationService.getRecentNotifications(RECENT_LIMIT);

        req.setAttribute("orders", orders);
        req.setAttribute("notifications", notifications);

        req.setAttribute("totalOrders", metrics.getTotalOrders());
        req.setAttribute("processedMessages", metrics.getProcessedMessages());
        req.setAttribute("avgOrderTime", metrics.getAverageProcessingTimes());
        req.setAttribute("totalProductFetchTime", metrics.getTotalProductFetchTime());

        req.getRequestDispatcher("/pages/transactions.jsp").forward(req, resp);

        long end = System.currentTimeMillis();
        System.out.println("Request Latency (" + req.getRequestURI() + "): " + (end - start) + " ms");
    }
}