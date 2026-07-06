package lk.techmart.controller;

import lk.techmart.ejb.CustomerServiceBean;
import lk.techmart.ejb.MetricsBean;
import lk.techmart.ejb.ProductServiceBean;
import lk.techmart.ejb.ShoppingCartBean;
import lk.techmart.model.Customer;
import lk.techmart.model.Product;
import jakarta.ejb.EJB;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet("/pos")
public class PosServlet extends HttpServlet {

    @EJB
    private ProductServiceBean productService;

    @EJB
    private CustomerServiceBean customerService;

    @EJB
    private MetricsBean metrics;

    @Inject
    private ShoppingCartBean cart;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        long start = System.currentTimeMillis();

        List<Product> products = productService.getAllProducts();
        List<Customer> customers = customerService.getAllCustomers();
        Map<Product, Integer> currentCart = cart.getCart();

        req.setAttribute("products", products);
        req.setAttribute("customers", customers);
        req.setAttribute("cart", currentCart);

        req.setAttribute("totalOrders", metrics.getTotalOrders());
        req.setAttribute("processedMessages", metrics.getProcessedMessages());
        req.setAttribute("avgOrderTime", metrics.getAverageProcessingTimes());
        req.setAttribute("totalProductFetchTime", metrics.getTotalProductFetchTime());

        req.getRequestDispatcher("/pages/pos.jsp").forward(req, resp);

        long end = System.currentTimeMillis();
        System.out.println("⏱ Request Latency (" + req.getRequestURI() + "): " + (end - start) + " ms");
    }
}