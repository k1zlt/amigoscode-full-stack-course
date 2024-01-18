package org.ucentralasia.customer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.ucentralasia.customer.exception.RequestValidationException;
import org.ucentralasia.exception.DublicateResourceException;
import org.ucentralasia.exception.ResourseNotFoundException;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerDao customerDao;

    public CustomerService(@Qualifier("jdbc") CustomerDao customerDao) {
        this.customerDao = customerDao;
    }

    public List<Customer> getAllCustomers() {
        return customerDao.selectAllCustomers();
    }

    public Customer getCustomer(Long id) {
        return customerDao.selectCustomerById(id).orElseThrow(() -> new ResourseNotFoundException("Customer with id [%s] doesnt exist.".formatted(id)));
    }

    public void addCustomer(CustomerRegistrationRequest customerRegistrationRequest) {
        if (customerDao.existsCustomerWithEmail(customerRegistrationRequest.email())) {
            throw new DublicateResourceException("Customer with email [%s] already exists.".formatted(customerRegistrationRequest.email()));
        }
        customerDao.insertCustomer(
                new Customer(
                        customerRegistrationRequest.name(),
                        customerRegistrationRequest.email(),
                        customerRegistrationRequest.age()
                )
        );
    }

    public void deleteCustomer(Long id) {
        Customer customer = customerDao.selectCustomerById(id).orElseThrow(
                () -> new ResourseNotFoundException("Customer with id [%s] doesnt exist.".formatted(id))
        );
        customerDao.deleteCustomerById(id);
    }

    public void updateCustomer(Long id, CustomerUpdateRequest customerUpdateRequest) {
        Customer customer = getCustomer(id);

        boolean changes = false;

        if (customerUpdateRequest.name() != null && !customerUpdateRequest.name().equals(customer.getName())) {
            customer.setName(customerUpdateRequest.name());
            changes = true;
        }

        if (customerUpdateRequest.age() != null && !customerUpdateRequest.age().equals(customer.getAge())) {
            customer.setAge(customerUpdateRequest.age());
            changes = true;
        }

        if (customerUpdateRequest.email() != null && !customerUpdateRequest.email().equals(customer.getEmail())) {
            if (customerDao.existsCustomerWithEmail(customerUpdateRequest.email())) {
                throw new DublicateResourceException("Customer with email [%s] already exists.".formatted(customerUpdateRequest.email()));
            }
            customer.setEmail(customerUpdateRequest.email());
            changes = true;
        }
        if (!changes) {
            throw new RequestValidationException("No changes provided.");
        }
        customerDao.updateCustomer(customer);
    }
}
