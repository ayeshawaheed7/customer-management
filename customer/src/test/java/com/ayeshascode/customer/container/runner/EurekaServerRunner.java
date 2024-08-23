package com.ayeshascode.customer.container.runner;

import com.ayeshascode.customer.container.EurekaServerContainer;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@Slf4j
public class EurekaServerRunner implements BeforeAllCallback {

    private static final EurekaServerContainer eurekaServer = new EurekaServerContainer();


    @Override
    public void beforeAll(ExtensionContext context) {
        eurekaServer.start();
        System.setProperty("EUREKA_CLIENT_SERVICE_URL_DEFAULT_ZONE", eurekaServer.getEurekaUrl());

        registerServiceWithEureka(
                eurekaServer.getEurekaUrl(),
                "fraud",
                9091
        );
    }

    private void registerServiceWithEureka(String eurekaUrl, String serviceName, int port) {
        RestTemplate restTemplate = new RestTemplate();

        // Construct the registration request body
        String instanceId = serviceName.toLowerCase() + ":" + port;
        String body = "{"
                + "\"instance\": {"
                + "\"instanceId\": \"" + instanceId + "\","
                + "\"hostName\": \"localhost\","
                + "\"app\": \"" + serviceName.toUpperCase() + "\","
                + "\"vipAddress\": \"" + serviceName.toUpperCase() + "\","
                + "\"secureVipAddress\": \"" + serviceName.toUpperCase() + "\","
                + "\"ipAddr\": \"127.0.0.1\","
                + "\"status\": \"UP\","
                + "\"port\": {\"$\": " + port + ", \"@enabled\": true},"
                + "\"dataCenterInfo\": {"
                + "\"@class\": \"com.netflix.appinfo.InstanceInfo$DefaultDataCenterInfo\","
                + "\"name\": \"MyOwn\""
                + "}"
                + "}"
                + "}";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        // Send the registration request to Eureka
        String registrationUrl = eurekaUrl + "apps/" + serviceName.toUpperCase();
        ResponseEntity<String> response = restTemplate.exchange(registrationUrl, HttpMethod.POST, entity, String.class);

        if (response.getStatusCode() == HttpStatus.NO_CONTENT) {
            System.out.println("Service registered successfully with Eureka: " + serviceName);
        } else {
            System.out.println("Failed to register service with Eureka: " + response.getStatusCode());
        }
    }
}
