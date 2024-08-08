package com.ayeshascode.customer.client;

import com.ayeshascode.customer.model.FraudCheckResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FraudClientTest {

    @Mock
    private RestTemplate restTemplate;

    @Value("${fraud-service.url}")
    private String fraudServiceUrl;

    @InjectMocks
    private FraudClient underTest;

    FraudClientTest() {
    }

    @BeforeEach
    void setUp() {
        underTest = new FraudClient(restTemplate, fraudServiceUrl);
    }

    @Nested
    @DisplayName("isFraudulentCustomer")
    class isFraudulentCustomer {

        @Nested
        @DisplayName("when customer Id is provided")
        class customerIdIsProvided {

            private final UUID customerId = UUID.randomUUID();
            private final String xIdempotencyKey = UUID.randomUUID().toString();

            @Nested
            @DisplayName("and customer is fraudulent")
            class customerIsFraudulent {

                @Test
                @DisplayName("then return true")
                void returnTrue() {
                    FraudCheckResponse expectedResponse = new FraudCheckResponse(true);

                    when(restTemplate.exchange(
                            fraudServiceUrl + "/v1/fraud-check/{customerId}",
                            HttpMethod.POST,
                            createHttpEntity(xIdempotencyKey),
                            FraudCheckResponse.class,
                            customerId)
                    ).thenReturn(ResponseEntity.ok(expectedResponse));

                    FraudCheckResponse result = underTest.isFraudulentCustomer(customerId, xIdempotencyKey);

                    assertThat(result.isFraudster()).isTrue();

                    verify(restTemplate).exchange(
                            fraudServiceUrl + "/v1/fraud-check/{customerId}",
                            HttpMethod.POST,
                            createHttpEntity(xIdempotencyKey),
                            FraudCheckResponse.class,
                            customerId);
                }
            }

            @Nested
            @DisplayName("and customer is NOT fraudulent")
            class customerIsNotFraudulent {

                @Test
                @DisplayName("then return false")
                void returnFalse() {
                    FraudCheckResponse expectedResponse = new FraudCheckResponse(false);

                    when(restTemplate.exchange(
                            fraudServiceUrl + "/v1/fraud-check/{customerId}",
                            HttpMethod.POST,
                            createHttpEntity(xIdempotencyKey),
                            FraudCheckResponse.class,
                            customerId)
                    ).thenReturn(ResponseEntity.ok(expectedResponse));

                    FraudCheckResponse result = underTest.isFraudulentCustomer(customerId, xIdempotencyKey);

                    assertThat(result.isFraudster()).isFalse();

                    verify(restTemplate).exchange(
                            fraudServiceUrl + "/v1/fraud-check/{customerId}",
                            HttpMethod.POST,
                            createHttpEntity(xIdempotencyKey),
                            FraudCheckResponse.class,
                            customerId);
                }
            }
        }
    }

    private HttpEntity<Void> createHttpEntity(String xIdempotencyKey) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Idempotency-Key", xIdempotencyKey);
        return new HttpEntity<>(headers);
    }
}