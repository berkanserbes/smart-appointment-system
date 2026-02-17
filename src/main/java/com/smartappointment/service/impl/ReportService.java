package com.smartappointment.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.springframework.stereotype.Service;

import com.smartappointment.dto.report.responses.ProviderReportResponse;
import com.smartappointment.dto.report.responses.ReportResponse;
import com.smartappointment.exception.BadRequestException;
import com.smartappointment.exception.ResourceNotFoundException;
import com.smartappointment.mapper.ReportMapper;
import com.smartappointment.model.enums.AppointmentStatus;
import com.smartappointment.repository.AppointmentRepository;
import com.smartappointment.repository.ServiceProviderRepository;
import com.smartappointment.repository.UserRepository;
import com.smartappointment.service.interfaces.IReportService;

@Service
public class ReportService implements IReportService {

    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final ServiceProviderRepository providerRepository;
    private final ReportMapper reportMapper;

    public ReportService(AppointmentRepository appointmentRepository, UserRepository userRepository,
            ServiceProviderRepository providerRepository, ReportMapper reportMapper) {
        this.appointmentRepository = appointmentRepository;
        this.userRepository = userRepository;
        this.providerRepository = providerRepository;
        this.reportMapper = reportMapper;
    }

    @Override
    public ReportResponse getDashboardOverview() {
        long totalAppointments = appointmentRepository.count();

        return reportMapper.toResponse(
                totalAppointments,
                appointmentRepository.countByStatus(AppointmentStatus.SCHEDULED),
                appointmentRepository.countByStatus(AppointmentStatus.CONFIRMED),
                appointmentRepository.countByStatus(AppointmentStatus.COMPLETED),
                appointmentRepository.countByStatus(AppointmentStatus.CANCELLED),
                appointmentRepository.countByStatus(AppointmentStatus.MISSED),
                userRepository.count(),
                providerRepository.count(),
                "ALL_TIME",
                null,
                null);
    }

    @Override
    public ReportResponse getDailyReport() {
        LocalDateTime start = LocalDate.now().atStartOfDay();
        LocalDateTime end = LocalDate.now().atTime(LocalTime.MAX);
        return buildReport(start, end, "DAILY");
    }

    @Override
    public ReportResponse getWeeklyReport() {
        LocalDateTime start = LocalDate.now().minusDays(7).atStartOfDay();
        LocalDateTime end = LocalDate.now().atTime(LocalTime.MAX);
        return buildReport(start, end, "WEEKLY");
    }

    @Override
    public ReportResponse getMonthlyReport() {
        LocalDateTime start = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime end = LocalDate.now().atTime(LocalTime.MAX);
        return buildReport(start, end, "MONTHLY");
    }

    @Override
    public ReportResponse getCustomReport(LocalDateTime start, LocalDateTime end) {
        validateDateRange(start, end);
        return buildReport(start, end, "CUSTOM");
    }

    @Override
    public ProviderReportResponse getProviderReport(Long providerId, LocalDateTime start, LocalDateTime end) {
        validateDateRange(start, end);

        if (!providerRepository.existsById(providerId)) {
            throw new ResourceNotFoundException("Service provider not found with id: " + providerId);
        }

        long total = appointmentRepository.countByServiceProviderIdAndStartTimeBetween(providerId, start, end);

        return reportMapper.toProviderResponse(
            providerId,
                total,
            appointmentRepository.countByProviderIdAndDateRangeAndStatus(providerId, start, end,
                AppointmentStatus.SCHEDULED),
            appointmentRepository.countByProviderIdAndDateRangeAndStatus(providerId, start, end,
                AppointmentStatus.CONFIRMED),
            appointmentRepository.countByProviderIdAndDateRangeAndStatus(providerId, start, end,
                AppointmentStatus.COMPLETED),
            appointmentRepository.countByProviderIdAndDateRangeAndStatus(providerId, start, end,
                AppointmentStatus.CANCELLED),
            appointmentRepository.countByProviderIdAndDateRangeAndStatus(providerId, start, end,
                AppointmentStatus.MISSED),
                start,
                end);
    }

    private ReportResponse buildReport(LocalDateTime start, LocalDateTime end, String period) {
        return reportMapper.toResponse(
            appointmentRepository.countByStartTimeBetween(start, end),
                appointmentRepository.countByDateRangeAndStatus(start, end, AppointmentStatus.SCHEDULED),
                appointmentRepository.countByDateRangeAndStatus(start, end, AppointmentStatus.CONFIRMED),
                appointmentRepository.countByDateRangeAndStatus(start, end, AppointmentStatus.COMPLETED),
                appointmentRepository.countByDateRangeAndStatus(start, end, AppointmentStatus.CANCELLED),
                appointmentRepository.countByDateRangeAndStatus(start, end, AppointmentStatus.MISSED),
                userRepository.count(),
                providerRepository.count(),
                period,
                start,
                end);
    }

    private void validateDateRange(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            throw new BadRequestException("Start and end dates are required");
        }

        if (start.isAfter(end)) {
            throw new BadRequestException("Start date must be before or equal to end date");
        }
    }
}
