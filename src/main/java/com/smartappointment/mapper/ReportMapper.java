package com.smartappointment.mapper;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.smartappointment.dto.report.responses.ProviderReportResponse;
import com.smartappointment.dto.report.responses.ReportResponse;

@Component
public class ReportMapper {

    public ReportResponse toResponse(
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

        return new ReportResponse(
                totalAppointments,
                scheduledCount,
                confirmedCount,
                completedCount,
                cancelledCount,
                missedCount,
                totalUsers,
                totalProviders,
                period,
                periodStart,
                periodEnd);
    }

    public ProviderReportResponse toProviderResponse(
            Long providerId,
            long totalAppointments,
            long scheduledCount,
            long confirmedCount,
            long completedCount,
            long cancelledCount,
            long missedCount,
            LocalDateTime periodStart,
            LocalDateTime periodEnd) {

        return new ProviderReportResponse(
                providerId,
                totalAppointments,
                scheduledCount,
                confirmedCount,
                completedCount,
                cancelledCount,
                missedCount,
                periodStart,
                periodEnd);
    }
}
