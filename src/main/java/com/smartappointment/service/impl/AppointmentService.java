package com.smartappointment.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smartappointment.dto.appointment.requests.CreateAppointmentRequest;
import com.smartappointment.dto.appointment.requests.UpdateAppointmentRequest;
import com.smartappointment.dto.appointment.responses.AppointmentResponse;
import com.smartappointment.exception.BadRequestException;
import com.smartappointment.exception.ConflictException;
import com.smartappointment.exception.ResourceNotFoundException;
import com.smartappointment.mapper.AppointmentMapper;
import com.smartappointment.model.entity.Appointment;
import com.smartappointment.model.entity.Location;
import com.smartappointment.model.entity.Reminder;
import com.smartappointment.model.entity.ServiceProvider;
import com.smartappointment.model.entity.User;
import com.smartappointment.model.enums.AppointmentStatus;
import com.smartappointment.repository.AppointmentRepository;
import com.smartappointment.repository.LocationRepository;
import com.smartappointment.repository.ServiceProviderRepository;
import com.smartappointment.repository.UserRepository;
import com.smartappointment.service.interfaces.IAppointmentService;

import lombok.extern.slf4j.Slf4j;

/**
 * Appointment management service.
 */
@Slf4j
@Service
public class AppointmentService implements IAppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final ServiceProviderRepository providerRepository;
    private final LocationRepository locationRepository;
    private final AppointmentMapper appointmentMapper;

    public AppointmentService(AppointmentRepository appointmentRepository, UserRepository userRepository,
            ServiceProviderRepository providerRepository, LocationRepository locationRepository,
            AppointmentMapper appointmentMapper) {
        this.appointmentRepository = appointmentRepository;
        this.userRepository = userRepository;
        this.providerRepository = providerRepository;
        this.locationRepository = locationRepository;
        this.appointmentMapper = appointmentMapper;
    }

    @Override
    @Transactional
    @CacheEvict(value = "appointments", allEntries = true)
    public AppointmentResponse createAppointment(String userEmail, CreateAppointmentRequest request) {
        User user = findUserByEmailOrThrow(userEmail);
        ServiceProvider provider = findProviderOrThrow(request.serviceProviderId());
        Location location = findLocationOrThrow(request.locationId());

        LocalDateTime startTime = request.startTime();
        LocalDateTime endTime = resolveEndTime(startTime, request.endTime(), provider);
        validateTimeRange(startTime, endTime);

        if (hasProviderConflict(provider.getId(), startTime, endTime)) {
            throw new ConflictException("Provider already has an appointment in this time range");
        }

        if (hasUserConflict(userEmail, startTime, endTime)) {
            throw new ConflictException("User already has an appointment in this time range");
        }

        Appointment appointment = appointmentMapper.toEntity(request, user, provider, location, endTime);
        Reminder reminder = appointmentMapper.toDefaultReminder(appointment, provider);
        if (reminder != null) {
            appointment.getReminders().add(reminder);
        }

        Appointment saved = appointmentRepository.save(appointment);
        log.info("Appointment created: id={}, user={}, provider={}", saved.getId(), user.getEmail(),
                provider.getName());

        return appointmentMapper.toResponse(saved);
    }

    @Override
    public List<AppointmentResponse> getUserAppointments(String userEmail) {
        User user = findUserByEmailOrThrow(userEmail);
        return appointmentMapper.toResponseList(appointmentRepository.findByUserIdOrderByStartTimeDesc(user.getId()));
    }

    @Override
    public AppointmentResponse getAppointmentById(Long id) {
        Appointment appointment = findAppointmentOrThrow(id);
        return appointmentMapper.toResponse(appointment);
    }

    @Override
    public List<AppointmentResponse> getAllAppointments() {
        return appointmentMapper.toResponseList(appointmentRepository.findAll());
    }

    @Override
    @Transactional
    @CacheEvict(value = "appointments", allEntries = true)
    public AppointmentResponse updateAppointment(Long id, UpdateAppointmentRequest request) {
        Appointment appointment = findAppointmentOrThrow(id);

        validateUpdatableStatus(appointment.getStatus());

        ServiceProvider provider = request.serviceProviderId() != null
                ? findProviderOrThrow(request.serviceProviderId())
                : appointment.getServiceProvider();

        Location location = request.locationId() != null
                ? findLocationOrThrow(request.locationId())
                : appointment.getLocation();

        LocalDateTime updatedStart = request.startTime() != null ? request.startTime() : appointment.getStartTime();

        LocalDateTime updatedEnd = request.endTime() != null
                ? request.endTime()
                : appointment.getEndTime();

        if (request.startTime() != null && request.endTime() == null &&
                (updatedEnd == null || !updatedEnd.isAfter(updatedStart))) {
            updatedEnd = updatedStart.plusMinutes(provider.getCategory().getDefaultDurationMinutes());
        }

        validateTimeRange(updatedStart, updatedEnd);

        if (hasProviderConflictExcludingAppointment(provider.getId(), updatedStart, updatedEnd, appointment.getId())) {
            throw new ConflictException("Provider already has an appointment in this time range");
        }

        if (hasUserConflictExcludingAppointment(appointment.getUser().getId(), updatedStart, updatedEnd,
                appointment.getId())) {
            throw new ConflictException("User already has an appointment in this time range");
        }

        appointmentMapper.updateEntityFromRequest(appointment, request, provider, location, updatedEnd);

        Appointment updated = appointmentRepository.save(appointment);
        return appointmentMapper.toResponse(updated);
    }

    @Override
    @Transactional
    @CacheEvict(value = "appointments", allEntries = true)
    public AppointmentResponse cancelAppointment(Long id, String reason) {
        Appointment appointment = findAppointmentOrThrow(id);

        if (appointment.getStatus() == AppointmentStatus.COMPLETED
                || appointment.getStatus() == AppointmentStatus.CANCELLED
                || appointment.getStatus() == AppointmentStatus.REJECTED) {
            throw new BadRequestException("Cannot cancel appointment in " + appointment.getStatus() + " status");
        }

        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointment.setCancellationReason(reason);
        appointment.setCancelledAt(LocalDateTime.now());
        appointment.setCancelledBy("USER");

        Appointment updated = appointmentRepository.save(appointment);
        log.info("Appointment cancelled: id={}, reason={}", id, reason);

        return appointmentMapper.toResponse(updated);
    }

    @Override
    @Transactional
    @CacheEvict(value = "appointments", allEntries = true)
    public AppointmentResponse completeAppointment(Long id) {
        Appointment appointment = findAppointmentOrThrow(id);

        if (appointment.getStatus() != AppointmentStatus.SCHEDULED &&
                appointment.getStatus() != AppointmentStatus.CONFIRMED) {
            throw new BadRequestException("Can only complete SCHEDULED or CONFIRMED appointments");
        }

        appointment.setStatus(AppointmentStatus.COMPLETED);
        Appointment updated = appointmentRepository.save(appointment);

        return appointmentMapper.toResponse(updated);
    }

    @Override
    @Transactional
    @CacheEvict(value = "appointments", allEntries = true)
    public void deleteAppointment(Long id) {
        Appointment appointment = findAppointmentOrThrow(id);
        appointmentRepository.delete(appointment);
        log.info("Appointment deleted: id={}", id);
    }

    @Override
    @Transactional
    @CacheEvict(value = "appointments", allEntries = true)
    public AppointmentResponse confirmAppointment(Long id) {
        Appointment appointment = findAppointmentOrThrow(id);

        if (appointment.getStatus() != AppointmentStatus.SCHEDULED) {
            throw new BadRequestException("Only SCHEDULED appointments can be confirmed");
        }

        appointment.setStatus(AppointmentStatus.CONFIRMED);
        appointment.setConfirmedAt(LocalDateTime.now());

        Appointment updated = appointmentRepository.save(appointment);
        return appointmentMapper.toResponse(updated);
    }

    @Override
    @Transactional
    @CacheEvict(value = "appointments", allEntries = true)
    public AppointmentResponse rejectAppointment(Long id, String reason) {
        Appointment appointment = findAppointmentOrThrow(id);

        if (appointment.getStatus() != AppointmentStatus.SCHEDULED &&
                appointment.getStatus() != AppointmentStatus.CONFIRMED) {
            throw new BadRequestException("Only SCHEDULED or CONFIRMED appointments can be rejected");
        }

        appointment.setStatus(AppointmentStatus.REJECTED);
        appointment.setCancellationReason(reason);
        appointment.setCancelledAt(LocalDateTime.now());
        appointment.setCancelledBy("ADMIN");

        Appointment updated = appointmentRepository.save(appointment);
        return appointmentMapper.toResponse(updated);
    }

    @Override
    @Transactional
    @CacheEvict(value = "appointments", allEntries = true)
    public void markMissedAppointments() {
        List<Appointment> missedAppointments = appointmentRepository.findMissedAppointments(LocalDateTime.now());
        if (missedAppointments.isEmpty()) {
            return;
        }

        missedAppointments.forEach(appointment -> appointment.setStatus(AppointmentStatus.MISSED));
        appointmentRepository.saveAll(missedAppointments);
        log.info("Marked {} appointments as MISSED", missedAppointments.size());
    }

    @Override
    public List<AppointmentResponse> getAppointmentsByStatus(AppointmentStatus status) {
        return appointmentMapper.toResponseList(appointmentRepository.findByStatus(status));
    }

    @Override
    public List<AppointmentResponse> getAppointmentsByDateRange(LocalDateTime start, LocalDateTime end) {
        validateTimeRange(start, end);
        return appointmentMapper.toResponseList(appointmentRepository.findByDateRange(start, end));
    }

    @Override
    public List<AppointmentResponse> getUserAppointmentsByStatus(String userEmail, AppointmentStatus status) {
        User user = findUserByEmailOrThrow(userEmail);
        return appointmentMapper.toResponseList(appointmentRepository.findByUserIdAndStatus(user.getId(), status));
    }

    @Override
    public List<AppointmentResponse> getUpcomingUserAppointments(String userEmail) {
        User user = findUserByEmailOrThrow(userEmail);
        return appointmentMapper.toResponseList(appointmentRepository.findUpcomingByUserId(user.getId(), LocalDateTime.now()));
    }

    @Override
    public List<AppointmentResponse> getPastUserAppointments(String userEmail) {
        User user = findUserByEmailOrThrow(userEmail);
        return appointmentMapper.toResponseList(appointmentRepository.findPastByUserId(user.getId(), LocalDateTime.now()));
    }

    @Override
    public long countUserAppointments(String userEmail) {
        User user = findUserByEmailOrThrow(userEmail);
        return appointmentRepository.countByUserId(user.getId());
    }

    @Override
    public List<AppointmentResponse> getProviderAppointments(Long providerId) {
        findProviderOrThrow(providerId);
        return appointmentMapper.toResponseList(appointmentRepository.findByServiceProviderIdOrderByStartTimeDesc(providerId));
    }

    @Override
    public List<AppointmentResponse> getUpcomingProviderAppointments(Long providerId) {
        findProviderOrThrow(providerId);
        return appointmentMapper.toResponseList(appointmentRepository.findUpcomingByProviderId(providerId, LocalDateTime.now()));
    }

    @Override
    public List<AppointmentResponse> getProviderAppointmentsByDateRange(Long providerId, LocalDateTime start,
            LocalDateTime end) {
        findProviderOrThrow(providerId);
        validateTimeRange(start, end);
        return appointmentMapper.toResponseList(appointmentRepository.findByProviderIdAndDateRange(providerId, start, end));
    }

    @Override
    public List<AppointmentResponse> getLocationAppointments(Long locationId) {
        findLocationOrThrow(locationId);
        return appointmentMapper.toResponseList(appointmentRepository.findByLocationId(locationId));
    }

    @Override
    public boolean hasProviderConflict(Long providerId, LocalDateTime startTime, LocalDateTime endTime) {
        validateTimeRange(startTime, endTime);
        return !appointmentRepository.findConflictingAppointments(providerId, startTime, endTime).isEmpty();
    }

    @Override
    public boolean hasUserConflict(String userEmail, LocalDateTime startTime, LocalDateTime endTime) {
        validateTimeRange(startTime, endTime);
        User user = findUserByEmailOrThrow(userEmail);
        return !appointmentRepository.findUserConflictingAppointments(user.getId(), startTime, endTime).isEmpty();
    }

    private Appointment findAppointmentOrThrow(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + id));
    }

    private User findUserByEmailOrThrow(String userEmail) {
        return userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private ServiceProvider findProviderOrThrow(Long providerId) {
        return providerRepository.findById(providerId)
                .orElseThrow(() -> new ResourceNotFoundException("Service provider not found with id: " + providerId));
    }

    private Location findLocationOrThrow(Long locationId) {
        return locationRepository.findById(locationId)
                .orElseThrow(() -> new ResourceNotFoundException("Location not found with id: " + locationId));
    }

    private LocalDateTime resolveEndTime(LocalDateTime startTime, LocalDateTime requestedEndTime, ServiceProvider provider) {
        if (requestedEndTime != null) {
            return requestedEndTime;
        }

        int durationMinutes = provider.getCategory().getDefaultDurationMinutes();
        return startTime.plusMinutes(durationMinutes);
    }

    private void validateTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime == null || endTime == null) {
            throw new BadRequestException("Start and end time are required");
        }

        if (!endTime.isAfter(startTime)) {
            throw new BadRequestException("End time must be after start time");
        }
    }

    private void validateUpdatableStatus(AppointmentStatus status) {
        if (status == AppointmentStatus.COMPLETED ||
                status == AppointmentStatus.CANCELLED ||
                status == AppointmentStatus.REJECTED ||
                status == AppointmentStatus.MISSED) {
            throw new BadRequestException("Cannot update appointment in " + status + " status");
        }
    }

    private boolean hasProviderConflictExcludingAppointment(Long providerId, LocalDateTime startTime,
            LocalDateTime endTime, Long appointmentId) {
        return appointmentRepository.findConflictingAppointments(providerId, startTime, endTime).stream()
                .anyMatch(existing -> !existing.getId().equals(appointmentId));
    }

    private boolean hasUserConflictExcludingAppointment(Long userId, LocalDateTime startTime,
            LocalDateTime endTime, Long appointmentId) {
        return appointmentRepository.findUserConflictingAppointments(userId, startTime, endTime).stream()
                .anyMatch(existing -> !existing.getId().equals(appointmentId));
    }
}
