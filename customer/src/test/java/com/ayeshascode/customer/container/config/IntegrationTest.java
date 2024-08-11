package com.ayeshascode.customer.container.config;

import com.ayeshascode.customer.CustomerApplication;
import com.ayeshascode.customer.container.runner.EurekaServerRunner;
import com.ayeshascode.customer.container.runner.PostgresRunner;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(classes = CustomerApplication.class)
@ExtendWith({PostgresRunner.class, EurekaServerRunner.class})
@AutoConfigureMockMvc
public @interface IntegrationTest {}