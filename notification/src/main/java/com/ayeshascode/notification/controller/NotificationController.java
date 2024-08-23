package com.ayeshascode.notification.controller;

import com.ayeshascode.clients.notification.NotificationUpdate;
import com.ayeshascode.notification.service.IdempotencyKeyService;
import com.ayeshascode.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final IdempotencyKeyService idempotencyKeyService;

    @PostMapping
    public void sendNotification(@RequestHeader(value = "X-Idempotency-Key") String xIdempotencyKey,
                                 @Valid @RequestBody NotificationUpdate request) {
        log.info("send notification request {}", request);
        if (idempotencyKeyService.hasBeenAlreadyProcessed(xIdempotencyKey)) {
            log.info("Already processed - Discarding send notification request with xIdempotencyKey: {} \n and request: {}", xIdempotencyKey, request);
            return;
        }
        notificationService.send(
                request.toCustomerId(),
                request.toCustomerEmail(),
                request.message()
        );
        idempotencyKeyService.save(xIdempotencyKey);
    }
}
