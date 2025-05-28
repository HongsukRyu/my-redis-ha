package com.backend.api.common.config;

/**
 * DTO interface of public builder pattern
 *
 * @since 2021.11.17
 * @param <T>
 */
public interface CommonBuilder<T> {
    T build();
}
