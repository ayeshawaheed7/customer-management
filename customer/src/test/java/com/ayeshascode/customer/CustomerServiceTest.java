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

import java.util.UUID;

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

            @Test
            @DisplayName("then save customer in DB")
            void saveCustomerInDB() {
                Customer customer = new Customer(
                        UUID.randomUUID(),
                        "Harry",
                        "Potter",
                        "harry@hogwarts.com"
                );

                CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                        customer.getFirstName(),
                        customer.getLastName(),
                        customer.getEmail()
                );

                when(customerRepository.save(any())).thenReturn(customer);

                underTest.registerCustomer(request);

                verify(customerRepository).save(argThat(cu ->
                        cu.getFirstName().equals(customer.getFirstName()) &&
                        cu.getLastName().equals(customer.getLastName()) &&
                        cu.getEmail().equals(customer.getEmail())
                ));
            }
        }
    }
}