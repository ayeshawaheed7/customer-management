package com.ayeshascode.customer.client;

import com.ayeshascode.clients.fraud.FraudCheckResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

// microservice communication via Resttemplate - currently doing with feignClient
@Component
public class FraudClient {

    @Qualifier("fraudApiRestTemplate")
    private final RestTemplate restTemplate;

    private final String fraudServiceUrl;

    public FraudClient(RestTemplate restTemplate, @Value("${fraud-service.url}") String fraudServiceUrl) {
        this.restTemplate = restTemplate;
        this.fraudServiceUrl = fraudServiceUrl;
    }

    public FraudCheckResponse isFraudulentCustomer(UUID customerId, String xIdempotencyKey) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Idempotency-Key", xIdempotencyKey);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<FraudCheckResponse> response = restTemplate.exchange(
                fraudServiceUrl + "/v1/fraud-check/{customerId}",
                HttpMethod.POST,
                entity,
                FraudCheckResponse.class,
                customerId
        );

        return response.getBody();
    }
}
