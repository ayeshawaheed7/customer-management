package com.ayeshascode.customer.service;

import com.ayeshascode.clients.fraud.FraudCheckResponse;
import com.ayeshascode.clients.fraud.FraudClient;
import com.ayeshascode.clients.notification.NotificationUpdate;
import com.ayeshascode.customer.model.Customer;
import com.ayeshascode.customer.repository.CustomerRepository;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Tracer;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.UUID;

@Service
@Slf4j
@AllArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final FraudClient fraudClient;
    private final IdempotencyKeyService idempotencyKeyService;

    @Qualifier("notificationUpdateKafkaTemplate")
    private KafkaTemplate<String, NotificationUpdate> kafkaTemplate;

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

        customerRepository.saveAndFlush(customer);

        NotificationUpdate notificationUpdate = new NotificationUpdate(
                customer.getId(),
                customer.getEmail(),
                "Hi. Welcome to Hogwarts. :)"
        );

        dispatch(notificationUpdate);
    }

    private void dispatch(NotificationUpdate notificationUpdate) {
        ProducerRecord<String, NotificationUpdate> producerRecord = new ProducerRecord<>("notification-updates", notificationUpdate.toCustomerId().toString(), notificationUpdate);
        kafkaTemplate.send(producerRecord);
        log.info("Dispatching event: \n {} to topic: {}", notificationUpdate, "notification-updates");
    }

    private boolean isEmailAlreadyTaken(String email) {
        return customerRepository.existsByEmail(email);
    }

    private boolean fraudCheckWithRetry(UUID customerId, String xIdempotencyKey) {
        int maxRetries = 3;
        for (int attempt = 0; attempt < maxRetries; attempt++) {
            try {
                FraudCheckResponse response = fraudClient.saveAndCheckFraud(xIdempotencyKey, customerId).getBody();
                return response.isFraudster();
            } catch (ResourceAccessException e) {
                if (attempt == maxRetries - 1) {
                    throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Fraud check failed after maximum retries.", e);
                }
            }
        }
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to complete fraud check due to some internal server issues.");
    }
}
