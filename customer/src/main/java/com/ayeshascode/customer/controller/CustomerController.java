package com.ayeshascode.customer.controller;

import com.ayeshascode.customer.model.CustomerRegistrationRequest;
import com.ayeshascode.customer.service.CustomerService;
import com.ayeshascode.customer.service.IdempotencyKeyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("v1/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;
    private final IdempotencyKeyService idempotencyKeyService;

    @PostMapping
    public void registerCustomer(@RequestHeader(value = "X-Idempotency-Key") String xIdempotencyKey,
                                 @Valid @RequestBody CustomerRegistrationRequest request) {
        log.info("new customer registration {}", request);
        if (idempotencyKeyService.hasBeenAlreadyProcessed(xIdempotencyKey)) {
            log.debug("Already processed - Discarding register customer request with xIdempotencyKey: {} \n and request: {}", xIdempotencyKey, request);
            return;
        }
        customerService.registerCustomer(
                request.firstName(),
                request.lastName(),
                request.email()
        );
        idempotencyKeyService.save(xIdempotencyKey);
    }
}