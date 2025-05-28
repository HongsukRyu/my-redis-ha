package com.backend.api.common.utils;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class JasyptEncryptorUtilTest {
    // Junit5
    private static StandardPBEStringEncryptor encryptor;
    private static final String secretKey = "test-secret-key"; // 테스트용 키

    @BeforeAll
    static void setup() {
        encryptor = new StandardPBEStringEncryptor();
        String password = System.getenv("JASYPT_ENCRYPTOR_PASSWORD") == null ? "test-secret-key" : System.getenv("JASYPT_ENCRYPTOR_PASSWORD");
        System.out.println("🔑 Jasypt Encryptor Password: " + password);
        encryptor.setPassword(password);
        encryptor.setAlgorithm("PBEWithMD5AndDES");
        encryptor.setStringOutputType("base64");
        encryptor.setProviderName("SunJCE");
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
                "12345678"
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