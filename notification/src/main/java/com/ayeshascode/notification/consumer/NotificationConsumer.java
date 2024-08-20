package com.ayeshascode.notification.consumer;

import com.ayeshascode.clients.notification.NotificationRequest;
import com.ayeshascode.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationConsumer {

    private final NotificationService notificationService;

    @RabbitListener(queues = "${rabbitmq.queues.notification}")
    public void consumer(NotificationRequest request) {
        log.info("Consumed {} from queue", request);
        notificationService.send(
                request.toCustomerId(),
                request.toCustomerEmail(),
                request.message()
        );
    }
}
