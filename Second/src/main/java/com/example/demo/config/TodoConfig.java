package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.atomic.AtomicLong;

@Configuration
public class TodoConfig {

    @Bean
    public AtomicLong todoIdGenerator() {
        return new AtomicLong(0);
    }
}