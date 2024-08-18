package com.ayeshascode.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import io.opentelemetry.api.trace.Span;
import org.springframework.stereotype.Component;

@Component
public class FeignRequestInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        Span currentSpan = Span.current();
        if (currentSpan.getSpanContext().isValid()) {
            String traceparent = String.format("00-%s-%s-01",
                    currentSpan.getSpanContext().getTraceId(),
                    currentSpan.getSpanContext().getSpanId());
            template.header("traceparent", traceparent);
        }
    }
}


