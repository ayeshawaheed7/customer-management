package com.ayeshascode.customer;

import com.ayeshascode.customer.model.Customer;
import com.ayeshascode.customer.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    public void registerCustomer(String firstName, String lastName, String email) {
        if (isEmailAlreadyTaken(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already taken");
        }

        Customer customer = new Customer(
                UUID.randomUUID(),
                firstName,
                lastName,
                email
        );

        customerRepository.save(customer);
    }

    private boolean isEmailAlreadyTaken(String email) {
        return customerRepository.existsByEmail(email);
    }
}
