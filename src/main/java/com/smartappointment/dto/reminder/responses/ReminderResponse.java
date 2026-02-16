package com.smartappointment.dto.reminder.responses;

import java.time.LocalDateTime;

public record ReminderResponse(
        Long id,
        Long appointmentId,
        String type,
        String status,
        LocalDateTime scheduledAt,
        LocalDateTime sentAt,
        String message) {
}
