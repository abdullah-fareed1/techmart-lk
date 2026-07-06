package lk.techmart.ejb;

import lk.techmart.model.Customer;
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
class CustomerServiceBeanTest {

    @Mock
    private EntityManager entityManager;

    private CustomerServiceBean customerService;

    @BeforeEach
    void setUp() {
        customerService = new CustomerServiceBean();
        EjbTestUtils.setField(customerService, "entityManager", entityManager);
    }

    @SuppressWarnings("unchecked")
    @Test
    void getAllCustomers_returnsRowsAsProducedByTheOrderByNameQuery() {
        Customer c1 = new Customer();
        c1.setId(1L);
        c1.setName("Ashan Fernando");
        Customer c2 = new Customer();
        c2.setId(2L);
        c2.setName("Nimal Perera");

        TypedQuery<Customer> query = mock(TypedQuery.class);
        when(entityManager.createQuery("SELECT c FROM Customer c ORDER BY c.name", Customer.class))
                .thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(c1, c2));

        List<Customer> result = customerService.getAllCustomers();

        assertEquals(2, result.size());
        assertEquals("Ashan Fernando", result.get(0).getName());
        verify(entityManager).createQuery("SELECT c FROM Customer c ORDER BY c.name", Customer.class);
    }

    @Test
    void getCustomerById_returnsNullForNonExistentId() {
        when(entityManager.find(Customer.class, 999L)).thenReturn(null);

        Customer result = customerService.getCustomerById(999L);

        assertNull(result);
    }

    @Test
    void getCustomerById_returnsCustomerWhenFound() {
        Customer c = new Customer();
        c.setId(1L);
        c.setName("Ashan Fernando");
        when(entityManager.find(Customer.class, 1L)).thenReturn(c);

        Customer result = customerService.getCustomerById(1L);

        assertNotNull(result);
        assertEquals("Ashan Fernando", result.getName());
    }
}
