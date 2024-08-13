package com.ayeshascode.notification.service;

import com.ayeshascode.notification.repository.NotificationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationService underTest;

    @Nested
    @DisplayName("send")
    class send {

        @Nested
        @DisplayName("when notification data is provided")
        class notificationDataIsProvided {

            @Test
            @DisplayName("then send notification and persists data in DB")
            void sendNotificationAndPersistsDataInDb() {
                UUID toCustomerId = UUID.randomUUID();
                String toCustomerEmail = "harvey@suits.com";
                String message = "Woohoo! You have been successfully registered.";

                when(notificationRepository.save(any())).thenReturn(any());

                underTest.send(toCustomerId, toCustomerEmail, message);

                verify(notificationRepository).save(argThat(notification ->
                        notification.getToCustomerId().equals(toCustomerId) &&
                                notification.getToCustomerEmail().equals(toCustomerEmail) &&
                                notification.getMessage().equals(message) &&
                                notification.getSender().equals("Donna")
                ));
            }
        }
    }
}