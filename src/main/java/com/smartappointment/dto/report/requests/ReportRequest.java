package com.smartappointment.dto.report.requests;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record ReportRequest(

        @NotNull(message = "Start date is required") LocalDateTime startDate,

        @NotNull(message = "End date is required") LocalDateTime endDate) {
}
