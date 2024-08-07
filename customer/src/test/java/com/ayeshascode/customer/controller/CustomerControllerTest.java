package com.ayeshascode.customer.controller;

import com.ayeshascode.customer.model.CustomerRegistrationRequest;
import com.ayeshascode.customer.service.CustomerService;
import com.ayeshascode.customer.service.IdempotencyKeyService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerControllerTest {

    @Mock
    private CustomerService customerService;

    @Mock
    private IdempotencyKeyService idempotencyKeyService;

    @InjectMocks
    private CustomerController underTest;

    @Nested
    @DisplayName("registerCustomer")
    class RegisterCustomer {

        @Nested
        @DisplayName("given registration data")
        class GivenRegistrationData {

            @Nested
            @DisplayName("When request is NOT already processed")
            class RequestIsNotAlreadyProcessed {

                @Nested
                @DisplayName("and registerCustomer is called with the provided data")
                class registerCustomerIsCalled {

                    @Test
                    @DisplayName("then customer should be registered successfully")
                    void customerShouldBeRegisteredSuccessfully() {
                        CustomerRegistrationRequest request = new CustomerRegistrationRequest("Harry", "Potter", "hogwartscom");
                        String xIdempotencyKey = "123456789";

                        when(idempotencyKeyService.hasBeenAlreadyProcessed(any())).thenReturn(false);
                        doNothing().when(customerService).registerCustomer(any(), any(), any());
                        doNothing().when(idempotencyKeyService).save(any());

                        underTest.registerCustomer(xIdempotencyKey, request);

                        verify(idempotencyKeyService).hasBeenAlreadyProcessed(xIdempotencyKey);
                        verify(customerService).registerCustomer(
                                request.firstName(),
                                request.lastName(),
                                request.email()
                        );
                        verify(idempotencyKeyService).save(xIdempotencyKey);
                    }
                }
            }

            @Nested
            @DisplayName("When request is already processed")
            class RequestIsAlreadyProcessed {

                @Test
                @DisplayName("then discard the request")
                void discardTheRequest() {
                    CustomerRegistrationRequest request = new CustomerRegistrationRequest("Harry", "Potter", "hogwartscom");
                    String xIdempotencyKey = "123456789";

                    when(idempotencyKeyService.hasBeenAlreadyProcessed(any())).thenReturn(true);

                    underTest.registerCustomer(xIdempotencyKey, request);

                    verify(idempotencyKeyService).hasBeenAlreadyProcessed(xIdempotencyKey);
                    verify(customerService, never()).registerCustomer(
                            request.firstName(),
                            request.lastName(),
                            request.email()
                    );
                }
            }
        }
    }
}