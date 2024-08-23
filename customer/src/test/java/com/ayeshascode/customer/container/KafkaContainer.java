package com.ayeshascode.customer.container;

import org.testcontainers.utility.DockerImageName;

public class KafkaContainer extends org.testcontainers.containers.KafkaContainer {
    private static final int KAFKA_PORT = 9093;

    public KafkaContainer() {
        super(DockerImageName.parse("confluentinc/cp-kafka:latest"));
        withExposedPorts(KAFKA_PORT);
    }

    public String getBootstrapServers() {
        return String.format("PLAINTEXT://%s:%s", getHost(), getMappedPort(KAFKA_PORT));
    }
}
