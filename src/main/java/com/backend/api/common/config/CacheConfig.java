package com.backend.api.common.config;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {

        // 커스터마이징된 ObjectMapper 생성
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.activateDefaultTyping(
                LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY
        );

        // ObjectMapper를 사용하는 RedisSerializer 구성
        GenericJackson2JsonRedisSerializer customSerializer = new GenericJackson2JsonRedisSerializer(objectMapper);

        // 기본 캐시 설정: 30분 TTL, String Key Serializer, JSON Value Serializer
        RedisCacheConfiguration defaultCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(60))
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(customSerializer)); // 커스터마이징된 ObjectMapper를 사용하는 RedisSerializer 설정

        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
        Duration specificTtl = Duration.ofSeconds(60);
        // AclPolicy 관련 캐시 설정
        cacheConfigurations.put("methodAllowed", defaultCacheConfig.entryTtl(specificTtl));

        // Tenant 관련 캐시 설정
        cacheConfigurations.put("isValidTenant", defaultCacheConfig);

        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(defaultCacheConfig) // 기본 캐시 설정 적용
                .withInitialCacheConfigurations(cacheConfigurations) // 특정 캐시 설정 적용
                .build();
    }
}