package com.backend.api.common.utils;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.security.Provider;
import java.security.Security;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class JasyptEncryptorUtilTest {
    // Junit5
    private static StandardPBEStringEncryptor encryptor;
    private static final String secretKey = "test-secret-key"; // 테스트용 키

    @BeforeAll
    static void setup() {
        // BouncyCastle 프로바이더 추가
        Security.addProvider(new BouncyCastleProvider());
        
        // 사용 가능한 암호화 알고리즘 출력 (디버깅용)
        printAvailableAlgorithms();
        
        encryptor = new StandardPBEStringEncryptor();
        String password = System.getenv("JASYPT_ENCRYPTOR_PASSWORD") == null ? "test-secret-key" : System.getenv("JASYPT_ENCRYPTOR_PASSWORD");
        System.out.println("🔑 Jasypt Encryptor Password: " + password);
        encryptor.setPassword(password);
        
        // BouncyCastle에서 지원하는 강력한 알고리즘 사용
        // PBEWITHSHA256AND256BITAES-CBC-BC는 BouncyCastle에서 지원하는 강력한 AES 256bit 알고리즘
        encryptor.setAlgorithm("PBEWITHSHA256AND256BITAES-CBC-BC");
        encryptor.setStringOutputType("base64");
        encryptor.setProviderName("BC"); // BouncyCastle 프로바이더 명시
    }
    
    private static void printAvailableAlgorithms() {
        Provider bc = Security.getProvider("BC");
        if (bc != null) {
            Set<Provider.Service> services = bc.getServices();
            System.out.println("🔍 Available BouncyCastle algorithms:");
            services.stream()
                .filter(s -> s.getType().equals("SecretKeyFactory"))
                .filter(s -> s.getAlgorithm().toLowerCase().contains("pbe"))
                .forEach(s -> System.out.println("  - " + s.getAlgorithm()));
        }
    }

    @Test
    @DisplayName("Jasypt 암호화")
    void testEncryptionAndDecryption() {
        // url
        // username
        // password
        String[] targets = {
                "jdbc:mysql://localhost:3306/TESTDB?serverTimezone=Asia/Seoul&characterEncoding=UTF-8&allowMultiQueries=true",
                "root",
                "cnrqhrdl1!"
        };
        List<String> encryptedList = new ArrayList();

        for (String plain : targets) {
            String encrypted = encryptor.encrypt(plain);
            String decrypted = encryptor.decrypt(encrypted);
            encryptedList.add(encrypted);

            System.out.println("🔐 Plain:     " + plain);
            System.out.println("🔒 Encrypted: ENC(" + encrypted + ")");
            System.out.println("🔓 Decrypted: " + decrypted);
            System.out.println("------------------------------------------------");

            assertEquals(plain, decrypted, "복호화 결과가 원문과 일치하지 않습니다.");
        }
        System.out.println("url: ENC(%s)".formatted(encryptedList.get(0)));
        System.out.println("username: ENC(%s)".formatted(encryptedList.get(1)));
        System.out.println("password: ENC(%s)".formatted(encryptedList.get(2)));
    }
}