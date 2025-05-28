package com.backend.api.service.aws;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

public class S3KeyValidator implements ConstraintValidator<IValidS3Key, String> {

    private static final int MAX_KEY_LENGTH = 1024;

    private static final Pattern VALID_KEY_PATTERN = Pattern.compile(
            "^[\\p{Print}\\p{Space}&&[^\\p{Cntrl}]]{1,1024}$"
    );

    @Override
    public boolean isValid(String key, ConstraintValidatorContext context) {
        if (key == null || key.isEmpty()) return false;
        if (key.getBytes(StandardCharsets.UTF_8).length > MAX_KEY_LENGTH) return false;
        return VALID_KEY_PATTERN.matcher(key).matches();
    }
}

