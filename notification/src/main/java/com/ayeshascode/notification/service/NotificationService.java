package com.ayeshascode.notification.service;

import com.ayeshascode.notification.model.Notification;
import com.ayeshascode.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public void send(UUID toCustomerId, String toCustomerEmail, String message) {
        notificationRepository.save(new Notification(
                        UUID.randomUUID(),
                        toCustomerId,
                        toCustomerEmail,
                        "Dumbledore",
                        message,
                        LocalDateTime.now()
                )
        );
    }
}
