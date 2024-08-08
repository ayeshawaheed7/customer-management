package com.ayeshascode.customer.mockClient;

import com.ayeshascode.customer.model.FraudCheckResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.test.web.client.response.MockRestResponseCreators;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.net.URISyntaxException;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;

@Component
@Scope("prototype")
public class FraudClientMock {

    private MockRestServiceServer mockRestServiceServer;

    @Autowired
    @Qualifier("fraudApiRestTemplate")
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${fraud-service.url}")
    private String fraudServiceUrl;

    @PostConstruct
    public void setup() {
        this.mockRestServiceServer = MockRestServiceServer.bindTo(restTemplate).build();
    }

    public void setupFraudCheckMock(boolean isFraudster) throws JsonProcessingException {
        FraudCheckResponse response = new FraudCheckResponse(isFraudster);
        mockRestServiceServer.expect(MockRestRequestMatchers.method(HttpMethod.POST))
                .andExpect(requestTo(Matchers.startsWith(fraudServiceUrl + "/v1/fraud-check/")))
                .andExpect(MockRestRequestMatchers.header("X-Idempotency-Key", Matchers.notNullValue()))
                .andRespond(
                        MockRestResponseCreators.withStatus(HttpStatus.OK)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(objectMapper.writeValueAsString(response))
                );
    }

    public void verify() {
        mockRestServiceServer.verify();
    }

    public void reset() {
        mockRestServiceServer.reset();
    }
}


