package lk.techmart.ejb;

import lk.techmart.model.Product;
import jakarta.ejb.Remove;
import jakarta.ejb.Stateful;
import jakarta.ejb.StatefulTimeout;
import jakarta.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Stateful
@StatefulTimeout(value = 30, unit = TimeUnit.MINUTES)
@SessionScoped
public class ShoppingCartBean implements Serializable {

    private final Map<Product, Integer> cart = new ConcurrentHashMap<>();

    public void addItem(Product product, int qty) {
        cart.merge(product, qty, Integer::sum);
    }

    public Map<Product, Integer> getCart() {
        return cart;
    }

    @Remove
    public void clear() {
        cart.clear();
    }
}