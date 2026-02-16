package com.smartappointment.service.interfaces;

import com.smartappointment.dto.appointment.AppointmentRequest;
import com.smartappointment.dto.appointment.AppointmentResponse;
import com.smartappointment.model.enums.AppointmentStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface IAppointmentService {

    // CRUD
    AppointmentResponse createAppointment(String userEmail, AppointmentRequest request);

    AppointmentResponse getAppointmentById(Long id);

    AppointmentResponse updateAppointment(Long id, AppointmentRequest request);

    void deleteAppointment(Long id);

    // Status transitions
    AppointmentResponse confirmAppointment(Long id);

    AppointmentResponse completeAppointment(Long id);

    AppointmentResponse cancelAppointment(Long id, String reason);

    AppointmentResponse rejectAppointment(Long id, String reason);

    void markMissedAppointments();

    // All appointments (admin)
    List<AppointmentResponse> getAllAppointments();

    List<AppointmentResponse> getAppointmentsByStatus(AppointmentStatus status);

    List<AppointmentResponse> getAppointmentsByDateRange(LocalDateTime start, LocalDateTime end);

    // User-based queries
    List<AppointmentResponse> getUserAppointments(String userEmail);

    List<AppointmentResponse> getUserAppointmentsByStatus(String userEmail, AppointmentStatus status);

    List<AppointmentResponse> getUpcomingUserAppointments(String userEmail);

    List<AppointmentResponse> getPastUserAppointments(String userEmail);

    long countUserAppointments(String userEmail);

    // Provider-based queries
    List<AppointmentResponse> getProviderAppointments(Long providerId);

    List<AppointmentResponse> getUpcomingProviderAppointments(Long providerId);

    List<AppointmentResponse> getProviderAppointmentsByDateRange(Long providerId, LocalDateTime start,
            LocalDateTime end);

    // Location-based queries
    List<AppointmentResponse> getLocationAppointments(Long locationId);

    // Conflict detection
    boolean hasProviderConflict(Long providerId, LocalDateTime startTime, LocalDateTime endTime);

    boolean hasUserConflict(String userEmail, LocalDateTime startTime, LocalDateTime endTime);
}
