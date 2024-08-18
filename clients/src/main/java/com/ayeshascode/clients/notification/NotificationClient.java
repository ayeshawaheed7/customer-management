package com.ayeshascode.clients.notification;

import com.ayeshascode.interceptor.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import javax.validation.Valid;

@FeignClient(name = "notification", configuration = FeignConfig.class)
public interface NotificationClient {

    @PostMapping(path = "v1/notifications")
    void sendNotification(@RequestHeader(value = "X-Idempotency-Key") String xIdempotencyKey,
                          @Valid @RequestBody NotificationRequest request);
}
