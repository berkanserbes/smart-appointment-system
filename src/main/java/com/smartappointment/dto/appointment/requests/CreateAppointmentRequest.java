package com.smartappointment.dto.appointment.requests;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record CreateAppointmentRequest(

        @NotNull(message = "Service provider ID is required")
        Long serviceProviderId,

        @NotNull(message = "Location ID is required")
        Long locationId,

        @NotNull(message = "Start time is required")
        @Future(message = "Start time must be in the future")
        LocalDateTime startTime,

        LocalDateTime endTime,

        @Size(max = 1000, message = "Notes cannot exceed 1000 characters")
        String notes
) {
}
