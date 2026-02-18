package com.smartappointment.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.smartappointment.dto.reminder.responses.ReminderResponse;
import com.smartappointment.model.entity.Reminder;

@Component
public class ReminderMapper {

    public ReminderResponse toResponse(Reminder reminder) {
        if (reminder == null) {
            return null;
        }

        return new ReminderResponse(
                reminder.getId(),
                reminder.getAppointment() != null ? reminder.getAppointment().getId() : null,
                reminder.getType() != null ? reminder.getType().name() : null,
                reminder.getStatus() != null ? reminder.getStatus().name() : null,
                reminder.getScheduledAt(),
                reminder.getSentAt(),
                reminder.getMessage());
    }

    public List<ReminderResponse> toResponseList(List<Reminder> reminders) {
        if (reminders == null) {
            return List.of();
        }

        return reminders.stream().map(this::toResponse).collect(Collectors.toList());
    }
}
