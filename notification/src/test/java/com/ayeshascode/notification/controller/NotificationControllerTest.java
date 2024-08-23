package com.ayeshascode.notification.controller;

import com.ayeshascode.clients.notification.NotificationUpdate;
import com.ayeshascode.notification.service.IdempotencyKeyService;
import com.ayeshascode.notification.service.NotificationService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Disabled
@ExtendWith(MockitoExtension.class)
class NotificationControllerTest {

    @Mock
    private NotificationService notificationService;

    @Mock
    private IdempotencyKeyService idempotencyKeyService;

    @InjectMocks
    private NotificationController underTest;

    @Nested
    @DisplayName("sendNotification")
    class sendNotification {


        @Nested
        @DisplayName("given a valid notification request")
        class validNotificationRequest {
            private final UUID toCustomerId = UUID.randomUUID();
            private final String toCustomerEmail = "harvey@suits.com";
            private final String message = "Woohoo! You have been successfully registered.";

            private final NotificationUpdate request = new NotificationUpdate(
                    toCustomerId,
                    toCustomerEmail,
                    message
            );
            private final String xIdempotencyKey = "12345679";

            @Nested
            @DisplayName("When request is NOT already processed")
            class RequestIsNotAlreadyProcessed {

                @Nested
                @DisplayName("and saveNotification is called with the provided data")
                class saveNotificationMethodIsCalled {

                    @Test
                    @DisplayName("then should successfully save the notification")
                    void shouldSuccessfullySaveNotification() {
                        when(idempotencyKeyService.hasBeenAlreadyProcessed(any())).thenReturn(false);
                        doNothing().when(notificationService).send(any(), any(), any());
                        doNothing().when(idempotencyKeyService).save(any());

                        underTest.sendNotification(xIdempotencyKey, request);

                        verify(idempotencyKeyService).hasBeenAlreadyProcessed(xIdempotencyKey);
                        verify(notificationService).send(toCustomerId, toCustomerEmail, message);
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
                    when(idempotencyKeyService.hasBeenAlreadyProcessed(any())).thenReturn(true);

                    underTest.sendNotification(xIdempotencyKey, request);

                    verify(idempotencyKeyService).hasBeenAlreadyProcessed(xIdempotencyKey);
                    verify(notificationService, never()).send(toCustomerId, toCustomerEmail, message);
                }
            }
        }
    }
}