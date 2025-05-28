package com.backend.api.common.config;

import com.backend.api.common.listener.KeyExpirationListener;
import com.backend.api.common.utils.Utils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

@Configuration
public class RedisKeyExpirationListenerConfig {

    private static final String KEY_EVENT_CHANNEL = "__keyevent@0__:expired";

    private final Utils utils;

    private final Environment env;

    public RedisKeyExpirationListenerConfig(Utils utils, Environment env) {
        this.utils = utils;
        this.env = env;
    }

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(
            RedisConnectionFactory connectionFactory,
            MessageListenerAdapter listenerAdapter
    ) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(listenerAdapter, new PatternTopic(KEY_EVENT_CHANNEL));
        return container;
    }

    @Bean
    public MessageListenerAdapter listenerAdapter(KeyExpirationListener listener) {
        return new MessageListenerAdapter(listener);
    }

    @Bean
    public KeyExpirationListener keyExpirationListener() {
        return new KeyExpirationListener(utils, env);
    }
}
