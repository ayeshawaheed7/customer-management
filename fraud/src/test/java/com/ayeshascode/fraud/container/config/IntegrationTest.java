package com.ayeshascode.fraud.container.config;

import com.ayeshascode.FraudApplication;
import com.ayeshascode.fraud.container.PostgresRunner;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(classes = FraudApplication.class)
@ExtendWith({PostgresRunner.class})
@AutoConfigureMockMvc
public @interface IntegrationTest {}