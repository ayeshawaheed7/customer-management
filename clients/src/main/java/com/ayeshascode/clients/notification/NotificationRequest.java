package com.ayeshascode.clients.notification;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.UUID;

public record NotificationRequest(
        @NotNull
        UUID toCustomerId,
        @NotBlank
        String toCustomerEmail,
        @NotBlank
        String message
) {
}
