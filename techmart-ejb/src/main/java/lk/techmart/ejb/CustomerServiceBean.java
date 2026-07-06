package lk.techmart.ejb;

import lk.techmart.model.Customer;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;

@Stateless
public class CustomerServiceBean {

    @PersistenceContext(unitName = "techmartPU")
    private EntityManager entityManager;

    public List<Customer> getAllCustomers() {
        return entityManager.createQuery("SELECT c FROM Customer c ORDER BY c.name", Customer.class)
                .getResultList();
    }

    public Customer getCustomerById(Long id) {
        return entityManager.find(Customer.class, id);
    }
}