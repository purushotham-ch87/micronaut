package com.shree.cloudweb.product;

import io.micronaut.serde.annotation.Serdeable;

import java.time.Instant;

@Serdeable
public record ProductResponse<T>(
        int statusCode,
        String message,
        T data,
        Instant timestamp
) {
    public static <T> ProductResponse<T> success(int code, String message, T data, Instant timestamp) {
        return new ProductResponse<>(code, message, data, Instant.now());
    }
}