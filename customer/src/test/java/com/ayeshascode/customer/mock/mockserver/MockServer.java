package com.ayeshascode.customer.mock.mockserver;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@Component
public class MockServer {

    public void setupFraudCheckMock(WireMockServer mockService, boolean isFraudster)  {
        mockService.stubFor(post(urlPathMatching("/v1/fraud-check/.*"))
                .withHeader("X-Idempotency-Key", matching(".*"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody("{\"isFraudster\": " + isFraudster + "}")));
    }

    public void verify(WireMockServer mockService, String url) {
        mockService.verify(postRequestedFor(urlPathMatching(url)));
    }

    public void stop(WireMockServer mockService) {
        mockService.stop();
    }
}
