package com.ayeshascode.customer.producer;

import com.ayeshascode.clients.notification.NotificationUpdate;
import com.ayeshascode.customer.producer.interceptor.OpenTelemetryKafkaProducerInterceptor;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.io.IOException;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class KafkaProducerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.security-protocol}")
    private String securityProtocol;

    private Map<String, Object> producerConfig() {
        return Map.of(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
                CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, securityProtocol,
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, NotificationUpdateSerializer.class,
                ProducerConfig.INTERCEPTOR_CLASSES_CONFIG, OpenTelemetryKafkaProducerInterceptor.class.getName()
        );
    }

    @Bean
    public ProducerFactory<String, NotificationUpdate> producerFactory() {
        Map<String, Object> producerConfig = producerConfig();
        return new DefaultKafkaProducerFactory<>(producerConfig);
    }

    @Bean("notificationUpdateKafkaTemplate")
    KafkaTemplate<String, NotificationUpdate> notificationUpdateKafkaTemplate(
            ProducerFactory<String, NotificationUpdate> producerFactory
    ) {
        return new KafkaTemplate<>(producerFactory);
    }

    public static class NotificationUpdateDeserializer implements Deserializer<NotificationUpdate> {

        private final ObjectMapper objectMapper = new ObjectMapper();

        @Override
        public NotificationUpdate deserialize(String topic, byte[] data) {
            try {
                return objectMapper.readValue(data, NotificationUpdate.class);
            } catch (IOException e) {
                throw new RuntimeException("Failed to deserialize NotificationUpdate", e);
            }
        }
    }

    public static class NotificationUpdateSerializer implements Serializer<NotificationUpdate> {
        private final ObjectMapper objectMapper = new ObjectMapper();

        @Override
        public byte[] serialize(String topic, NotificationUpdate data) {
            try {
                return objectMapper.writeValueAsBytes(data);
            } catch (Exception e) {
                throw new RuntimeException("Failed to serialize NotificationUpdate", e);
            }
        }
    }

}
