package com.smartappointment.repository;

import com.smartappointment.model.entity.Appointment;
import com.smartappointment.model.enums.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

        // ==================== User-based queries ====================

        List<Appointment> findByUserIdOrderByStartTimeDesc(Long userId);

        List<Appointment> findByUserIdAndStatus(Long userId, AppointmentStatus status);

        List<Appointment> findByUserIdAndStatusNot(Long userId, AppointmentStatus status);

        List<Appointment> findByUserIdAndStatusIn(Long userId, List<AppointmentStatus> statuses);

        @Query("SELECT a FROM Appointment a WHERE a.user.id = :userId AND a.startTime >= :now ORDER BY a.startTime ASC")
        List<Appointment> findUpcomingByUserId(@Param("userId") Long userId, @Param("now") LocalDateTime now);

        @Query("SELECT a FROM Appointment a WHERE a.user.id = :userId AND a.endTime < :now ORDER BY a.startTime DESC")
        List<Appointment> findPastByUserId(@Param("userId") Long userId, @Param("now") LocalDateTime now);

        long countByUserId(Long userId);

        long countByUserIdAndStatus(Long userId, AppointmentStatus status);

        // ==================== Provider-based queries ====================

        List<Appointment> findByServiceProviderIdOrderByStartTimeDesc(Long providerId);

        List<Appointment> findByServiceProviderIdAndStatus(Long providerId, AppointmentStatus status);

        @Query("SELECT a FROM Appointment a WHERE a.serviceProvider.id = :providerId AND a.startTime >= :now AND a.status IN ('SCHEDULED', 'CONFIRMED') ORDER BY a.startTime ASC")
        List<Appointment> findUpcomingByProviderId(@Param("providerId") Long providerId,
                        @Param("now") LocalDateTime now);

        long countByServiceProviderId(Long providerId);

        // ==================== Location-based queries ====================

        List<Appointment> findByLocationId(Long locationId);

        List<Appointment> findByLocationIdAndStartTimeBetween(Long locationId, LocalDateTime start, LocalDateTime end);

        // ==================== Status-based queries ====================

        List<Appointment> findByStatus(AppointmentStatus status);

        long countByStatus(AppointmentStatus status);

        // ==================== Date range & reporting queries ====================

        @Query("SELECT a FROM Appointment a WHERE a.startTime BETWEEN :start AND :end ORDER BY a.startTime")
        List<Appointment> findByDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

        @Query("SELECT COUNT(a) FROM Appointment a WHERE a.startTime BETWEEN :start AND :end AND a.status = :status")
        long countByDateRangeAndStatus(
                        @Param("start") LocalDateTime start,
                        @Param("end") LocalDateTime end,
                        @Param("status") AppointmentStatus status);

        @Query("SELECT a FROM Appointment a WHERE a.serviceProvider.id = :providerId AND a.startTime BETWEEN :start AND :end ORDER BY a.startTime")
        List<Appointment> findByProviderIdAndDateRange(
                        @Param("providerId") Long providerId,
                        @Param("start") LocalDateTime start,
                        @Param("end") LocalDateTime end);

        // ==================== Conflict detection ====================

        /**
         * Checks provider time slot conflicts.
         * Two time ranges overlap when: start1 < end2 AND start2 < end1.
         * Cancelled appointments are excluded from conflict detection.
         */
        @Query("SELECT a FROM Appointment a WHERE a.serviceProvider.id = :providerId " +
                        "AND a.status <> 'CANCELLED' " +
                        "AND a.startTime < :endTime AND a.endTime > :startTime")
        List<Appointment> findConflictingAppointments(
                        @Param("providerId") Long providerId,
                        @Param("startTime") LocalDateTime startTime,
                        @Param("endTime") LocalDateTime endTime);

        /**
         * Checks user time slot conflicts.
         * Prevents a user from booking overlapping appointments.
         */
        @Query("SELECT a FROM Appointment a WHERE a.user.id = :userId " +
                        "AND a.status <> 'CANCELLED' " +
                        "AND a.startTime < :endTime AND a.endTime > :startTime")
        List<Appointment> findUserConflictingAppointments(
                        @Param("userId") Long userId,
                        @Param("startTime") LocalDateTime startTime,
                        @Param("endTime") LocalDateTime endTime);

        /**
         * Finds appointments that should be marked as MISSED.
         * Appointments with SCHEDULED or CONFIRMED status whose endTime has passed.
         */
        @Query("SELECT a FROM Appointment a WHERE a.status IN ('SCHEDULED', 'CONFIRMED') AND a.endTime < :now")
        List<Appointment> findMissedAppointments(@Param("now") LocalDateTime now);
}
