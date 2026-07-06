package lk.techmart.controller;

import lk.techmart.ejb.MetricsBean;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/metrics")
public class MetricsServlet extends HttpServlet {
    @EJB
    private MetricsBean metrics;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        long start = System.currentTimeMillis();

        req.setAttribute("totalOrders", metrics.getTotalOrders());
        req.setAttribute("processedMessages", metrics.getProcessedMessages());
        req.setAttribute("avgOrderTime", metrics.getAverageProcessingTimes());
        req.setAttribute("totalProductFetchTime", metrics.getTotalProductFetchTime());

        req.getRequestDispatcher("/pages/metrics.jsp").forward(req, resp);

        long end = System.currentTimeMillis();
        System.out.println("Request Latency (" + req.getRequestURI() + "): " + (end - start) + " ms");
    }
}