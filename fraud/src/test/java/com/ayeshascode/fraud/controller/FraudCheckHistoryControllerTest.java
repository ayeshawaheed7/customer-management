package com.ayeshascode.fraud.controller;

import com.ayeshascode.fraud.service.FraudCheckHistoryService;
import com.ayeshascode.fraud.service.IdempotencyKeyService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FraudCheckHistoryControllerTest {

    @Mock
    private FraudCheckHistoryService fraudCheckHistoryService;

    @Mock
    private IdempotencyKeyService idempotencyKeyService;

    @InjectMocks
    private FraudCheckHistoryController underTest;

    @Nested
    @DisplayName("saveAndCheckFraud")
    class saveAndCheckFraud {


        @Nested
        @DisplayName("given a valid customer Id")
        class validCustomerId {

            @Nested
            @DisplayName("When request is NOT already processed")
            class RequestIsNotAlreadyProcessed {

                @Nested
                @DisplayName("and saveAndCheckFraud method is called with customer Id")
                class saveAndCheckFraudMethodIsCalledWithCustomerId {

                    @Test
                    @DisplayName("then should return customer fraud check response")
                    void shouldReturnCustomerFraudCheckResponse() {
                        UUID customerId = UUID.randomUUID();
                        String xIdempotencyKey = "12345679";

                        when(idempotencyKeyService.hasBeenAlreadyProcessed(any())).thenReturn(false);
                        when(fraudCheckHistoryService.saveAndCheckFraud(any())).thenReturn(false);
                        doNothing().when(idempotencyKeyService).save(any());

                        underTest.saveAndCheckFraud(xIdempotencyKey, customerId);

                        verify(idempotencyKeyService).hasBeenAlreadyProcessed(xIdempotencyKey);
                        verify(fraudCheckHistoryService).saveAndCheckFraud(customerId);
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
                    UUID customerId = UUID.randomUUID();
                    String xIdempotencyKey = "123456789";

                    when(idempotencyKeyService.hasBeenAlreadyProcessed(any())).thenReturn(true);

                    underTest.saveAndCheckFraud(xIdempotencyKey, customerId);

                    verify(idempotencyKeyService).hasBeenAlreadyProcessed(xIdempotencyKey);
                    verify(fraudCheckHistoryService, never()).saveAndCheckFraud(customerId);
                }
            }
        }
    }
}