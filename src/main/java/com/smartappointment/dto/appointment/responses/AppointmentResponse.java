package com.smartappointment.dto.appointment.responses;

import java.time.LocalDateTime;

public record AppointmentResponse(
                Long id,
                LocalDateTime startTime,
                LocalDateTime endTime,
                String status,
                String notes,

                // Cancellation info
                String cancellationReason,
                LocalDateTime cancelledAt,
                String cancelledBy,

                // Confirmation info
                LocalDateTime confirmedAt,

                // User info
                Long userId,
                String userName,

                // Provider info
                Long serviceProviderId,
                String serviceProviderName,
                String serviceProviderTitle,

                // Location info
                Long locationId,
                String locationName,
                String locationAddress,

                LocalDateTime createdAt,
                LocalDateTime updatedAt) {
}
