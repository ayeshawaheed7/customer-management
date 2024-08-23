package com.ayeshascode.notification.integration;

import com.ayeshascode.clients.notification.NotificationUpdate;
import com.ayeshascode.notification.consumer.config.KafkaConsumerConfig.NotificationUpdateSerializer;
import com.ayeshascode.notification.container.config.IntegrationTest;
import com.ayeshascode.notification.model.Notification;
import com.ayeshascode.notification.repository.NotificationRepository;
import com.ayeshascode.notification.utils.KafkaUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

@IntegrationTest
public class NotificationUpdateEventIntegrationTest {

    @Autowired
    private NotificationRepository notificationRepository;

    private KafkaProducer<String, NotificationUpdate> producer;

    @BeforeEach
    void setup() {
        notificationRepository.deleteAll();
        producer = KafkaUtils.createProducer(NotificationUpdateSerializer.class);
    }

    @Nested
    @DisplayName("NotificationUpdate")
    class notificationUpdate {

        @Nested
        @DisplayName("when consume notification update event")
        class consumeNotificationUpdateEvent {

            @Test
            @DisplayName("then should save data successfully in DB")
            void shouldSaveDataSuccessfullyInDB() {
                UUID toCustomerId = UUID.randomUUID();
                String toCustomerEmail = "ross@suits.com";
                String message = "Hi. Welcome to Pearson Spector Litt. ;)";

                String topicName = "notification-updates";

                NotificationUpdate notificationUpdate = new NotificationUpdate(
                        toCustomerId,
                        toCustomerEmail,
                        message
                );

                producer.send(new ProducerRecord<>(topicName, UUID.randomUUID().toString(), notificationUpdate));

                await().atMost(20, SECONDS).untilAsserted(() -> {
                    Notification notification = notificationRepository.findAll()
                            .stream()
                            .findFirst()
                            .orElseThrow(() -> new AssertionError("Expected result not found"));

                    assertThat(notification.getToCustomerId()).isEqualTo(toCustomerId);
                    assertThat(notification.getToCustomerEmail()).isEqualTo(toCustomerEmail);
                    assertThat(notification.getMessage()).isEqualTo(message);
                    assertThat(notification.getSender()).isEqualTo("Donna");
                });

                producer.close();
            }
        }
    }
}
