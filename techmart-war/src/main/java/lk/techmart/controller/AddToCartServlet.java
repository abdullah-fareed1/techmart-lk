package lk.techmart.controller;

import lk.techmart.ejb.ProductServiceBean;
import lk.techmart.ejb.ShoppingCartBean;
import lk.techmart.model.Product;
import jakarta.ejb.EJB;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/addToCart")
public class AddToCartServlet extends HttpServlet {

    @EJB
    private ProductServiceBean productService;

    @Inject
    private ShoppingCartBean cart;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        long start = System.currentTimeMillis();
        Long productId = Long.valueOf(req.getParameter("productId"));
        int qty = Integer.parseInt(req.getParameter("qty"));

        Product p = productService.getProductById(productId);

        cart.addItem(p, qty);

        resp.sendRedirect("pos");

        long end = System.currentTimeMillis();

        System.out.println("Request Latency (" + req.getRequestURI() + "): " + (end - start) + " ms");
        System.out.println("Product added to Stateful Bean ID = " + productId + " | Qty = " + qty);
    }
}