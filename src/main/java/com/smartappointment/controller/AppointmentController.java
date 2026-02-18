package com.smartappointment.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.smartappointment.config.swagger.ApiResponseAnnotations.*;
import com.smartappointment.dto.appointment.requests.CreateAppointmentRequest;
import com.smartappointment.dto.appointment.requests.UpdateAppointmentRequest;
import com.smartappointment.dto.appointment.responses.AppointmentResponse;
import com.smartappointment.dto.user.responses.UserResponse;
import com.smartappointment.exception.ForbiddenException;
import com.smartappointment.model.enums.AppointmentStatus;
import com.smartappointment.service.interfaces.IAppointmentService;
import com.smartappointment.service.interfaces.IUserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/appointments")
@Tag(name = "Appointments", description = "Appointment management endpoints")
@SecurityRequirement(name = "bearerAuth")
public class AppointmentController {

    private final IAppointmentService appointmentService;
    private final IUserService userService;

    public AppointmentController(IAppointmentService appointmentService, IUserService userService) {
        this.appointmentService = appointmentService;
        this.userService = userService;
    }

    @PostMapping
    @Operation(summary = "Create a new appointment", description = "Creates a new appointment for the authenticated user")
    @CreatedResponses
    public ResponseEntity<AppointmentResponse> createAppointment(
            Authentication authentication,
            @Valid @RequestBody CreateAppointmentRequest request) {
        AppointmentResponse response = appointmentService.createAppointment(authentication.getName(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Get current user's appointments")
    @StandardResponses
    public ResponseEntity<List<AppointmentResponse>> getMyAppointments(Authentication authentication) {
        return ResponseEntity.ok(appointmentService.getUserAppointments(authentication.getName()));
    }

    @GetMapping("/me/status")
    @Operation(summary = "Get current user's appointments by status")
    @StandardResponses
    public ResponseEntity<List<AppointmentResponse>> getMyAppointmentsByStatus(
            Authentication authentication,
            @RequestParam AppointmentStatus status) {
        return ResponseEntity.ok(appointmentService.getUserAppointmentsByStatus(authentication.getName(), status));
    }

    @GetMapping("/me/upcoming")
    @Operation(summary = "Get current user's upcoming appointments")
    @StandardResponses
    public ResponseEntity<List<AppointmentResponse>> getMyUpcomingAppointments(Authentication authentication) {
        return ResponseEntity.ok(appointmentService.getUpcomingUserAppointments(authentication.getName()));
    }

    @GetMapping("/me/past")
    @Operation(summary = "Get current user's past appointments")
    @StandardResponses
    public ResponseEntity<List<AppointmentResponse>> getMyPastAppointments(Authentication authentication) {
        return ResponseEntity.ok(appointmentService.getPastUserAppointments(authentication.getName()));
    }

    @GetMapping("/me/count")
    @Operation(summary = "Count current user's appointments")
    @StandardResponses
    public ResponseEntity<Long> countMyAppointments(Authentication authentication) {
        return ResponseEntity.ok(appointmentService.countUserAppointments(authentication.getName()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get appointment by ID")
    @GetResponses
    public ResponseEntity<AppointmentResponse> getAppointmentById(
            @PathVariable Long id,
            Authentication authentication) {
        validateOwnershipOrAdmin(id, authentication);
        return ResponseEntity.ok(appointmentService.getAppointmentById(id));
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all appointments (Admin only)")
    @StandardResponses
    public ResponseEntity<List<AppointmentResponse>> getAllAppointments() {
        return ResponseEntity.ok(appointmentService.getAllAppointments());
    }

    @GetMapping("/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get appointments by status (Admin only)")
    @StandardResponses
    public ResponseEntity<List<AppointmentResponse>> getAppointmentsByStatus(@RequestParam AppointmentStatus status) {
        return ResponseEntity.ok(appointmentService.getAppointmentsByStatus(status));
    }

    @GetMapping("/date-range")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get appointments by date range (Admin only)")
    @StandardResponses
    public ResponseEntity<List<AppointmentResponse>> getAppointmentsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ResponseEntity.ok(appointmentService.getAppointmentsByDateRange(start, end));
    }

    @GetMapping("/provider/{providerId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get provider appointments (Admin only)")
    @StandardResponses
    public ResponseEntity<List<AppointmentResponse>> getProviderAppointments(@PathVariable Long providerId) {
        return ResponseEntity.ok(appointmentService.getProviderAppointments(providerId));
    }

    @GetMapping("/provider/{providerId}/upcoming")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get provider upcoming appointments (Admin only)")
    @StandardResponses
    public ResponseEntity<List<AppointmentResponse>> getUpcomingProviderAppointments(@PathVariable Long providerId) {
        return ResponseEntity.ok(appointmentService.getUpcomingProviderAppointments(providerId));
    }

    @GetMapping("/provider/{providerId}/date-range")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get provider appointments by date range (Admin only)")
    @StandardResponses
    public ResponseEntity<List<AppointmentResponse>> getProviderAppointmentsByDateRange(
            @PathVariable Long providerId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ResponseEntity.ok(appointmentService.getProviderAppointmentsByDateRange(providerId, start, end));
    }

    @GetMapping("/location/{locationId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get location appointments (Admin only)")
    @StandardResponses
    public ResponseEntity<List<AppointmentResponse>> getLocationAppointments(@PathVariable Long locationId) {
        return ResponseEntity.ok(appointmentService.getLocationAppointments(locationId));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update appointment")
    @UpdateResponses
    public ResponseEntity<AppointmentResponse> updateAppointment(
            @PathVariable Long id,
            Authentication authentication,
            @Valid @RequestBody UpdateAppointmentRequest request) {
        validateOwnershipOrAdmin(id, authentication);
        return ResponseEntity.ok(appointmentService.updateAppointment(id, request));
    }

    @PatchMapping("/{id}/cancel")
    @Operation(summary = "Cancel appointment")
    @UpdateResponses
    public ResponseEntity<AppointmentResponse> cancelAppointment(
            @PathVariable Long id,
            Authentication authentication,
            @RequestParam(required = false) String reason) {
        validateOwnershipOrAdmin(id, authentication);
        return ResponseEntity.ok(appointmentService.cancelAppointment(id, reason));
    }

    @PatchMapping("/{id}/confirm")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Confirm appointment (Admin only)")
    @UpdateResponses
    public ResponseEntity<AppointmentResponse> confirmAppointment(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentService.confirmAppointment(id));
    }

    @PatchMapping("/{id}/complete")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Mark appointment as completed (Admin only)")
    @UpdateResponses
    public ResponseEntity<AppointmentResponse> completeAppointment(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentService.completeAppointment(id));
    }

    @PatchMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Reject appointment (Admin only)")
    @UpdateResponses
    public ResponseEntity<AppointmentResponse> rejectAppointment(
            @PathVariable Long id,
            @RequestParam(required = false) String reason) {
        return ResponseEntity.ok(appointmentService.rejectAppointment(id, reason));
    }

    @PostMapping("/mark-missed")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Mark missed appointments (Admin only)")
    @StandardResponses
    public ResponseEntity<Void> markMissedAppointments() {
        appointmentService.markMissedAppointments();
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete appointment permanently (Admin only)")
    @DeleteResponses
    public ResponseEntity<Void> deleteAppointment(@PathVariable Long id) {
        appointmentService.deleteAppointment(id);
        return ResponseEntity.noContent().build();
    }

    private void validateOwnershipOrAdmin(Long appointmentId, Authentication authentication) {
        if (isAdmin(authentication)) {
            return;
        }

        UserResponse currentUser = userService.getUserByEmail(authentication.getName());
        AppointmentResponse appointment = appointmentService.getAppointmentById(appointmentId);

        if (appointment == null || appointment.userId() == null || currentUser == null || currentUser.id() == null
                || !Objects.equals(appointment.userId(), currentUser.id())) {
            throw new ForbiddenException("You are not allowed to access this appointment");
        }
    }

    private boolean isAdmin(Authentication authentication) {
        if (authentication == null || authentication.getAuthorities() == null) {
            return false;
        }

        return authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));
    }
}
