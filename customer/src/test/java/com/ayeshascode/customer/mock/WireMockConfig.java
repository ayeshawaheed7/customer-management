package com.ayeshascode.customer.mock;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;

@TestConfiguration
@ActiveProfiles("mock-service")
public class WireMockConfig {

    @Bean(initMethod = "start", destroyMethod = "stop")
    public WireMockServer mockFraudService() {
        return new WireMockServer(9091);
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public WireMockServer mockNotificationService() {
        return new WireMockServer(9092);
    }
}