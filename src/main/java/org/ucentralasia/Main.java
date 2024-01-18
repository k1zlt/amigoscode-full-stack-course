package org.ucentralasia;

import com.github.javafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.ucentralasia.customer.Customer;
import org.ucentralasia.customer.CustomerRespository;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
    @Bean
    CommandLineRunner runner(CustomerRespository customerRespository) {

        return args -> {
            List<Customer> customers = new ArrayList<>();
            for (int i = 0; i < 4; i++) {
                customers.add(generateFakeCustomer());
            }
            customerRespository.saveAll(customers);
        };
    }

    public static Customer generateFakeCustomer() {
        Faker faker = new Faker();
        Long id = new Random().nextLong();
        String name = faker.name().fullName().toString();
        String email = faker.internet().safeEmailAddress();
        Integer age = new Random().nextInt(100);
        return new Customer(
                id, name, email, age
        );

    }
}
