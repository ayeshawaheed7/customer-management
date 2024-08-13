package com.ayeshascode.customer.service;

import com.ayeshascode.clients.fraud.FraudCheckResponse;
import com.ayeshascode.clients.fraud.FraudClient;
import com.ayeshascode.customer.model.Customer;
import com.ayeshascode.customer.model.CustomerRegistrationRequest;
import com.ayeshascode.customer.repository.CustomerRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private IdempotencyKeyService idempotencyKeyService;

    @Mock
    private FraudClient fraudClient;

    @InjectMocks
    private CustomerService underTest;

    @Nested
    @DisplayName("registerCustomer")
    class RegisterCustomer {

        @Nested
        @DisplayName("when registration data is provided")
        class RegistrationDataIsProvided {

            private final Customer customer = new Customer(
                    UUID.randomUUID(),
                    "Harry",
                    "Potter",
                    "harry@hogwarts.com"
            );

            private final CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                    customer.getFirstName(),
                    customer.getLastName(),
                    customer.getEmail()
            );

            private final String xIdempotencyKey = UUID.randomUUID().toString();

            @Test
            @DisplayName("then save customer in DB")
            void saveCustomerInDB() {
                FraudCheckResponse expectedResponse = new FraudCheckResponse(false);

                when(idempotencyKeyService.generateKey()).thenReturn(xIdempotencyKey);
                when(customerRepository.existsByEmail(any())).thenReturn(false);
                when(customerRepository.save(any())).thenReturn(customer);
                when(fraudClient.saveAndCheckFraud(any(), any())).thenReturn(ResponseEntity.ok(expectedResponse));

                underTest.registerCustomer(
                        request.firstName(),
                        request.lastName(),
                        request.email()
                );

                verify(idempotencyKeyService).generateKey();
                verify(customerRepository).existsByEmail(request.email());
                verify(customerRepository).save(argThat(cu ->
                        cu.getFirstName().equals(customer.getFirstName()) &&
                                cu.getLastName().equals(customer.getLastName()) &&
                                cu.getEmail().equals(customer.getEmail())
                ));
            }

            @Nested
            @DisplayName("and email is already taken")
            class EmailIsAlreadyTaken {

                @Test
                @DisplayName("then throws ResponseStatusException")
                void throwsResponseStatusException() {
                    when(customerRepository.existsByEmail(any())).thenReturn(true);

                    ResponseStatusException thrown = assertThrows(
                            ResponseStatusException.class, () -> {
                                underTest.registerCustomer(request.firstName(), request.lastName(), request.email()
                                );
                            });

                    assertThat(thrown.getStatus()).isEqualTo(HttpStatus.CONFLICT);
                    assertThat(thrown.getReason()).isEqualTo("Email already taken");

                    verify(customerRepository).existsByEmail(request.email());
                }
            }

            @Nested
            @DisplayName("and customer is fraudulent")
            class customerIsFraudulent {

                @Test
                @DisplayName("then throws ResponseStatusException")
                void throwsResponseStatusException() {
                    FraudCheckResponse expectedResponse = new FraudCheckResponse(true);

                    when(idempotencyKeyService.generateKey()).thenReturn(xIdempotencyKey);
                    when(customerRepository.existsByEmail(any())).thenReturn(false);
                    when(fraudClient.saveAndCheckFraud(any(), any())).thenReturn(ResponseEntity.ok(expectedResponse));

                    ResponseStatusException thrown = assertThrows(
                            ResponseStatusException.class, () -> {
                                underTest.registerCustomer(request.firstName(), request.lastName(), request.email()
                                );
                            });

                    assertThat(thrown.getStatus()).isEqualTo(HttpStatus.FORBIDDEN);
                    assertThat(thrown.getReason()).isEqualTo("Customer is fraudulent. We cannot proceed with the registration.");

                    verify(customerRepository).existsByEmail(request.email());
                    verify(customerRepository, never()).save(any());
                    verify(idempotencyKeyService).generateKey();
                }
            }
        }
    }
}