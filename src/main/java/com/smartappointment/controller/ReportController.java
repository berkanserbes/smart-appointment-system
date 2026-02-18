package com.smartappointment.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.smartappointment.config.swagger.ApiResponseAnnotations.GetResponses;
import com.smartappointment.config.swagger.ApiResponseAnnotations.StandardResponses;
import com.smartappointment.dto.report.requests.ReportRequest;
import com.smartappointment.dto.report.responses.ProviderReportResponse;
import com.smartappointment.dto.report.responses.ReportResponse;
import com.smartappointment.service.interfaces.IReportService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/reports")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Reports", description = "Admin reporting and analytics endpoints")
@SecurityRequirement(name = "bearerAuth")
public class ReportController {

    private final IReportService reportService;

    public ReportController(IReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/dashboard")
    @Operation(summary = "Get dashboard overview", description = "Retrieves all-time statistics for admin dashboard")
    @StandardResponses
    public ResponseEntity<ReportResponse> getDashboardOverview() {
        return ResponseEntity.ok(reportService.getDashboardOverview());
    }

    @GetMapping("/daily")
    @Operation(summary = "Get daily report", description = "Retrieves appointment statistics for today")
    @StandardResponses
    public ResponseEntity<ReportResponse> getDailyReport() {
        return ResponseEntity.ok(reportService.getDailyReport());
    }

    @GetMapping("/weekly")
    @Operation(summary = "Get weekly report", description = "Retrieves appointment statistics for the last 7 days")
    @StandardResponses
    public ResponseEntity<ReportResponse> getWeeklyReport() {
        return ResponseEntity.ok(reportService.getWeeklyReport());
    }

    @GetMapping("/monthly")
    @Operation(summary = "Get monthly report", description = "Retrieves appointment statistics for the current month")
    @StandardResponses
    public ResponseEntity<ReportResponse> getMonthlyReport() {
        return ResponseEntity.ok(reportService.getMonthlyReport());
    }

    @PostMapping("/custom")
    @Operation(summary = "Get custom date range report", description = "Retrieves appointment statistics for a specified date range")
    @StandardResponses
    public ResponseEntity<ReportResponse> getCustomReport(@Valid @RequestBody ReportRequest request) {
        return ResponseEntity.ok(reportService.getCustomReport(request.startDate(), request.endDate()));
    }

    @PostMapping("/provider/{providerId}")
    @Operation(summary = "Get provider-specific report", description = "Retrieves appointment statistics for a specific service provider within a date range")
    @GetResponses
    public ResponseEntity<ProviderReportResponse> getProviderReport(
            @PathVariable Long providerId,
            @Valid @RequestBody ReportRequest request) {
        return ResponseEntity.ok(
                reportService.getProviderReport(providerId, request.startDate(), request.endDate()));
    }
}
