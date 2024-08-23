package com.ayeshascode.notification.consumer;

import com.ayeshascode.clients.notification.NotificationUpdate;
import com.ayeshascode.notification.service.IdempotencyKeyService;
import com.ayeshascode.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationUpdateEventHandler {

    private final IdempotencyKeyService idempotencyKeyService;
    private final NotificationService notificationService;

    @KafkaListener(
            id = "notification-updates-event-handler",
            topics = {"notification-updates"},
            containerFactory = "notificationUpdateKafkaListenerContainerFactory"
    )
    public void consumeNotificationUpdate(NotificationUpdate notificationUpdate) {
        log.info("Received notification update event: \n {}", notificationUpdate);

        UUID xIdempotencyKey = notificationUpdate.toCustomerId();
        if (idempotencyKeyService.hasBeenAlreadyProcessed(xIdempotencyKey.toString())) {
            log.info("Already processed - Discarding notification update event with xIdempotencyKey: {} and event: \n {}", xIdempotencyKey, notificationUpdate);
            return;
        }

        notificationService.send(
                notificationUpdate.toCustomerId(),
                notificationUpdate.toCustomerEmail(),
                notificationUpdate.message()
        );
        idempotencyKeyService.save(xIdempotencyKey.toString());
        log.info("Consumed groupId: {}, event: \n {} ", "notification-updates-event-handler", notificationUpdate);
    }
}

