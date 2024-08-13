package com.ayeshascode.customer.mock.fraudclient;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@Component
public class FraudClientWireMockServer {

    public void setupFraudCheckMock(WireMockServer mockService, boolean isFraudster)  {
        mockService.stubFor(post(urlPathMatching("/v1/fraud-check/.*"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody("{\"isFraudster\": " + isFraudster + "}")));
    }

    public void verify(WireMockServer mockService) {
        mockService.verify(postRequestedFor(urlPathMatching("/v1/fraud-check/.*")));
    }

    public void stop(WireMockServer mockService) {
        mockService.stop();
    }
}
