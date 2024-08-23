package com.ayeshascode.notification.integration;

import com.ayeshascode.clients.notification.NotificationUpdate;
import com.ayeshascode.notification.container.config.IntegrationTest;
import com.ayeshascode.notification.model.Notification;
import com.ayeshascode.notification.repository.NotificationRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@DisplayName("POST v1/notifications")
@Disabled
@IntegrationTest
public class PostV1SendNotificationApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private NotificationRepository notificationRepository;

    @Nested
    @DisplayName("sendNotification")
    class sendNotification {

        @Nested
        @DisplayName("when a valid notification request provided")
        class validNotificationRequestProvided {

            @Test
            @DisplayName("then should save data successfully in DB")
            void shouldSaveDataSuccessfullyInDb() throws Exception {
                UUID toCustomerId = UUID.randomUUID();
                String toCustomerEmail = "ross@suits.com";
                String message = "Hi. Welcome to Pearson Spector Litt. ;)";

                NotificationUpdate request = new NotificationUpdate(
                        toCustomerId,
                        toCustomerEmail,
                        message
                );
                String xIdempotencyKey = "12345679";

                String requestJson = objectMapper.writeValueAsString(request);

                mockMvc.perform(post("/v1/notifications")
                                .header("X-Idempotency-Key", xIdempotencyKey)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson))
                        .andExpect(status().isOk());

                Notification notification = notificationRepository.findAll()
                        .stream()
                        .findFirst()
                        .orElseThrow(() -> new AssertionError("Expected result not found"));

                assertThat(notification.getToCustomerId()).isEqualTo(toCustomerId);
                assertThat(notification.getToCustomerEmail()).isEqualTo(toCustomerEmail);
                assertThat(notification.getMessage()).isEqualTo(message);
                assertThat(notification.getSender()).isEqualTo("Donna");
            }
        }
    }
}
