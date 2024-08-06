package com.ayeshascode.customer;

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
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

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

            @Test
            @DisplayName("then save customer in DB")
            void saveCustomerInDB() {
                when(customerRepository.existsByEmail(any())).thenReturn(false);
                when(customerRepository.save(any())).thenReturn(customer);

                underTest.registerCustomer(
                        request.firstName(),
                        request.lastName(),
                        request.email()
                );

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
                @DisplayName("then throws EmailAlreadyTakenException")
                void throwsEmailAlreadyTakenException() {
                    when(customerRepository.existsByEmail(any())).thenReturn(true);

                    ResponseStatusException thrown = assertThrows(
                            ResponseStatusException.class, () -> {
                                underTest.registerCustomer(request.firstName(), request.lastName(), request.email()
                                );
                            });

                    assertThat(thrown.getStatus()).isEqualTo(HttpStatus.CONFLICT);
                    assertThat(thrown.getReason()).isEqualTo("Email already taken");
                }
            }
        }
    }
}