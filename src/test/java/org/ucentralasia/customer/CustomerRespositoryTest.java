package org.ucentralasia.customer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationContextFactory;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.ApplicationContext;
import org.ucentralasia.AbstractTestcontainersUnitTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CustomerRespositoryTest extends AbstractTestcontainersUnitTest {

    @Autowired
    private CustomerRespository underTest;
    @Autowired
    private ApplicationContext applicationContext;

    @BeforeEach
    void setUp() {
        underTest.deleteAll();
        System.out.println(applicationContext.getBeanDefinitionCount());
    }

    @Test
    void existsCustomerByEmail() {
        Customer customer = randomCustomer();
        underTest.save(customer);
        boolean b = underTest.existsCustomerByEmail(customer.getEmail());
        assertThat(b).isTrue();
    }

    @Test
    void existsCustomerById() {
        Customer customer = randomCustomer();
        underTest.save(customer);
        long id = underTest.findAll()
                .stream()
                .filter(c -> c.getEmail().equals(customer.getEmail()))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        boolean b = underTest.existsCustomerById(id);
        assertThat(b).isTrue();
    }
}