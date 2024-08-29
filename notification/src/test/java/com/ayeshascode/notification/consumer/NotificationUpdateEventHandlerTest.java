package com.ayeshascode.notification.consumer;

import com.ayeshascode.clients.notification.NotificationUpdate;
import com.ayeshascode.notification.service.IdempotencyKeyService;
import com.ayeshascode.notification.service.NotificationService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
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

@ExtendWith(MockitoExtension.class)
class NotificationUpdateEventHandlerTest {

    @Mock
    private NotificationService notificationService;

    @Mock
    private IdempotencyKeyService idempotencyKeyService;

    @InjectMocks
    private NotificationUpdateEventHandler underTest;

    @Nested
    @DisplayName("consumeNotificationUpdate")
    class consumeNotificationUpdate {


        @Nested
        @DisplayName("given a notification update event")
        class NotificationUpdateEvent {
            private final UUID toCustomerId = UUID.randomUUID();
            private final String toCustomerEmail = "harvey@suits.com";
            private final String message = "Woohoo! You have been successfully registered.";

            private final NotificationUpdate notificationUpdate = new NotificationUpdate(
                    toCustomerId,
                    toCustomerEmail,
                    message
            );
            private final String xIdempotencyKey = toCustomerId.toString();

            private ConsumerRecord<String, NotificationUpdate> consumerRecord =
                    new ConsumerRecord<>(
                            "notification-updates",
                            1,
                            1,
                            toCustomerId.toString(),
                            notificationUpdate
                    );

            @Nested
            @DisplayName("When event is NOT already processed")
            class EventIsNotAlreadyProcessed {

                @Nested
                @DisplayName("and sendMethod is called with the provided data")
                class sendMethodIsCalled {

                    @Test
                    @DisplayName("then should successfully save the notification")
                    void shouldSuccessfullySaveNotification() {
                        when(idempotencyKeyService.hasBeenAlreadyProcessed(any())).thenReturn(false);
                        doNothing().when(notificationService).send(any(), any(), any());
                        doNothing().when(idempotencyKeyService).save(any());


                        underTest.consumeNotificationUpdate(consumerRecord);

                        verify(idempotencyKeyService).hasBeenAlreadyProcessed(xIdempotencyKey);
                        verify(notificationService).send(toCustomerId, toCustomerEmail, message);
                        verify(idempotencyKeyService).save(xIdempotencyKey);
                    }
                }
            }

            @Nested
            @DisplayName("When event is already processed")
            class EventIsAlreadyProcessed {

                @Test
                @DisplayName("then discard the request")
                void discardTheRequest() {
                    when(idempotencyKeyService.hasBeenAlreadyProcessed(any())).thenReturn(true);

                    underTest.consumeNotificationUpdate(consumerRecord);

                    verify(idempotencyKeyService).hasBeenAlreadyProcessed(xIdempotencyKey);
                    verify(notificationService, never()).send(toCustomerId, toCustomerEmail, message);
                }
            }
        }
    }
}