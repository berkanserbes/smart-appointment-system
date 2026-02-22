package com.smartappointment.service.impl;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smartappointment.dto.reminder.requests.CreateReminderRequest;
import com.smartappointment.dto.reminder.responses.ReminderResponse;
import com.smartappointment.exception.BadRequestException;
import com.smartappointment.exception.ResourceNotFoundException;
import com.smartappointment.mapper.ReminderMapper;
import com.smartappointment.model.entity.Appointment;
import com.smartappointment.model.entity.Reminder;
import com.smartappointment.model.enums.AppointmentStatus;
import com.smartappointment.model.enums.ReminderStatus;
import com.smartappointment.model.enums.ReminderType;
import com.smartappointment.repository.AppointmentRepository;
import com.smartappointment.repository.ReminderRepository;
import com.smartappointment.service.interfaces.IReminderService;
import com.smartappointment.service.interfaces.ISender;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ReminderService implements IReminderService {

    private final ReminderRepository reminderRepository;
    private final AppointmentRepository appointmentRepository;
    private final ReminderMapper reminderMapper;
    private final ISender sender;

    public ReminderService(ReminderRepository reminderRepository,
            AppointmentRepository appointmentRepository,
            ReminderMapper reminderMapper,
            ISender sender) {
        this.reminderRepository = reminderRepository;
        this.appointmentRepository = appointmentRepository;
        this.reminderMapper = reminderMapper;
        this.sender = sender;
    }

    // ==================== CREATE ====================

    @Override
    @Transactional
    public ReminderResponse createReminder(CreateReminderRequest request) {
        validateCreateRequest(request);

        Appointment appointment = appointmentRepository.findById(request.appointmentId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Appointment not found with id: " + request.appointmentId()));

        validateAppointmentForReminder(appointment);

        ReminderType type = parseReminderType(request.type());

        if (reminderRepository.existsByAppointmentIdAndType(request.appointmentId(), type)) {
            throw new BadRequestException(
                    "A reminder of type " + type + " already exists for this appointment");
        }

        validateChannelData(appointment, type);

        Reminder reminder = Reminder.builder()
                .appointment(appointment)
                .type(type)
                .status(ReminderStatus.PENDING)
                .scheduledAt(request.scheduledAt())
            .message(buildReminderMessage(request.message(), appointment))
                .build();

        Reminder saved = reminderRepository.save(reminder);
        log.info("Reminder created: id={}, appointmentId={}, type={}, scheduledAt={}",
            saved.getId(), appointment.getId(), saved.getType(), saved.getScheduledAt());
        return reminderMapper.toResponse(saved);
    }

    // ==================== SCHEDULED PROCESSING ====================

    @Override
    @Scheduled(fixedRate = 60000)
    @Transactional
    public void processReminders() {
        LocalDateTime now = LocalDateTime.now();

        List<Reminder> pendingReminders = reminderRepository
                .findByStatusAndScheduledAtBefore(ReminderStatus.PENDING, now);

        if (pendingReminders.isEmpty()) {
            return;
        }

        log.info("Processing {} pending reminders", pendingReminders.size());

        int successCount = 0;
        int failedCount = 0;

        for (Reminder reminder : pendingReminders) {
            try {
                validateChannelData(reminder.getAppointment(), reminder.getType());
                sendReminder(reminder);

                reminder.setStatus(ReminderStatus.SENT);
                reminder.setSentAt(LocalDateTime.now());
                successCount++;
                log.info("Reminder sent: id={}, appointmentWith={}, type={}",
                        reminder.getId(),
                        reminder.getAppointment().getServiceProvider().getName(),
                        reminder.getType());

            } catch (Exception e) {
                reminder.setStatus(ReminderStatus.FAILED);
                failedCount++;
                log.error("Failed to send reminder id={}", reminder.getId(), e);
            }
        }

        reminderRepository.saveAll(pendingReminders);
        log.info("Reminder processing completed: total={}, sent={}, failed={}",
                pendingReminders.size(), successCount, failedCount);
    }

    private void sendReminder(Reminder reminder) {
        String appointmentInfo = reminder.getAppointment().getServiceProvider().getName() +
                " at " + reminder.getAppointment().getStartTime();
        String userEmail = reminder.getAppointment().getUser().getEmail();

        switch (reminder.getType()) {
            case EMAIL -> {
                String subject = "Appointment Reminder: " + appointmentInfo;
                sender.send(userEmail, subject, reminder.getMessage());
            }
            case SMS -> log.info("[SMS] To: {}, Message: {}",
                    reminder.getAppointment().getUser().getPhone(),
                    reminder.getMessage());
            case IN_APP -> log.info("[IN_APP] User: {}, Message: {}",
                    userEmail,
                    reminder.getMessage());
        }
    }

    // ==================== GET OPERATIONS ====================

    @Override
    @Transactional(readOnly = true)
    public List<ReminderResponse> getRemindersByAppointmentId(Long appointmentId) {
        return reminderMapper.toResponseList(reminderRepository.findByAppointmentId(appointmentId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReminderResponse> getRemindersByStatus(ReminderStatus status) {
        return reminderMapper.toResponseList(reminderRepository.findByStatus(status));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReminderResponse> getRemindersByType(ReminderType type) {
        return reminderMapper.toResponseList(reminderRepository.findByType(type));
    }

    @Override
    @Transactional(readOnly = true)
    public ReminderResponse getReminderById(Long id) {
        Reminder reminder = findReminderOrThrow(id);
        return reminderMapper.toResponse(reminder);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByAppointmentIdAndType(Long appointmentId, ReminderType type) {
        return reminderRepository.existsByAppointmentIdAndType(appointmentId, type);
    }

    @Override
    @Transactional(readOnly = true)
    public long countByStatus(ReminderStatus status) {
        return reminderRepository.countByStatus(status);
    }

    // ==================== DELETE OPERATIONS ====================

    @Override
    @Transactional
    public void deleteRemindersByAppointmentId(Long appointmentId) {
        appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + appointmentId));
        reminderRepository.deleteByAppointmentId(appointmentId);
        log.info("Deleted reminders for appointmentId={}", appointmentId);
    }

    @Override
    @Transactional
    public void deleteReminder(Long id) {
        Reminder reminder = findReminderOrThrow(id);
        reminderRepository.delete(reminder);
        log.info("Deleted reminder id={}", id);
    }

    // ==================== HELPER METHODS ====================

    private Reminder findReminderOrThrow(Long id) {
        return reminderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reminder not found with id: " + id));
    }

    private ReminderType parseReminderType(String type) {
        try {
            return ReminderType.valueOf(type.trim().toUpperCase(Locale.ROOT));
        } catch (Exception e) {
            throw new BadRequestException("Invalid reminder type: " + type
                    + ". Valid values: EMAIL, SMS, IN_APP");
        }
    }

    private void validateCreateRequest(CreateReminderRequest request) {
        if (request == null) {
            throw new BadRequestException("Create reminder request is required");
        }

        if (request.scheduledAt() == null) {
            throw new BadRequestException("Scheduled time is required");
        }

        if (request.scheduledAt().isBefore(LocalDateTime.now().minusMinutes(1))) {
            throw new BadRequestException("Scheduled time cannot be in the past");
        }
    }

    private void validateAppointmentForReminder(Appointment appointment) {
        if (appointment == null) {
            throw new BadRequestException("Appointment is required");
        }

        if (appointment.getStatus() == AppointmentStatus.CANCELLED
                || appointment.getStatus() == AppointmentStatus.REJECTED
                || appointment.getStatus() == AppointmentStatus.COMPLETED
                || appointment.getStatus() == AppointmentStatus.MISSED) {
            throw new BadRequestException("Cannot create reminder for appointment in "
                    + appointment.getStatus() + " status");
        }
    }

    private void validateChannelData(Appointment appointment, ReminderType type) {
        if (appointment == null || appointment.getUser() == null || type == null) {
            throw new BadRequestException("Invalid reminder data");
        }

        if (type == ReminderType.EMAIL && isBlank(appointment.getUser().getEmail())) {
            throw new BadRequestException("Cannot send EMAIL reminder: user email is missing");
        }

        if (type == ReminderType.SMS && isBlank(appointment.getUser().getPhone())) {
            throw new BadRequestException("Cannot send SMS reminder: user phone is missing");
        }
    }

    private String buildReminderMessage(String requestMessage, Appointment appointment) {
        if (!isBlank(requestMessage)) {
            return requestMessage;
        }

        return String.format("Reminder: You have an appointment with %s at %s",
                appointment.getServiceProvider().getName(),
                appointment.getStartTime());
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
