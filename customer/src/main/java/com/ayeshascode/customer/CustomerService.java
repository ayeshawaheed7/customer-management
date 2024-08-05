package com.ayeshascode.customer;

import com.ayeshascode.customer.model.Customer;
import com.ayeshascode.customer.model.CustomerRegistrationRequest;
import com.ayeshascode.customer.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    public void registerCustomer(CustomerRegistrationRequest request) {
        Customer customer = new Customer(
                UUID.randomUUID(),
                request.firstName(),
                request.lastName(),
                request.email()
        );
        // todo: check if email valid
        // todo: check if email is not already taken
        customerRepository.save(customer);
    }
}
