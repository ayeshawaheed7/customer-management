package com.ayeshascode.customer.integration;

import com.ayeshascode.clients.notification.NotificationUpdate;
import com.ayeshascode.customer.container.config.IntegrationTest;
import com.ayeshascode.customer.producer.config.KafkaProducerConfig.NotificationUpdateDeserializer;
import com.ayeshascode.customer.utils.KafkaUtils;
import com.ayeshascode.customer.mock.WireMockConfig;
import com.ayeshascode.customer.mock.mockserver.MockServer;
import com.ayeshascode.customer.model.Customer;
import com.ayeshascode.customer.model.CustomerRegistrationRequest;
import com.ayeshascode.customer.repository.CustomerRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("mock-service")
@EnableFeignClients
@ContextConfiguration(classes = {WireMockConfig.class})
@DisplayName("POST v1/customers")
@IntegrationTest
public class PostV1RegisterCustomerApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private WireMockServer mockFraudService;

//    @Autowired
//    private WireMockServer mockNotificationService;

    @Autowired
    private MockServer mockServer;

    private final String FRAUD_CHECK_URL = "/v1/fraud-check/.*";
    private final String SEND_NOTIFICATION_URL = "/v1/notifications";

    private final String TOPIC = "notification-updates";

    private KafkaConsumer<String, NotificationUpdate> consumer;

    @BeforeEach
    void setUp() {
        customerRepository.deleteAll();
    }

    @Nested
    @DisplayName("given registration data")
    class GivenRegistrationData {

        @Nested
        @DisplayName("when input data is valid")
        class InputDataIsValid {

            @Nested
            @DisplayName("and customer is NOT fraudulent")
            class customerIsNotFraudulent {

                @Test
                @DisplayName("then customer should be registered successfully")
                void ShouldRegisterCustomer() throws Exception {
                    consumer = KafkaUtils.createConsumer(
                            TOPIC,
                            NotificationUpdateDeserializer.class
                    );

                    mockServer.setupFraudCheckMock(mockFraudService, false);
//                    mockServer.setupSendNotificationMock(mockNotificationService);

                    var request = new CustomerRegistrationRequest(
                            "Albus",
                            "Dumbledore",
                            "dumbledore@hogwarts.com"
                    );

                    String requestJson = objectMapper.writeValueAsString(request);

                    mockMvc.perform(post("/v1/customers")
                                    .header("X-Idempotency-Key", "123456789")
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

                    mockServer.verify(mockFraudService, FRAUD_CHECK_URL);

                    var records = consumer.poll(Duration.ofSeconds(5));
                    var record = records.iterator().next();

                    assertThat(record).isNotNull();
                    NotificationUpdate notificationUpdate = record.value();
                    assertThat(notificationUpdate).isNotNull();
                    assertThat(notificationUpdate.toCustomerEmail()).isEqualTo("dumbledore@hogwarts.com");
                    assertThat(notificationUpdate.message()).isEqualTo("Hi. Welcome to Hogwarts. :)");

//                    mockServer.verify(mockNotificationService, SEND_NOTIFICATION_URL);
                    consumer.close();
                }
            }

            @Nested
            @DisplayName("and customer is fraudulent")
            class customerIsFraudulent {

                @Test
                @DisplayName("then customer shouldnt be registered successfully")
                void ShouldntBeRegisterCustomer() throws Exception {
                    mockServer.setupFraudCheckMock(mockFraudService, true);

                    var request = new CustomerRegistrationRequest(
                            "Albus",
                            "Dumbledore",
                            "dumbledore@hogwarts.com"
                    );

                    String requestJson = objectMapper.writeValueAsString(request);

                    mockMvc.perform(post("/v1/customers")
                                    .header("X-Idempotency-Key", "123456789")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(requestJson))
                            .andExpect(status().isForbidden());

                    assertThat(customerRepository.findAll()).isEmpty();

                    mockServer.verify(mockFraudService, FRAUD_CHECK_URL);
                }
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
                                .header("X-Idempotency-Key", "123456789")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson))
                        .andExpect(status().isBadRequest());

                assertThat(customerRepository.findAll()).isEmpty();
            }
        }
    }
}
