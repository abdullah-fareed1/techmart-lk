package lk.techmart.ejb;

import lk.techmart.model.Product;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;

@Stateless
@jakarta.interceptor.Interceptors(PerformanceInterCeptor.class)
public class ProductServiceBean {

    @PersistenceContext(unitName = "techmartPU")
    private EntityManager entityManager;

    public List<Product> getAllProducts(){
        return entityManager.createQuery("SELECT p FROM Product p",Product.class).getResultList();
    }

    public Product getProductById(Long id) {
        return entityManager.find(Product.class, id);
    }
}

