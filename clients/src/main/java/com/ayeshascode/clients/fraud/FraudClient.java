package com.ayeshascode.clients.fraud;

import com.ayeshascode.interceptor.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

@FeignClient(
        name = "fraud",
        configuration = FeignConfig.class,
        url = "${clients.fraud.url}"
)
public interface FraudClient {
    @PostMapping(path = "v1/fraud-check/{customerId}")
    ResponseEntity<FraudCheckResponse> saveAndCheckFraud(@RequestHeader(value = "X-Idempotency-Key") String xIdempotencyKey,
                                                         @PathVariable("customerId") UUID customerId);
}
