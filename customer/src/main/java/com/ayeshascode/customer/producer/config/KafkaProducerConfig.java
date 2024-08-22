package com.ayeshascode.customer.producer.config;

import com.ayeshascode.clients.notification.NotificationUpdate;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

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
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class
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
}
