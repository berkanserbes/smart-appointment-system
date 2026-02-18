package com.smartappointment.mapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.smartappointment.dto.appointment.requests.CreateAppointmentRequest;
import com.smartappointment.dto.appointment.requests.UpdateAppointmentRequest;
import com.smartappointment.dto.appointment.responses.AppointmentResponse;
import com.smartappointment.model.entity.Appointment;
import com.smartappointment.model.entity.Location;
import com.smartappointment.model.entity.Reminder;
import com.smartappointment.model.entity.ServiceProvider;
import com.smartappointment.model.entity.User;
import com.smartappointment.model.enums.AppointmentStatus;
import com.smartappointment.model.enums.ReminderStatus;
import com.smartappointment.model.enums.ReminderType;

@Component
public class AppointmentMapper {

    public Appointment toEntity(CreateAppointmentRequest request, User user, ServiceProvider provider,
            Location location, LocalDateTime endTime) {
        if (request == null || user == null || provider == null || location == null || endTime == null) {
            return null;
        }

        return Appointment.builder()
                .user(user)
                .serviceProvider(provider)
                .location(location)
                .startTime(request.startTime())
                .endTime(endTime)
                .status(AppointmentStatus.SCHEDULED)
                .notes(request.notes())
                .build();
    }

    public void updateEntityFromRequest(Appointment appointment, UpdateAppointmentRequest request,
            ServiceProvider provider, Location location, LocalDateTime endTime) {
        if (appointment == null || request == null || provider == null || location == null || endTime == null) {
            return;
        }

        appointment.setServiceProvider(provider);
        appointment.setLocation(location);

        if (request.startTime() != null) {
            appointment.setStartTime(request.startTime());
        }

        appointment.setEndTime(endTime);

        if (request.notes() != null) {
            appointment.setNotes(request.notes());
        }
    }

    public Reminder toDefaultReminder(Appointment appointment, ServiceProvider provider) {
        if (appointment == null || provider == null || appointment.getStartTime() == null) {
            return null;
        }

        return Reminder.builder()
                .appointment(appointment)
                .type(ReminderType.EMAIL)
                .status(ReminderStatus.PENDING)
                .scheduledAt(appointment.getStartTime().minusHours(1))
                .message(String.format("Reminder: You have an appointment with %s at %s",
                        provider.getName(), appointment.getStartTime()))
                .build();
    }

    public List<AppointmentResponse> toResponseList(List<Appointment> appointments) {
        if (appointments == null) {
            return List.of();
        }

        return appointments.stream().map(this::toResponse).collect(Collectors.toList());
    }

    public AppointmentResponse toResponse(Appointment appointment) {
        if (appointment == null) {
            return null;
        }

        return new AppointmentResponse(
                appointment.getId(),
                appointment.getStartTime(),
                appointment.getEndTime(),
                appointment.getStatus().name(),
                appointment.getNotes(),
                appointment.getCancellationReason(),
                appointment.getCancelledAt(),
                appointment.getCancelledBy(),
                appointment.getConfirmedAt(),
                appointment.getUser().getId(),
                appointment.getUser().getFullName(),
                appointment.getServiceProvider().getId(),
                appointment.getServiceProvider().getName(),
                appointment.getServiceProvider().getTitle(),
                appointment.getLocation().getId(),
                appointment.getLocation().getName(),
                appointment.getLocation().getAddress(),
                appointment.getCreatedAt(),
                appointment.getUpdatedAt());
    }
}
