package com.smartappointment.mapper;

import org.springframework.stereotype.Component;

import com.smartappointment.dto.appointment.responses.AppointmentResponse;
import com.smartappointment.model.entity.Appointment;

@Component
public class AppointmentMapper {

    public AppointmentResponse toResponse(Appointment appointment) {
        if (appointment == null) {
            return null;
        }

        return new AppointmentResponse(
                appointment.getId(),
                appointment.getStartTime(),
                appointment.getEndTime(),
                appointment.getStatus().name(),
                appointment.getCancellationReason(),
                appointment.getNotes(),
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
