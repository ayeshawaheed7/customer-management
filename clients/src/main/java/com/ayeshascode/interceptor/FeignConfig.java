package com.ayeshascode.interceptor;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    @Bean
    public RequestInterceptor traceFeignRequestInterceptor() {
        return new FeignRequestInterceptor();
    }
}

