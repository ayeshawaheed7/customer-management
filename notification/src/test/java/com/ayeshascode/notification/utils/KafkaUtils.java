package com.ayeshascode.notification.utils;

import com.ayeshascode.notification.consumer.config.KafkaConsumerConfig;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

public class KafkaUtils {

    public static <T> KafkaProducer<String, T> createProducer(
            Class<? extends org.apache.kafka.common.serialization.Serializer<T>> valueSerializerClass
    ) {

        Properties producerProps = new Properties();
        producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, System.getProperty("KAFKA_BOOTSTRAP_SERVERS_URL"));
        producerProps.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, System.getProperty("KAFKA_BOOTSTRAP_SERVERS_SECURITY_PROTOCOL"));
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaConsumerConfig.NotificationUpdateSerializer.class);

        return new KafkaProducer<>(producerProps);
    }
}
