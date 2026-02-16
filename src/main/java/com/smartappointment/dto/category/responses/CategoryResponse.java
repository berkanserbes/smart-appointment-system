package com.smartappointment.dto.category.responses;

import java.time.LocalDateTime;

public record CategoryResponse(
        Long id,
        String name,
        String description,
        String colorCode,
        Integer defaultDurationMinutes,
        boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}
