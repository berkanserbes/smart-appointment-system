package com.smartappointment.dto.report.responses;

import java.time.LocalDateTime;

public record ReportResponse(
        long totalAppointments,
        long scheduledCount,
        long confirmedCount,
        long completedCount,
        long cancelledCount,
        long missedCount,
        long totalUsers,
        long totalProviders,
        String period,
        LocalDateTime periodStart,
        LocalDateTime periodEnd) {
}
