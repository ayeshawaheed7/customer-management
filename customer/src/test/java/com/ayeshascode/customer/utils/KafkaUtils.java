package com.ayeshascode.customer.utils;

import com.ayeshascode.clients.notification.NotificationUpdate;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.io.IOException;
import java.util.Collections;
import java.util.Properties;

public class KafkaUtils {

    public static <T> KafkaConsumer<String, T> createConsumer(
            String topic,
            Class<? extends org.apache.kafka.common.serialization.Deserializer<T>> valueDeserializerClass
    ) {

        Properties consumerProps = new Properties();
        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, System.getProperty("KAFKA_BOOTSTRAP_SERVERS_URL"));
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, "test-group");
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, valueDeserializerClass.getName());
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        KafkaConsumer<String, T> consumer = new KafkaConsumer<>(consumerProps);
        consumer.subscribe(Collections.singletonList(topic));
        return consumer;
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

}
