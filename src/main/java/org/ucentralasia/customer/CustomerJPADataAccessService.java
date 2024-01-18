package org.ucentralasia.customer;


import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;
import java.util.Optional;

@Repository("jpa")
public class CustomerJPADataAccessService implements CustomerDao{

    private final CustomerRespository customerRespository;

    public CustomerJPADataAccessService(CustomerRespository customerRespository) {
        this.customerRespository = customerRespository;
    }
    @Override
    public List<Customer> selectAllCustomers() {
        return customerRespository.findAll();
    }

    @Override
    public Optional<Customer> selectCustomerById(Long id) {
        return customerRespository.findById(Math.toIntExact(id));
    }

    @Override
    public void insertCustomer(Customer customer) {
        customerRespository.save(customer);
    }

    @Override
    public boolean existsCustomerWithEmail(String email) {
        return customerRespository.existsCustomerByEmail(email);
    }

    @Override
    public boolean existsCustomerWithId(Long id) {
        return customerRespository.existsCustomerById(id);
    }

    @Override
    public void deleteCustomerById(Long id) {
        customerRespository.deleteById(Math.toIntExact(id));
    }

    @Override
    public void updateCustomer(Customer customer) {
        customerRespository.save(customer);
    }
}
