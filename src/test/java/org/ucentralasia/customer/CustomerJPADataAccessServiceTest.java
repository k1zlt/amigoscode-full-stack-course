package org.ucentralasia.customer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

class CustomerJPADataAccessServiceTest {

    private CustomerJPADataAccessService underTest;
    private AutoCloseable autoCloseable;
    @Mock
    private CustomerRespository customerRespository;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        underTest = new CustomerJPADataAccessService(customerRespository);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void selectAllCustomers() {
        underTest.selectAllCustomers();

        verify(customerRespository).findAll();
    }

    @Test
    void selectCustomerById() {
        long id = 1;
        underTest.selectCustomerById(id);
        verify(customerRespository).findById((int) id);
    }

    @Test
    void insertCustomer() {
        Customer c = new Customer((long) 1, "Firuz ", "Firuz Azizbekov",122 );
        underTest.insertCustomer(c);
        verify(customerRespository).save(c);
    }

    @Test
    void existsCustomerWithEmail() {
        underTest.existsCustomerWithEmail("firuz.azi@ga");
        verify(customerRespository).existsCustomerByEmail("firuz.azi@ga");
    }

    @Test
    void existsCustomerWithId() {
        underTest.existsCustomerWithId((long)3);
        verify(customerRespository).existsCustomerById((long)3);
    }

    @Test
    void deleteCustomerById() {
        underTest.deleteCustomerById((long)3);
        verify(customerRespository).deleteById(3);
    }

    @Test
    void updateCustomer() {
        Customer customer = new Customer((long) 3,"Firuz", "Azizbekov", 19);
        underTest.updateCustomer(customer);
        verify(customerRespository).save(customer);
    }
}