package com.smartappointment.service.interfaces;

import com.smartappointment.dto.report.ReportResponse;

import java.time.LocalDateTime;

public interface IReportService {

    ReportResponse getDashboardOverview();

    ReportResponse getDailyReport();

    ReportResponse getWeeklyReport();

    ReportResponse getMonthlyReport();

    ReportResponse getCustomReport(LocalDateTime start, LocalDateTime end);

    ReportResponse getProviderReport(Long providerId, LocalDateTime start, LocalDateTime end);
}
