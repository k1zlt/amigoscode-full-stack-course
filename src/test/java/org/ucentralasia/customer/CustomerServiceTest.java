package org.ucentralasia.customer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ucentralasia.customer.exception.RequestValidationException;
import org.ucentralasia.exception.DublicateResourceException;
import org.ucentralasia.exception.ResourseNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;


import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {
    @Mock
    private CustomerDao customerDao;
    private CustomerService customerServiceUnderTest;

    @BeforeEach
    void setUp() {
        customerServiceUnderTest = new CustomerService(customerDao);
    }

    @Test
    void getAllCustomers() {
        customerServiceUnderTest.getAllCustomers();
        verify(customerDao).selectAllCustomers();
    }

    @Test
    void canGetCustomer() {
        long id = 3;
        Customer customer = new Customer((long)3, "Alex", "Alex@gmail.com", 22);
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));
        Customer actual = customerServiceUnderTest.getCustomer((long) 3);
        assertThat(actual).isEqualTo(customer);
    }
    @Test
    void willThrowWhenGetCustomerReturnEmptyOptional() {
        long id = 3;
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> customerServiceUnderTest.getCustomer(id))
                .isInstanceOf(ResourseNotFoundException.class)
                .hasMessageContaining(
                        "Customer with id [%s] doesnt exist.".formatted(id)
                );
    }

    @Test
    void addCustomer() {
        long id = 10;
        String email = "alex@gmail.com";
        when(customerDao.existsCustomerWithEmail(email)).thenReturn(false);
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                "Alex",
                email,
                19
        );
        customerServiceUnderTest.addCustomer(request);
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerDao).insertCustomer(customerArgumentCaptor.capture());
        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getId()).isNull();
        assertThat(capturedCustomer.getName()).isEqualTo(request.name());
        assertThat(capturedCustomer.getEmail()).isEqualTo(request.email());
        assertThat(capturedCustomer.getAge()).isEqualTo(request.age());
    }
    @Test
    void willThrowWhenEmailExistsWhileAddingACustomer() {
        long id = 10;
        String email = "alex@gmail.com";
        when(customerDao.existsCustomerWithEmail(email)).thenReturn(true);
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                "Alex",
                email,
                19
        );
        assertThatThrownBy(() -> customerServiceUnderTest.addCustomer(request))
                .isInstanceOf(DublicateResourceException.class)
                .hasMessageContaining(
                        "Customer with email [%s] already exists.".formatted(email)
                );
        verify(customerDao, never()).insertCustomer(any());
    }

    @Test
    void deleteCustomer() {
        long id = 3;
        when(customerDao.existsCustomerWithId(id)).thenReturn(true);
        customerServiceUnderTest.deleteCustomer(id);
        verify(customerDao).deleteCustomerById(id);
    }

    @Test
    void ThrowsErrorWhenCustomerNotFound() {
        long id = -1;
        when(customerDao.existsCustomerWithId(id)).thenReturn(false);
        assertThatThrownBy(() -> customerServiceUnderTest.deleteCustomer(id))
                .isInstanceOf(ResourseNotFoundException.class)
                .hasMessageContaining(
                        "Customer with id [%s] not found".formatted(id)
                );
        verify(customerDao, never()).deleteCustomerById(any());
    }

    @Test
    void canUpdateAllCustomerProperties() {
        long id = 2;
        Customer customer = new Customer(id, "Alex", "alex@gmail.com", 19);
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));
        CustomerUpdateRequest request = new CustomerUpdateRequest(
                "Alexandr",
                "alexandr@gmail.com",
                23);
        when(customerDao.existsCustomerWithEmail(request.email())).thenReturn(false);
        customerServiceUnderTest.updateCustomer(id, request);
        ArgumentCaptor<Customer> argumentCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerDao).updateCustomer(argumentCaptor.capture());
        Customer capturedCustomer = argumentCaptor.getValue();
        assertThat(capturedCustomer.getId()).isEqualTo(id);
        assertThat(capturedCustomer.getName()).isEqualTo(request.name());
        assertThat(capturedCustomer.getEmail()).isEqualTo(request.email());
        assertThat(capturedCustomer.getAge()).isEqualTo(request.age());
    }
    @Test
    void canOnlyCustomerName() {
        long id = 2;
        Customer customer = new Customer(id, "Alex", "alex@gmail.com", 19);
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));
        CustomerUpdateRequest request = new CustomerUpdateRequest(
                "Alexandr",
                null,
                null);
        customerServiceUnderTest.updateCustomer(id, request);
        ArgumentCaptor<Customer> argumentCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerDao).updateCustomer(argumentCaptor.capture());
        Customer capturedCustomer = argumentCaptor.getValue();
        assertThat(capturedCustomer.getId()).isEqualTo(id);
        assertThat(capturedCustomer.getName()).isEqualTo(request.name());
        assertThat(capturedCustomer.getEmail()).isEqualTo(customer.getEmail());
        assertThat(capturedCustomer.getAge()).isEqualTo(customer.getAge());
    }
    @Test
    void canUpdateEmail() {
        long id = 2;
        Customer customer = new Customer(id, "Alex", "alex@gmail.com", 19);
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));
        CustomerUpdateRequest request = new CustomerUpdateRequest(
                null,
                "alexandr@gmail.com",
                null);
        when(customerDao.existsCustomerWithEmail(request.email())).thenReturn(false);
        customerServiceUnderTest.updateCustomer(id, request);
        ArgumentCaptor<Customer> argumentCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerDao).updateCustomer(argumentCaptor.capture());
        Customer capturedCustomer = argumentCaptor.getValue();
        assertThat(capturedCustomer.getId()).isEqualTo(id);
        assertThat(capturedCustomer.getName()).isEqualTo(customer.getName());
        assertThat(capturedCustomer.getEmail()).isEqualTo(request.email());
        assertThat(capturedCustomer.getAge()).isEqualTo(customer.getAge());
    }
    @Test
    void canUpdateAge() {
        long id = 2;
        Customer customer = new Customer(id, "Alex", "alex@gmail.com", 19);
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));
        CustomerUpdateRequest request = new CustomerUpdateRequest(
                null,
                null,
                22);
        customerServiceUnderTest.updateCustomer(id, request);
        ArgumentCaptor<Customer> argumentCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerDao).updateCustomer(argumentCaptor.capture());
        Customer capturedCustomer = argumentCaptor.getValue();
        assertThat(capturedCustomer.getId()).isEqualTo(id);
        assertThat(capturedCustomer.getName()).isEqualTo(customer.getName());
        assertThat(capturedCustomer.getEmail()).isEqualTo(customer.getEmail());
        assertThat(capturedCustomer.getAge()).isEqualTo(request.age());
    }
    @Test
    void ThrowsErrorWhenNoChangesDetected() {
        long id = 2;
        Customer customer = new Customer(id, "Alex", "alex@gmail.com", 19);
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));
        CustomerUpdateRequest request = new CustomerUpdateRequest(
                customer.getName(),
                customer.getEmail(),
                customer.getAge());
        assertThatThrownBy(() -> customerServiceUnderTest.updateCustomer(id, request))
                .isInstanceOf(RequestValidationException.class)
                .hasMessageContaining(
                        "No changes provided."
                );
        verify(customerDao, never()).updateCustomer(any());
    }

    @Test
    void willThrowWhenTryingToUpdateCustomerEmailWhenAlreadyTaken() {
        long id = 2;
        Customer customer = new Customer(id, "Alex", "alex@gmail.com", 19);
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));
        CustomerUpdateRequest request = new CustomerUpdateRequest(
                "Alexandr",
                "alex1@gmail.com",
                23);
        when(customerDao.existsCustomerWithEmail(request.email())).thenReturn(true);
//        customerServiceUnderTest.updateCustomer(id, request);
        assertThatThrownBy(() -> customerServiceUnderTest.updateCustomer(id, request))
                .isInstanceOf(DublicateResourceException.class)
                .hasMessage(
                        "email already taken"
                );
        verify(customerDao, never()).updateCustomer(any());
    }
}