package com.backend.api.common.listener;

import com.backend.api.common.utils.MessageLogger;
import com.backend.api.common.utils.Utils;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Component
public class KeyExpirationListener implements MessageListener {

    private static final MessageLogger logger = new MessageLogger(LoggerFactory.getLogger(KeyExpirationListener.class));

    private final Utils utils;
    private final Environment env;

    public KeyExpirationListener(Utils utils, Environment env) {
        this.utils = utils;
        this.env = env;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String expiredKey = message.toString();
//        logger.infoLog("🧨 Redis Key TTL Expired - key: {}", expiredKey);

        // expired event slack msg
//        utils.sendSlackMessage(env.getProperty("logging.slack.webhook-redis"), "🧨 Redis Key TTL Expired - key: " + expiredKey);

        // Optional: key prefix 기준 분기 처리 가능
        if (expiredKey.startsWith("refreshToken:")) {
            logger.infoLog("🔔 사용자 RefreshToken 만료됨: {}", expiredKey);
            // 알림 전송, DB 업데이트 등 추가 처리
        }
    }
}