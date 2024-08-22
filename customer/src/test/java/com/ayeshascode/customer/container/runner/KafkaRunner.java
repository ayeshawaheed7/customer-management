package com.ayeshascode.customer.container.runner;

import com.ayeshascode.customer.container.KafkaContainer;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class KafkaRunner implements BeforeAllCallback {

    private static final KafkaContainer kafkaContainer = new KafkaContainer();

    @Override
    public void beforeAll(ExtensionContext context) {
        kafkaContainer.start();
        System.setProperty("KAFKA_BOOTSTRAP_SERVERS_URL", kafkaContainer.getBootstrapServers());
        System.setProperty("KAFKA_BOOTSTRAP_SERVERS_SECURITY_PROTOCOL", obtainSecurityProtocol());
    }

    private static String obtainSecurityProtocol() {
        return "PLAINTEXT";
    }

    public static void stopContainer() {
        kafkaContainer.stop();
    }
}
