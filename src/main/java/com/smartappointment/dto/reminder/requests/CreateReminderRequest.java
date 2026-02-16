package com.smartappointment.dto.reminder.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record CreateReminderRequest(

        @NotNull(message = "Appointment ID is required") Long appointmentId,

        @NotBlank(message = "Reminder type is required (EMAIL, SMS, IN_APP)") String type,

        @NotNull(message = "Scheduled time is required") LocalDateTime scheduledAt,

        @Size(max = 500, message = "Message cannot exceed 500 characters") String message) {
}
