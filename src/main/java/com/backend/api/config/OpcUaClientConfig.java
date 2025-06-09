package com.backend.api.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableScheduling
@Slf4j
public class OpcUaClientConfig {
    
    @Bean(name = "opcUaTaskExecutor")
    public Executor opcUaTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("OpcUa-");
        executor.setRejectedExecutionHandler((r, executor1) -> {
            log.warn("OPC-UA Task rejected: {}", r.toString());
        });
        executor.initialize();
        return executor;
    }
} 