package com.example.demo.di;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component("SampleComponent2")
@Slf4j
public class SampleComponent2 implements SampleComponent {
    /** getStrの戻り値 */
    private String str = "SampleComponent2";

    @Override
    public String getStr() {
        return this.str;
    }

    @PostConstruct
    public void init() {
        log.info(str + " init");
    }

    @PreDestroy
    public void preDestroy() {
        log.info(str + " preDestroy");
    }
}
