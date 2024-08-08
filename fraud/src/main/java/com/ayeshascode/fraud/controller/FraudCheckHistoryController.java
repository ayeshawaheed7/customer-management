package com.ayeshascode.fraud.controller;

import com.ayeshascode.fraud.model.FraudCheckResponse;
import com.ayeshascode.fraud.service.FraudCheckHistoryService;
import com.ayeshascode.fraud.service.IdempotencyKeyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("v1/fraud-check")
public class FraudCheckHistoryController {

    private final FraudCheckHistoryService fraudCheckHistoryService;
    private final IdempotencyKeyService idempotencyKeyService;

    @PostMapping(path = "{customerId}")
    public ResponseEntity<FraudCheckResponse> saveAndCheckFraud(@RequestHeader(value = "X-Idempotency-Key") String xIdempotencyKey,
                                                                @PathVariable("customerId") UUID customerId) {
        if (idempotencyKeyService.hasBeenAlreadyProcessed(xIdempotencyKey)) {
            log.debug("Already processed - Discarding register customer request with xIdempotencyKey: {} \n and customerId: {}", xIdempotencyKey, customerId);
            return ResponseEntity.ok().build();
        }
        boolean isFraudulentCustomer = fraudCheckHistoryService.saveAndCheckFraud(customerId);
        idempotencyKeyService.save(xIdempotencyKey);
        return ResponseEntity.ok(new FraudCheckResponse(isFraudulentCustomer));
    }
}
