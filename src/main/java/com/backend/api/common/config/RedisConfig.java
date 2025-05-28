package com.backend.api.common.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.*;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class RedisConfig {

    private final RedisProperties redisProperties;

    @Bean
    @Profile("!prod")
    public LettuceConnectionFactory localRedisConnectionFactory() {
        RedisStandaloneConfiguration standaloneConfig = new RedisStandaloneConfiguration();
        standaloneConfig.setHostName("localhost");
        standaloneConfig.setPort(6379);

        LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
                .commandTimeout(Duration.ofSeconds(10)) // 타임아웃 명시
                .shutdownTimeout(Duration.ofMillis(300))
                .build();

        LettuceConnectionFactory factory = new LettuceConnectionFactory(standaloneConfig, clientConfig);
        factory.afterPropertiesSet();

        // Keyspace notification 설정
        try (RedisConnection connection = factory.getConnection()) {
            String currentSetting = (String) connection.getConfig("notify-keyspace-events").get("notify-keyspace-events");
            if (currentSetting == null || !currentSetting.contains("E") || !currentSetting.contains("x")) {
                connection.setConfig("notify-keyspace-events", "Ex");
                System.out.println("✅ Redis notify-keyspace-events 설정 적용됨: Ex");
            } else {
                System.out.println("ℹ️ Redis notify-keyspace-events 이미 설정되어 있음: " + currentSetting);
            }
        } catch (Exception e) {
            System.err.println("⚠️ Redis notify-keyspace-events 설정 중 오류 발생: " + e.getMessage());
        }

        return factory;

    }

    @Bean
    @Profile("prod")
    public LettuceConnectionFactory redisConnectionFactory() {
        LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
                .commandTimeout(Duration.ofSeconds(10)) // 타임아웃 명시
                .shutdownTimeout(Duration.ofMillis(300))
                .build();

        LettuceConnectionFactory factory = null;

        if (redisProperties.getSentinel() != null && redisProperties.getSentinel().getMaster() != null) {
            // Sentinel 모드
            RedisSentinelConfiguration sentinelConfig = new RedisSentinelConfiguration()
                    .master(redisProperties.getSentinel().getMaster());

            List<RedisNode> sentinelNodes = redisProperties.getSentinel().getNodes().stream()
                    .map(node -> {
                        String[] parts = node.split(":");
                        return new RedisNode(parts[0], Integer.parseInt(parts[1]));
                    })
                    .toList();

            sentinelConfig.setSentinels(sentinelNodes);

            if (redisProperties.getPassword() != null) {
                sentinelConfig.setPassword(RedisPassword.of(redisProperties.getPassword()));
            }

            factory = new LettuceConnectionFactory(sentinelConfig, clientConfig);
        }

        assert factory != null;
        factory.afterPropertiesSet(); // 필수

        // Keyspace notification 설정
        try (RedisConnection connection = factory.getConnection()) {
            String currentSetting = (String) connection.getConfig("notify-keyspace-events").get("notify-keyspace-events");
            if (currentSetting == null || !currentSetting.contains("E") || !currentSetting.contains("x")) {
                connection.setConfig("notify-keyspace-events", "Ex");
                System.out.println("✅ Redis notify-keyspace-events 설정 적용됨: Ex");
            } else {
                System.out.println("ℹ️ Redis notify-keyspace-events 이미 설정되어 있음: " + currentSetting);
            }
        } catch (Exception e) {
            System.err.println("⚠️ Redis notify-keyspace-events 설정 중 오류 발생: " + e.getMessage());
        }

        return factory;
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(LettuceConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // ObjectMapper 설정 추가
        // Redis 역직렬화 시, 타입 추론이 안되서 발생하는 문제 방지
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.activateDefaultTyping(
                LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY
        );

        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(mapper);

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(serializer);

        template.afterPropertiesSet();
        return template;
    }

}