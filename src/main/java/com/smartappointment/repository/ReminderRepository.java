package com.smartappointment.repository;

import com.smartappointment.model.entity.Reminder;
import com.smartappointment.model.enums.ReminderStatus;
import com.smartappointment.model.enums.ReminderType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReminderRepository extends JpaRepository<Reminder, Long> {

    /**
     * Finds reminders that need to be sent:
     * Status = PENDING and scheduledAt has passed.
     * Called periodically by the scheduler.
     */
    List<Reminder> findByStatusAndScheduledAtBefore(ReminderStatus status, LocalDateTime dateTime);

    List<Reminder> findByAppointmentId(Long appointmentId);

    List<Reminder> findByStatus(ReminderStatus status);

    List<Reminder> findByType(ReminderType type);

    boolean existsByAppointmentIdAndType(Long appointmentId, ReminderType type);

    long countByStatus(ReminderStatus status);

    void deleteByAppointmentId(Long appointmentId);
}
