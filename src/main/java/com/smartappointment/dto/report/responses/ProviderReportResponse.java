package com.smartappointment.dto.report.responses;

import java.time.LocalDateTime;

public record ProviderReportResponse(
        Long providerId,
        long totalAppointments,
        long scheduledCount,
        long confirmedCount,
        long completedCount,
        long cancelledCount,
        long missedCount,
        LocalDateTime periodStart,
        LocalDateTime periodEnd) {
}
