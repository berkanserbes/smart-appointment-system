package com.smartappointment.dto.user.responses;

import java.time.LocalDateTime;

public record UserResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        String phone,
        String role,
        boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}
