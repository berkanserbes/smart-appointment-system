package com.smartappointment.service.interfaces;

import com.smartappointment.dto.reminder.ReminderResponse;
import com.smartappointment.model.enums.ReminderStatus;
import com.smartappointment.model.enums.ReminderType;

import java.util.List;

public interface IReminderService {

    void processReminders();

    List<ReminderResponse> getRemindersByAppointmentId(Long appointmentId);

    List<ReminderResponse> getRemindersByStatus(ReminderStatus status);

    List<ReminderResponse> getRemindersByType(ReminderType type);

    ReminderResponse getReminderById(Long id);

    boolean existsByAppointmentIdAndType(Long appointmentId, ReminderType type);

    long countByStatus(ReminderStatus status);

    void deleteRemindersByAppointmentId(Long appointmentId);

    void deleteReminder(Long id);
}
