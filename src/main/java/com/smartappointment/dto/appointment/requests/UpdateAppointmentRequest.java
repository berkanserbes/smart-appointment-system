package com.smartappointment.dto.appointment.requests;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record UpdateAppointmentRequest(

        Long serviceProviderId,

        Long locationId,

        @Future(message = "Start time must be in the future")
        LocalDateTime startTime,

        LocalDateTime endTime,

        @Size(max = 1000, message = "Notes cannot exceed 1000 characters")
        String notes
) {
}
