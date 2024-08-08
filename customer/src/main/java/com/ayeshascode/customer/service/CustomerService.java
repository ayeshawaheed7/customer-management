package com.ayeshascode.customer.service;

import com.ayeshascode.customer.client.FraudClient;
import com.ayeshascode.customer.model.Customer;
import com.ayeshascode.customer.model.FraudCheckResponse;
import com.ayeshascode.customer.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final FraudClient fraudClient;
    private final IdempotencyKeyService idempotencyKeyService;

    @Transactional
    public void registerCustomer(String firstName, String lastName, String email) {
        String xIdempotencyKey = idempotencyKeyService.generateKey();

        if (isEmailAlreadyTaken(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already taken");
        }

        Customer customer = new Customer(
                UUID.randomUUID(),
                firstName,
                lastName,
                email
        );

        if (fraudCheckWithRetry(customer.getId(), xIdempotencyKey)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Customer is fraudulent. We cannot proceed with the registration.");
        }

        customerRepository.save(customer);
    }

    private boolean isEmailAlreadyTaken(String email) {
        return customerRepository.existsByEmail(email);
    }

    private boolean fraudCheckWithRetry(UUID customerId, String xIdempotencyKey) {
        int maxRetries = 3;
        for (int attempt = 0; attempt < maxRetries; attempt++) {
            try {
                FraudCheckResponse response = fraudClient.isFraudulentCustomer(customerId, xIdempotencyKey);
                return response.isFraudster();
            } catch (ResourceAccessException e) {
                if (attempt == maxRetries - 1) {
                    throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Fraud check failed after maximum retries.", e);
                }
            }
        }
        throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Unable to complete fraud check due to repeated connection issues.");
    }
}
