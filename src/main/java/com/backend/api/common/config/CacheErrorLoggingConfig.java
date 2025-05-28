package com.backend.api.common.config;

import com.backend.api.common.utils.MessageLogger;
import com.backend.api.common.utils.Utils;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.SimpleCacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class CacheErrorLoggingConfig {
    private final Utils utils;
    private final Environment env;

    public CacheErrorLoggingConfig(Utils utils, Environment env) {
        this.utils = utils;
        this.env = env;
    }

    @Bean
    public CacheErrorHandler cacheErrorHandler() {
        return new SimpleCacheErrorHandler() {
            private static final MessageLogger log = new MessageLogger(LoggerFactory.getLogger(CacheErrorHandler.class));

            @Override
            public void handleCacheGetError(RuntimeException exception, Cache cache, Object key) {
                log.errorLog("⚠️ Redis Cache Miss or Get Error - key: {}, reason: {}", key, exception.getMessage());
                utils.sendSlackMessage(
                        env.getProperty("logging.slack.webhook-redis"),
                        "⚠️ Redis Cache Miss or Get Error - key: " + key + ", reason: " + exception.getMessage()
                );
                super.handleCacheGetError(exception, cache, key);
            }

            @Override
            public void handleCachePutError(RuntimeException exception, Cache cache, Object key, Object value) {
                log.errorLog("⚠️ Redis Cache Put Error - key: {}, reason: {}", key, exception.getMessage());
                utils.sendSlackMessage(
                        env.getProperty("logging.slack.webhook-redis"),
                        "⚠️ Redis Cache Put Error - key: " + key + ", reason: " + exception.getMessage()
                );
                super.handleCachePutError(exception, cache, key, value);
            }

            @Override
            public void handleCacheEvictError(RuntimeException exception, Cache cache, Object key) {
                log.errorLog("⚠️ Redis Cache Evict Error - key: {}, reason: {}", key, exception.getMessage());
                utils.sendSlackMessage(
                        env.getProperty("logging.slack.webhook-redis"),
                        "⚠️ Redis Cache Evict Error - key: " + key + ", reason: " + exception.getMessage()
                );
                super.handleCacheEvictError(exception, cache, key);
            }
        };
    }
}
