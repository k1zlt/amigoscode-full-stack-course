package org.ucentralasia.customer;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRespository extends JpaRepository<Customer, Integer> {
    boolean existsCustomerByEmail(String email);
    boolean existsCustomerById(Long id);
}
