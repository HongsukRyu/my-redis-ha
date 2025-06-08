package com.backend.api.common.config;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.Security;

/**
 * JasyptConfig for properties encryption
 *
 */
@Configuration
@EnableEncryptableProperties
public class JasyptConfig {

    // static 블록으로 BouncyCastle 프로바이더를 가능한 빨리 등록
    static {
        if (Security.getProvider("BC") == null) {
            Security.addProvider(new BouncyCastleProvider());
            System.out.println("🔐 BouncyCastle 프로바이더가 static 블록에서 등록되었습니다.");
        }
    }

    @Bean("jasyptEncryptor")
    public StringEncryptor jasyptEncryptor() {

        String password = System.getenv("JASYPT_ENCRYPTOR_PASSWORD");
        
        // 환경변수가 없을 경우 기본값 사용 (개발 환경)
        if (password == null || password.isEmpty()) {
            password = "test-secret-key";
            System.out.println("⚠️  환경변수 'JASYPT_ENCRYPTOR_PASSWORD'가 설정되지 않아 기본값을 사용합니다.");
        }

        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        config.setPassword(password);
        // 테스트와 동일한 강력한 암호화 알고리즘 사용
        config.setAlgorithm("PBEWITHSHA256AND256BITAES-CBC-BC");
        config.setKeyObtentionIterations("1000");
        config.setPoolSize("1");
        config.setProviderName("BC"); // BouncyCastle 프로바이더 사용
        config.setStringOutputType("base64");
        config.setIvGeneratorClassName("org.jasypt.iv.RandomIvGenerator");

        encryptor.setConfig(config);
        
        System.out.println("🔐 Jasypt 설정 완료:");
        System.out.println("  - 알고리즘: " + config.getAlgorithm());
        System.out.println("  - 프로바이더: " + config.getProviderName());
        System.out.println("  - 출력 타입: " + config.getStringOutputType());
        
        return encryptor;
    }
}
