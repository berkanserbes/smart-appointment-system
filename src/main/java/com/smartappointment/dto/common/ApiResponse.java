package com.smartappointment.dto.common;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
        boolean success,
        int statusCode,
        String message,
        T data,
        Object error,
        String path,
        LocalDateTime timestamp) {

    public static <T> ApiResponse<T> success(int statusCode, String message, T data, String path) {
        return new ApiResponse<>(
                true,
                statusCode,
                message,
                data,
                null,
                path,
                LocalDateTime.now());
    }

    public static <T> ApiResponse<T> error(int statusCode, String message, Object error, String path) {
        return new ApiResponse<>(
                false,
                statusCode,
                message,
                null,
                error,
                path,
                LocalDateTime.now());
    }
}
