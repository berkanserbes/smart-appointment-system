package com.smartappointment.service.interfaces;

import java.time.LocalDateTime;

import com.smartappointment.dto.report.responses.ProviderReportResponse;
import com.smartappointment.dto.report.responses.ReportResponse;

public interface IReportService {

    ReportResponse getDashboardOverview();

    ReportResponse getDailyReport();

    ReportResponse getWeeklyReport();

    ReportResponse getMonthlyReport();

    ReportResponse getCustomReport(LocalDateTime start, LocalDateTime end);

    ProviderReportResponse getProviderReport(Long providerId, LocalDateTime start, LocalDateTime end);
}
