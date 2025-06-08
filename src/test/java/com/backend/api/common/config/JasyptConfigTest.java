package com.backend.api.common.config;

import org.jasypt.encryption.StringEncryptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JasyptConfigTest {

    private JasyptConfig jasyptConfig;
    private StringEncryptor stringEncryptor;

    @BeforeEach
    void setUp() {
        // 환경변수 설정
        System.setProperty("JASYPT_ENCRYPTOR_PASSWORD", "test-secret-key");
        
        jasyptConfig = new JasyptConfig();
        stringEncryptor = jasyptConfig.jasyptEncryptor();
    }

    @Test
    @DisplayName("JasyptConfig에서 StringEncryptor Bean이 정상적으로 생성되는지 테스트")
    void testJasyptEncryptorCreation() {
        assertNotNull(stringEncryptor, "StringEncryptor가 정상적으로 생성되어야 합니다.");
    }

    @Test
    @DisplayName("암호화/복호화가 정상적으로 작동하는지 테스트")
    void testEncryptionDecryption() {
        String plainText = "테스트 문자열";
        
        // 암호화
        String encrypted = stringEncryptor.encrypt(plainText);
        assertNotNull(encrypted, "암호화된 결과가 null이 아니어야 합니다.");
        assertNotEquals(plainText, encrypted, "암호화된 값은 원본과 달라야 합니다.");
        
        // 복호화
        String decrypted = stringEncryptor.decrypt(encrypted);
        assertEquals(plainText, decrypted, "복호화 결과가 원본과 일치해야 합니다.");
        
        System.out.println("🔐 원본: " + plainText);
        System.out.println("🔒 암호화: " + encrypted);
        System.out.println("🔓 복호화: " + decrypted);
    }

    @Test
    @DisplayName("새로운 값들을 암호화하여 datasource 설정에 사용할 수 있는 값들 생성")
    void generateNewEncryptedValues() {
        // 새로운 값들을 암호화해서 출력
        String[] plainValues = {
                "jdbc:mysql://localhost:3306/TESTDB?serverTimezone=Asia/Seoul&characterEncoding=UTF-8&allowMultiQueries=true",
                "root",
                "test1234"
        };

        System.out.println("\n🔑 새로 생성된 암호화 값들:");
        System.out.println("=================================================");
        
        for (int i = 0; i < plainValues.length; i++) {
            String encrypted = stringEncryptor.encrypt(plainValues[i]);
            String[] labels = {"url", "username", "password"};
            
            System.out.println(labels[i] + ": ENC(" + encrypted + ")");
            
            // 복호화 검증
            String decrypted = stringEncryptor.decrypt(encrypted);
            assertEquals(plainValues[i], decrypted, labels[i] + " 복호화가 정상적으로 작동해야 합니다.");
        }
        
        System.out.println("=================================================");
        System.out.println("✅ 위의 값들을 application-dev.yml에 복사해서 사용하세요!");
    }
} 