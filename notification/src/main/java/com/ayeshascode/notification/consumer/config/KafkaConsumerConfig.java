package com.ayeshascode.notification.consumer.config;

import com.ayeshascode.clients.notification.NotificationUpdate;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import java.io.IOException;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.security-protocol}")
    private String securityProtocol;

    @Value("${spring.kafka.consumer.notification-updates.offset}")
    private String consumerOffset;

    private Map<String, Object> consumerConfig() {
        return Map.of(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
                CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, securityProtocol,
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, NotificationUpdateDeserializer.class,
                ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed",
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, consumerOffset
        );
    }

    @Bean("notificationUpdateConsumerFactory")
    ConsumerFactory<String, NotificationUpdate> consumerFactory() {
        Map<String, Object> consumerConfig = consumerConfig();
        return new DefaultKafkaConsumerFactory<>(consumerConfig);
    }

    @Bean("notificationUpdateKafkaListenerContainerFactory")
    ConcurrentKafkaListenerContainerFactory<String, NotificationUpdate> notificationUpdateKafkaListenerContainerFactory(
            ConsumerFactory<String, NotificationUpdate> consumerFactory
    ) {
        ConcurrentKafkaListenerContainerFactory<String, NotificationUpdate> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        return factory;
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
