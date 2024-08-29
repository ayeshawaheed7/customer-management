package com.ayeshascode.customer.producer.interceptor;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.*;
import io.opentelemetry.context.Scope;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;

import java.util.Map;

@Slf4j
public class OpenTelemetryKafkaProducerInterceptor<K, V> implements ProducerInterceptor<K, V> {

    @Override
    public ProducerRecord<K, V> onSend(ProducerRecord<K, V> record) {
        Span currentSpan = Span.current();
        SpanContext spanContext = currentSpan.getSpanContext();

        if (spanContext.isValid()) {
            String traceparent = String.format("00-%s-%s-01",
                    spanContext.getTraceId(),
                    spanContext.getSpanId());

            // Add traceparent to the Kafka headers
            Header traceparentHeader = new RecordHeader("traceparent", traceparent.getBytes());
            record.headers().add(traceparentHeader);

            log.info("Added traceparent header: {}", traceparent);
        } else {
            log.warn("No valid span context found. Traceparent header not added.");
        }

        return record;
    }

    // Get the global tracer
//    private static final Tracer tracer = GlobalOpenTelemetry.getTracer("customer");

//    @Override
//    public ProducerRecord<K, V> onSend(ProducerRecord<K, V> record) {
//        // Start a new span for the Kafka producer send operation
//        Span span = tracer.spanBuilder("kafka.producer.send")
//                .setSpanKind(SpanKind.PRODUCER)
//                .startSpan();
//
//        log.info("create producer span: {}", span);
//        try (Scope scope = span.makeCurrent()) {
//            // Set the span as the current span
//            SpanContext spanContext = span.getSpanContext();
//
//            if (spanContext.isValid()) {
//                String traceparent = String.format("00-%s-%s-01",
//                        spanContext.getTraceId(),
//                        spanContext.getSpanId());
//
//                // Add traceparent to the Kafka headers
//                Header traceparentHeader = new RecordHeader("traceparent", traceparent.getBytes());
//                record.headers().add(traceparentHeader);
//
//                log.info("Added traceparent header: {}", traceparent);
//            } else {
//                log.warn("No valid span context found. Traceparent header not added.");
//            }
//
//            return record;
//        } finally {
//            // End the producer span to capture the send operation
//            span.end();
//            log.info("End the producer span to capture the send operation: {}", span);
//        }
//    }


    @Override
    public void onAcknowledgement(RecordMetadata metadata, Exception exception) {
        // Optionally handle acknowledgements
    }

    @Override
    public void close() {
        // Optionally handle closing resources
    }

    @Override
    public void configure(Map<String, ?> configs) {
        // Optionally handle configuration
    }
}


