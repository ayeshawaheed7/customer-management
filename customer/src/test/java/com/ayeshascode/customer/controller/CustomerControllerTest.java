package com.ayeshascode.customer.controller;

import com.ayeshascode.customer.CustomerService;
import com.ayeshascode.customer.model.CustomerRegistrationRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CustomerControllerTest {

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private CustomerController underTest;

    @Nested
    @DisplayName("registerCustomer")
    class RegisterCustomer {

        @Nested
        @DisplayName("given registration data")
        class GivenRegistrationData {

            @Nested
            @DisplayName("When registerCustomer is called with the provided data")
            class registerCustomerIsCalled {

                @Test
                @DisplayName("then customer should be registered successfully")
                void customerShouldBeRegisteredSuccessfully() {
                    CustomerRegistrationRequest request = new CustomerRegistrationRequest(null, "Potter", "hogwartscom");

                    underTest.registerCustomer(request);

                    verify(customerService).registerCustomer(
                            request.firstName(),
                            request.lastName(),
                            request.email()
                    );
                }
            }
        }
    }
}