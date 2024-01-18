package org.ucentralasia.customer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.ucentralasia.AbstractTestcontainersUnitTest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class CustomerJDBCDataAccessServiceTest extends AbstractTestcontainersUnitTest {

    private CustomerJDBCDataAccessService customerJDBCDataAccessServiceUnderTest;
    private final CustomerRowMapper customerRowMapper = new CustomerRowMapper();

    @BeforeEach
    void setUp() {
        customerJDBCDataAccessServiceUnderTest = new CustomerJDBCDataAccessService(
                getJdbcTemplate(),
                customerRowMapper
        );
    }

    @Test
    void willReturnEmptyWhenSelectCustomerById() {
        assertThat(customerJDBCDataAccessServiceUnderTest.selectCustomerById((long)-1)).isEmpty();
    }

    @Test
    void selectAllCustomers() {
        Customer customer =  randomCustomer();
        customerJDBCDataAccessServiceUnderTest.insertCustomer(customer);
        List<Customer> customers = customerJDBCDataAccessServiceUnderTest.selectAllCustomers();
        assertThat(customers).isNotEmpty();
    }

    @Test
    void selectCustomerById() {
        Customer customer = randomCustomer();
        customerJDBCDataAccessServiceUnderTest.insertCustomer(customer);
        Long id = customerJDBCDataAccessServiceUnderTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(customer.getEmail()))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();
        Optional<Customer> actual = customerJDBCDataAccessServiceUnderTest.selectCustomerById(id);
        assertThat(actual).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(id);
            assertThat(c.getName()).isEqualTo(customer.getName());
            assertThat(c.getEmail()).isEqualTo(customer.getEmail());
            assertThat(c.getAge()).isEqualTo(customer.getAge());
        });
    }

    @Test
    void insertCustomer() {
        Customer customer = randomCustomer();
        customerJDBCDataAccessServiceUnderTest.insertCustomer(customer);
        assertThat(customerJDBCDataAccessServiceUnderTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(customer.getEmail()) &&
                        c.getName().equals(customer.getName()) &&
                        c.getAge().equals(customer.getAge()))
                .findFirst()).isPresent();
    }

    @Test
    void existsCustomerWithEmail() {
        Customer customer = randomCustomer();
        customerJDBCDataAccessServiceUnderTest.insertCustomer(customer);
        assertThat(
                customerJDBCDataAccessServiceUnderTest.selectAllCustomers()
                        .stream()
                        .filter(c -> c.getEmail().equals(customer.getEmail()))
        ).isNotEmpty();
    }

    @Test
    void existsCustomerWithId() {
        Customer customer = randomCustomer();
        customerJDBCDataAccessServiceUnderTest.insertCustomer(customer);
        assertThat(
                customerJDBCDataAccessServiceUnderTest.selectAllCustomers()
                        .stream()
                        .filter(c -> c.getEmail().equals(customer.getEmail()))
                        .map(Customer::getId)
                        .findFirst()
                        .isPresent()
        ).isTrue();
    }

    @Test
    void willReturnFalseWhenExistCustomerById() {
        assertThat(
                customerJDBCDataAccessServiceUnderTest.selectCustomerById((long)-1).isPresent()
        ).isFalse();
    }

    @Test
    void deleteCustomerById() {
        Customer customer = randomCustomer();
        customerJDBCDataAccessServiceUnderTest.insertCustomer(customer);
        Customer customerFromDB = customerJDBCDataAccessServiceUnderTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(customer.getEmail()))
                .findFirst()
                .orElseThrow();
        customerJDBCDataAccessServiceUnderTest.deleteCustomerById(customerFromDB.getId());
        assertThat(
                customerJDBCDataAccessServiceUnderTest
                        .selectCustomerById(customerFromDB.getId())
                        .isEmpty()
        ).isTrue();
    }

    @Test
    void updateCustomer() {
        Customer customer = randomCustomer();
        customerJDBCDataAccessServiceUnderTest.insertCustomer(customer);
        Customer customerFromDB = customerJDBCDataAccessServiceUnderTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(customer.getEmail()))
                .findFirst()
                .orElseThrow();
        customerFromDB.setAge(0);
        customerJDBCDataAccessServiceUnderTest.updateCustomer(customerFromDB);
        Customer updatedCustomer = customerJDBCDataAccessServiceUnderTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(customer.getEmail()))
                .findFirst()
                .orElseThrow();
        assertThat(updatedCustomer.getAge()).isEqualTo(0);
    }

    @Test
    void existsPersonReturnFalseWhenEmailIsNotPresent() {
        String randomEmail = FAKER.internet().safeEmailAddress()+UUID.randomUUID();
        assertThat(customerJDBCDataAccessServiceUnderTest.existsCustomerWithEmail(randomEmail)).isFalse();
    }
}