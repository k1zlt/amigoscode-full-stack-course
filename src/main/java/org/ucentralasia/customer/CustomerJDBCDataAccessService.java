package org.ucentralasia.customer;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("jdbc")
public class CustomerJDBCDataAccessService implements CustomerDao{
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper customerRowMapper;

    public CustomerJDBCDataAccessService(JdbcTemplate jdbcTemplate, RowMapper customerRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.customerRowMapper = customerRowMapper;
    }

    @Override
    public List<Customer> selectAllCustomers() {
        var sql = """
                SELECT id, name, email, age 
                FROM customer
        """;
        return jdbcTemplate.query(sql, customerRowMapper);
    }

    @Override
    public Optional selectCustomerById(Long id) {
        var sql = """
                SELECT * FROM customer WHERE id = ?
                """;
        return jdbcTemplate.query(sql, customerRowMapper, id)
                .stream()
                .findFirst();

    }

    @Override
    public void insertCustomer(Customer customer) {
        var sql = """
                INSERT INTO customer(name, email, age)
                VALUES (?, ?, ?)
                """;
        int update = jdbcTemplate.update(sql, customer.getName(), customer.getEmail(), customer.getAge());
        System.out.println("jdbcTemplate.update = " + update);
    }

    @Override
    public boolean existsCustomerWithEmail(String email) {
        var sql = """
                SELECT COUNT(id)
                FROM customer
                WHERE email = ?
                """;
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, email);
        return count != null && count > 0;
    }

    @Override
    public boolean existsCustomerWithId(Long id) {
        var sql = """
                SELECT COUNT(id)
                FROM customer
                WHERE id = ?
                """;
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }

    @Override
    public void deleteCustomerById(Long id) {
        var sql = """
                DELETE
                FROM customer
                WHERE id = ?
                """;
        int result = jdbcTemplate.update(sql, id);
        System.out.println("result = " + result);
    }

    @Override
    public void updateCustomer(Customer customer) {
        if (customer.getId() == null) throw new IllegalArgumentException("Customer id is null");
        if (customer.getName() != null) {
            var sql = """
                    UPDATE customer
                    SET name = ?
                    WHERE id = ?
                    """;
            int result = jdbcTemplate.update(sql, customer.getName(), customer.getId());
            System.out.println("Change name result is " + result);
        }
        if (customer.getEmail()!= null) {
            var sql = """
                    UPDATE customer
                    SET email = ?
                    WHERE id = ?
                    """;
            int result = jdbcTemplate.update(sql, customer.getEmail(), customer.getId());
            System.out.println("Change email result is " + result);
        }
        if (customer.getAge() != null) {
            var sql = """
                    UPDATE customer
                    SET age = ?
                    WHERE id = ?
                    """;
            int result = jdbcTemplate.update(sql, customer.getAge(), customer.getId());
            System.out.println("Change age result is " + result);
        }
    }
}
