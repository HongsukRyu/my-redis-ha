package com.backend.api.common.config;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * JasyptConfig for properties encryption
 *
 */
@Configuration
@EnableEncryptableProperties
public class JasyptConfig {

    @Bean("jasyptEncryptor")
    public StringEncryptor jasyptEncryptor() {

        String password = System.getenv("JASYPT_ENCRYPTOR_PASSWORD"); // 또는 하드코딩 테스트용 키

        if (password == null || password.isEmpty()) {
            System.err.println("환경변수 'JASYPT_ENCRYPTOR_PASSWORD'가 설정되지 않았습니다.");
            System.exit(1);
        }

        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        config.setPassword(password); // ✔️ 필수
        config.setAlgorithm("PBEWithMD5AndDES"); // ✔️ 필수
        config.setKeyObtentionIterations("1000");
        config.setPoolSize("1");
        config.setProviderName("SunJCE");
        config.setStringOutputType("base64");

        encryptor.setConfig(config);
        return encryptor;
    }
}
