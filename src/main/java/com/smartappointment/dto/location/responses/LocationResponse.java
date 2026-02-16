package com.smartappointment.dto.location.responses;

import java.time.LocalDateTime;

public record LocationResponse(
        Long id,
        String name,
        String address,
        String city,
        boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}
