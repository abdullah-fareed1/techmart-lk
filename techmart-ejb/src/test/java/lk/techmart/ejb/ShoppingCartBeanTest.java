package lk.techmart.ejb;

import lk.techmart.model.Product;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ShoppingCartBeanTest {

    @Test
    void addItem_newProduct_insertsItWithGivenQuantity() {
        ShoppingCartBean cart = new ShoppingCartBean();
        Product product = new Product();
        product.setId(1L);
        product.setName("Logitech MX Master 3S Mouse");

        cart.addItem(product, 3);

        assertEquals(3, cart.getCart().get(product));
    }

    @Test
    void addItem_sameProductTwice_accumulatesQuantityRatherThanOverwriting() {
        ShoppingCartBean cart = new ShoppingCartBean();
        Product product = new Product();
        product.setId(1L);
        product.setName("Logitech MX Master 3S Mouse");

        cart.addItem(product, 2);
        cart.addItem(product, 5);

        assertEquals(7, cart.getCart().get(product));
    }

    @Test
    void addItem_differentProducts_trackedIndependently() {
        ShoppingCartBean cart = new ShoppingCartBean();
        Product mouse = new Product();
        mouse.setId(1L);
        Product keyboard = new Product();
        keyboard.setId(2L);

        cart.addItem(mouse, 1);
        cart.addItem(keyboard, 4);

        assertEquals(1, cart.getCart().get(mouse));
        assertEquals(4, cart.getCart().get(keyboard));
    }

    @Test
    void clear_afterItemsAdded_emptiesTheCart() {
        ShoppingCartBean cart = new ShoppingCartBean();
        Product product = new Product();
        product.setId(1L);
        cart.addItem(product, 2);

        cart.clear();

        assertTrue(cart.getCart().isEmpty(),
                "clear() (the @Remove lifecycle method PlaceOrderServlet now calls on successful "
                        + "checkout) should leave no items behind");
    }
}