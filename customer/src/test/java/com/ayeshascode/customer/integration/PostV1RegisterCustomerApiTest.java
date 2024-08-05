package com.ayeshascode.customer.integration;

import com.ayeshascode.customer.container.config.IntegrationTest;
import com.ayeshascode.customer.model.Customer;
import com.ayeshascode.customer.model.CustomerRegistrationRequest;
import com.ayeshascode.customer.repository.CustomerRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("POST v1/customers")
@IntegrationTest
public class PostV1RegisterCustomerApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CustomerRepository customerRepository;

    @BeforeEach
    void setUp() {
        customerRepository.deleteAll();
    }

    @Nested
    @DisplayName("given registration data")
    class GivenRegistrationData {

        @Nested
        @DisplayName("When input data is valid")
        class InputDataIsValid {

            @Test
            @DisplayName("then customer should be registered successfully")
            void ShouldRegisterCustomer() throws Exception {
                var request = new CustomerRegistrationRequest(
                        "Albus",
                        "Dumbledore",
                        "dumbledore@hogwarts.com"
                );

                String requestJson = objectMapper.writeValueAsString(request);

                mockMvc.perform(post("/v1/customers")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson))
                        .andExpect(status().isOk());

                Customer customer = customerRepository.findAll()
                        .stream()
                        .findFirst()
                        .orElseThrow(() -> new AssertionError("Expected customer not found"));

                assertThat(customer.getFirstName()).isEqualTo("Albus");
                assertThat(customer.getLastName()).isEqualTo("Dumbledore");
                assertThat(customer.getEmail()).isEqualTo("dumbledore@hogwarts.com");
            }
        }

        @Nested
        @DisplayName("When input data is invalid")
        class InputDataIsInValid {

            @Test
            @DisplayName("then response status should be BAD REQUEST")
            void responseStatusShouldBeBadRequest() throws Exception {
                var request = new CustomerRegistrationRequest(
                        "",
                        "Dumbledore",
                        "dumbledore@hogwarts.com"
                );

                String requestJson = objectMapper.writeValueAsString(request);

                mockMvc.perform(post("/v1/customers")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson))
                        .andExpect(status().isBadRequest());

                assertThat(customerRepository.findAll()).isEmpty();
            }
        }
    }
}
