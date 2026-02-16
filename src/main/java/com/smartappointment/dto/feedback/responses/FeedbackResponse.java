package com.smartappointment.dto.feedback.responses;

import java.time.LocalDateTime;

public record FeedbackResponse(
        Long id,
        Long appointmentId,
        String serviceProviderName,
        Long userId,
        String userName,
        Integer rating,
        String comment,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}
