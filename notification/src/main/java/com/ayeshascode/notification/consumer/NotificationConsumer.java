package com.ayeshascode.notification.consumer;

import com.ayeshascode.clients.notification.NotificationUpdate;
import com.ayeshascode.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationConsumer {

    private final NotificationService notificationService;

//    @RabbitListener(queues = "${rabbitmq.queues.notification}")
    public void consumer(NotificationUpdate request) {
        log.info("Consumed {} from queue", request);
        notificationService.send(
                request.toCustomerId(),
                request.toCustomerEmail(),
                request.message()
        );
    }
}
