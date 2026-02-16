package com.smartappointment.dto.provider.responses;

import java.time.LocalDateTime;

public record ServiceProviderResponse(
        Long id,
        String name,
        String title,
        String phone,
        String email,
        String description,
        Long categoryId,
        String categoryName,
        boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}
