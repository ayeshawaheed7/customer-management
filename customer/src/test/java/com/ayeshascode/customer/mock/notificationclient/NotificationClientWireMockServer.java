package com.ayeshascode.customer.mock.notificationclient;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@Component
public class NotificationClientWireMockServer {

    public void setupFraudCheckMock(WireMockServer mockService) {
        mockService.stubFor(post(urlPathMatching("/v1/notifications"))
                .withHeader("X-Idempotency-Key", matching(".*"))
                .withRequestBody(matchingJsonPath("$.toCustomerId"))
                .withRequestBody(matchingJsonPath("$.toCustomerEmail"))
                .withRequestBody(matchingJsonPath("$.message"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)));
    }

    public void verify(WireMockServer mockService) {
        mockService.verify(postRequestedFor(urlPathMatching("/v1/notifications")));
    }

    public void stop(WireMockServer mockService) {
        mockService.stop();
    }
}
