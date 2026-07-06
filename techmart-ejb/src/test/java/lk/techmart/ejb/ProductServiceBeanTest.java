package lk.techmart.ejb;

import lk.techmart.model.Product;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceBeanTest {

    @Mock
    private EntityManager entityManager;

    private ProductServiceBean productService;

    @BeforeEach
    void setUp() {
        productService = new ProductServiceBean();
        EjbTestUtils.setField(productService, "entityManager", entityManager);
    }

    @SuppressWarnings("unchecked")
    @Test
    void getAllProducts_returnsFullCatalogue() {
        Product p1 = new Product();
        p1.setId(1L);
        p1.setName("Dell XPS 13 Laptop");

        Product p2 = new Product();
        p2.setId(2L);
        p2.setName("Apple iPhone 15");

        TypedQuery<Product> query = mock(TypedQuery.class);
        when(entityManager.createQuery("SELECT p FROM Product p", Product.class)).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(p1, p2));

        List<Product> result = productService.getAllProducts();

        assertEquals(2, result.size());
        assertEquals("Dell XPS 13 Laptop", result.get(0).getName());
        verify(entityManager).createQuery("SELECT p FROM Product p", Product.class);
    }

    @Test
    void getProductById_returnsNullForNonExistentId() {
        when(entityManager.find(Product.class, 999L)).thenReturn(null);

        Product result = productService.getProductById(999L);

        assertNull(result);
        verify(entityManager).find(Product.class, 999L);
    }

    @Test
    void getProductById_returnsProductWhenFound() {
        Product p = new Product();
        p.setId(1L);
        p.setName("Dell XPS 13 Laptop");
        when(entityManager.find(Product.class, 1L)).thenReturn(p);

        Product result = productService.getProductById(1L);

        assertNotNull(result);
        assertEquals("Dell XPS 13 Laptop", result.getName());
    }
}
