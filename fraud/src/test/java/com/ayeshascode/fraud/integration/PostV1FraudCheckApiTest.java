package com.ayeshascode.fraud.integration;

import com.ayeshascode.fraud.container.config.IntegrationTest;
import com.ayeshascode.fraud.model.FraudCheckHistory;
import com.ayeshascode.fraud.model.FraudCheckResponse;
import com.ayeshascode.fraud.repository.FraudCheckHistoryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@DisplayName("POST v1/fraud-check/{customerId}")
@IntegrationTest
public class PostV1FraudCheckApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FraudCheckHistoryRepository fraudCheckHistoryRepository;

    @Nested
    @DisplayName("saveAndCheckFraud")
    class saveAndCheckFraud {

        @Nested
        @DisplayName("given a valid customer Id")
        class validCustomerId {

            @Test
            @DisplayName("then should determine customer fraud check")
            void shouldDetermineFraudCheck() throws Exception {
                UUID customerId = UUID.randomUUID();
                FraudCheckHistory fraudCheckHistory = new FraudCheckHistory(
                        UUID.randomUUID(),
                        customerId,
                        false,
                        LocalDateTime.now()
                );

                fraudCheckHistoryRepository.save(fraudCheckHistory);

                String response = mockMvc.perform(post("/v1/fraud-check/{customerId}", "b6bfef64-27a8-4b67-9f95-111f306acbb8")
                                .header("X-Idempotency-Key", "b6bfef64-27a8-4b67-9f95-111f306acbbc")
                                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

                FraudCheckResponse responseDTO = objectMapper.readValue(response, FraudCheckResponse.class);

                assertThat(responseDTO.isFraudster()).isFalse();
            }
        }
    }
}
