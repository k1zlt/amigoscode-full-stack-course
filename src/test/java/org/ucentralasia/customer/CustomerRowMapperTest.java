package org.ucentralasia.customer;

import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CustomerRowMapperTest {

    @Test
    void mapRow() throws SQLException {
        CustomerRowMapper customerRowMapper = new CustomerRowMapper();
        ResultSet rs = mock(ResultSet.class);
        when(rs.getInt("id")).thenReturn(1);
        when(rs.getString("name")).thenReturn("Firuz");
        when(rs.getString("email")).thenReturn("firuz.azi@ga");
        when(rs.getInt("age")).thenReturn(122);
        Customer customer = customerRowMapper.mapRow(rs, 1);
        assertEquals(1, customer.getId());
        assertEquals("Firuz", customer.getName());
        assertEquals("firuz.azi@ga", customer.getEmail());
        assertEquals(122, customer.getAge());
    }
}