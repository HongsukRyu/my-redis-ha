package com.backend.api.common.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ 설정 클래스
 * 
 * @author backend-api
 */
@Configuration
@Slf4j
public class RabbitMQConfig {
    
    // 큐 이름 상수
    public static final String USER_QUEUE = "user.queue";
    public static final String NOTIFICATION_QUEUE = "notification.queue";
    public static final String REDIS_FALLBACK_QUEUE = "redis.fallback.queue";
    public static final String AUDIT_QUEUE = "audit.queue";
    
    // Exchange 이름 상수
    public static final String TOPIC_EXCHANGE = "backend.topic.exchange";
    public static final String DIRECT_EXCHANGE = "backend.direct.exchange";
    
    // Routing Key 상수
    public static final String USER_ROUTING_KEY = "user.event";
    public static final String NOTIFICATION_ROUTING_KEY = "notification.send";
    public static final String REDIS_FALLBACK_ROUTING_KEY = "redis.fallback";
    public static final String AUDIT_ROUTING_KEY = "audit.log";
    
    // Dead Letter Queue 설정
    public static final String DLX_EXCHANGE = "backend.dlx.exchange";
    public static final String DLQ_QUEUE = "backend.dlq";
    public static final String DLQ_ROUTING_KEY = "dlq";
    
    /**
     * JSON 메시지 컨버터 설정
     */
    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
    
    /**
     * RabbitTemplate 설정
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        
        // 메시지 전송 실패 시 로그 출력
        template.setConfirmCallback((correlationData, ack, cause) -> {
            if (!ack) {
                log.error("💥 RabbitMQ 메시지 전송 실패: {}", cause);
            }
        });
        
        template.setReturnsCallback(returned -> {
            log.error("💥 RabbitMQ 메시지 반환: {}", returned.getMessage());
        });
        
        return template;
    }
    
    /**
     * 리스너 컨테이너 팩토리 설정
     */
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter());
        factory.setConcurrentConsumers(3);
        factory.setMaxConcurrentConsumers(10);
        factory.setPrefetchCount(10);
        return factory;
    }
    
    // ==================== Exchange 설정 ====================
    
    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(TOPIC_EXCHANGE, true, false);
    }
    
    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(DIRECT_EXCHANGE, true, false);
    }
    
    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(DLX_EXCHANGE, true, false);
    }
    
    // ==================== Queue 설정 ====================
    
    /**
     * 사용자 이벤트 큐
     */
    @Bean
    public Queue userQueue() {
        return QueueBuilder.durable(USER_QUEUE)
                .withArgument("x-dead-letter-exchange", DLX_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", DLQ_ROUTING_KEY)
                .withArgument("x-message-ttl", 60000) // 1분 TTL
                .build();
    }
    
    /**
     * 알림 큐
     */
    @Bean
    public Queue notificationQueue() {
        return QueueBuilder.durable(NOTIFICATION_QUEUE)
                .withArgument("x-dead-letter-exchange", DLX_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", DLQ_ROUTING_KEY)
                .build();
    }
    
    /**
     * Redis Fallback 큐 (Redis 장애 시 사용)
     */
    @Bean
    public Queue redisFallbackQueue() {
        return QueueBuilder.durable(REDIS_FALLBACK_QUEUE)
                .withArgument("x-dead-letter-exchange", DLX_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", DLQ_ROUTING_KEY)
                .build();
    }
    
    /**
     * 감사 로그 큐
     */
    @Bean
    public Queue auditQueue() {
        return QueueBuilder.durable(AUDIT_QUEUE)
                .withArgument("x-dead-letter-exchange", DLX_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", DLQ_ROUTING_KEY)
                .build();
    }
    
    /**
     * Dead Letter Queue
     */
    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable(DLQ_QUEUE).build();
    }
    
    // ==================== Binding 설정 ====================
    
    @Bean
    public Binding userBinding() {
        return BindingBuilder.bind(userQueue())
                .to(topicExchange())
                .with(USER_ROUTING_KEY);
    }
    
    @Bean
    public Binding notificationBinding() {
        return BindingBuilder.bind(notificationQueue())
                .to(directExchange())
                .with(NOTIFICATION_ROUTING_KEY);
    }
    
    @Bean
    public Binding redisFallbackBinding() {
        return BindingBuilder.bind(redisFallbackQueue())
                .to(directExchange())
                .with(REDIS_FALLBACK_ROUTING_KEY);
    }
    
    @Bean
    public Binding auditBinding() {
        return BindingBuilder.bind(auditQueue())
                .to(topicExchange())
                .with(AUDIT_ROUTING_KEY);
    }
    
    @Bean
    public Binding deadLetterBinding() {
        return BindingBuilder.bind(deadLetterQueue())
                .to(deadLetterExchange())
                .with(DLQ_ROUTING_KEY);
    }
} 