package com.smartappointment.model.entity;

import com.smartappointment.model.enums.ReminderStatus;
import com.smartappointment.model.enums.ReminderType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Reminder entity for appointment notifications.
 * Supports multiple reminders per appointment with different channels (EMAIL,
 * SMS, IN_APP).
 */
@Entity
@Table(name = "reminders", indexes = {
        @Index(name = "idx_reminder_status_scheduled", columnList = "status, scheduledAt"),
        @Index(name = "idx_reminder_appointment", columnList = "appointment_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reminder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id", nullable = false)
    private Appointment appointment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ReminderType type = ReminderType.EMAIL;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ReminderStatus status = ReminderStatus.PENDING;

    @Column(nullable = false)
    private LocalDateTime scheduledAt;

    private LocalDateTime sentAt;

    @Column(length = 500)
    private String message;
}
