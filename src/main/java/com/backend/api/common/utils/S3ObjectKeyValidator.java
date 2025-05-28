package com.backend.api.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;
import java.util.List;

@Slf4j
@Component
public class S3ObjectKeyValidator {

    private static final int MAX_KEY_LENGTH = 1024;
    private static final Pattern VALID_KEY_PATTERN = Pattern.compile(
            "^[\\p{Print}\\p{Space}&&[^\\p{Cntrl}]]{1,1024}$"
    );

    public static boolean isValidS3Key(String key) {
        if (key == null || key.isEmpty()) return false;

        // 1. 길이 제한
        if (key.getBytes(StandardCharsets.UTF_8).length > MAX_KEY_LENGTH) {
            return false;
        }

        // 2. 유효 문자 검증 (제어 문자 제거)
        return VALID_KEY_PATTERN.matcher(key).matches();
    }

    private static final List<String> ALLOWED_EXT = List.of("jpg", "png", "pdf");

    public static boolean isAllowedExtension(String filename) {
        int dotIndex = filename.lastIndexOf(".");
        if (dotIndex == -1) return false;

        String ext = filename.substring(dotIndex + 1).toLowerCase();
        return ALLOWED_EXT.contains(ext);
    }

}
