package com.smartappointment.exception;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;

@Builder
public record ErrorResponse(
        int status,
        String error,
        String message,
        String path,
        LocalDateTime timestamp,
        @JsonInclude(JsonInclude.Include.NON_NULL) Map<String, List<String>> validationErrors) {
}
