package com.backend.api.service.aws;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * S3 키 유효성 검증을 위한 어노테이션입니다.
 * 이 어노테이션은 S3 객체 키의 유효성을 검사하는 데 사용됩니다.
 *
 * @see S3KeyValidator
 */
@Documented
@Constraint(validatedBy = S3KeyValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface IValidS3Key {
    String message() default "유효하지 않은 S3 키입니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}