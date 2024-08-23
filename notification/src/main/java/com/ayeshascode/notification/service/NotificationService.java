package com.ayeshascode.notification.service;

import com.ayeshascode.notification.model.Notification;
import com.ayeshascode.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @Transactional
    public void send(UUID toCustomerId, String toCustomerEmail, String message) {
        Notification notification = new Notification(
                UUID.randomUUID(),
                toCustomerId,
                toCustomerEmail,
                "Donna",
                message,
                LocalDateTime.now()
        );
        notificationRepository.save(notification);
    }
}
