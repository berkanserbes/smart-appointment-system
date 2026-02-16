package com.smartappointment.dto.provider.responses;

import java.time.LocalDateTime;

public record ServiceProviderDetailResponse(
        Long id,
        String name,
        String title,
        String phone,
        String email,
        String description,
        Long categoryId,
        String categoryName,
        boolean active,
        Double averageRating,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}
