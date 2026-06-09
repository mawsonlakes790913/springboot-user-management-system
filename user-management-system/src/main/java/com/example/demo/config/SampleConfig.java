package com.example.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class SampleConfig {

    @Value("${spring.application.name:N/A}")
    private String applicationName;

    @Value("${sample.test:N/A}")
    private String test;

    @PostConstruct
    private void postConstruct() {
        log.info("applicationName={} test={}", applicationName, test);
    }
}
