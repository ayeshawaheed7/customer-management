package com.ayeshascode.clients.notification;

import java.util.UUID;

public record NotificationUpdate(
        UUID toCustomerId,
        String toCustomerEmail,
        String message
) {
}
