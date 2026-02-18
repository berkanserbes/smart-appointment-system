package com.smartappointment.service.interfaces;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.smartappointment.dto.appointment.requests.CreateAppointmentRequest;
import com.smartappointment.dto.appointment.requests.UpdateAppointmentRequest;
import com.smartappointment.dto.appointment.responses.AppointmentResponse;
import com.smartappointment.model.enums.AppointmentStatus;

public interface IAppointmentService {

    // CRUD
    AppointmentResponse createAppointment(String userEmail, CreateAppointmentRequest request);

    AppointmentResponse getAppointmentById(Long id);

    AppointmentResponse updateAppointment(Long id, UpdateAppointmentRequest request);

    void deleteAppointment(Long id);

    // Status transitions
    AppointmentResponse confirmAppointment(Long id);

    AppointmentResponse completeAppointment(Long id);

    AppointmentResponse cancelAppointment(Long id, String reason);

    AppointmentResponse rejectAppointment(Long id, String reason);

    void markMissedAppointments();

    // All appointments (admin)
    Page<AppointmentResponse> getAllAppointments(Pageable pageable);

    Page<AppointmentResponse> getAppointmentsByStatus(AppointmentStatus status, Pageable pageable);

    Page<AppointmentResponse> getAppointmentsByDateRange(LocalDateTime start, LocalDateTime end, Pageable pageable);

    // User-based queries
    Page<AppointmentResponse> getUserAppointments(String userEmail, Pageable pageable);

    Page<AppointmentResponse> getUserAppointmentsByStatus(String userEmail, AppointmentStatus status, Pageable pageable);

    Page<AppointmentResponse> getUpcomingUserAppointments(String userEmail, Pageable pageable);

    Page<AppointmentResponse> getPastUserAppointments(String userEmail, Pageable pageable);

    long countUserAppointments(String userEmail);

    // Provider-based queries
    Page<AppointmentResponse> getProviderAppointments(Long providerId, Pageable pageable);

    Page<AppointmentResponse> getUpcomingProviderAppointments(Long providerId, Pageable pageable);

    Page<AppointmentResponse> getProviderAppointmentsByDateRange(Long providerId, LocalDateTime start,
            LocalDateTime end, Pageable pageable);

    // Location-based queries
    Page<AppointmentResponse> getLocationAppointments(Long locationId, Pageable pageable);

    // Conflict detection
    boolean hasProviderConflict(Long providerId, LocalDateTime startTime, LocalDateTime endTime);

    boolean hasUserConflict(String userEmail, LocalDateTime startTime, LocalDateTime endTime);
}
