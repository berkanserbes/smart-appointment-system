package com.smartappointment.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.smartappointment.model.entity.Appointment;
import com.smartappointment.model.enums.AppointmentStatus;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

        // ==================== User-based queries ====================

        Page<Appointment> findByUserIdOrderByStartTimeDesc(Long userId, Pageable pageable);

        Page<Appointment> findByUserIdAndStatus(Long userId, AppointmentStatus status, Pageable pageable);

        List<Appointment> findByUserIdAndStatusNot(Long userId, AppointmentStatus status);

        List<Appointment> findByUserIdAndStatusIn(Long userId, List<AppointmentStatus> statuses);

        @Query(value = "SELECT a FROM Appointment a WHERE a.user.id = :userId AND a.startTime >= :now ORDER BY a.startTime ASC",
                countQuery = "SELECT count(a) FROM Appointment a WHERE a.user.id = :userId AND a.startTime >= :now")
        Page<Appointment> findUpcomingByUserId(@Param("userId") Long userId, @Param("now") LocalDateTime now, Pageable pageable);

        @Query(value = "SELECT a FROM Appointment a WHERE a.user.id = :userId AND a.endTime < :now ORDER BY a.startTime DESC",
                countQuery = "SELECT count(a) FROM Appointment a WHERE a.user.id = :userId AND a.endTime < :now")
        Page<Appointment> findPastByUserId(@Param("userId") Long userId, @Param("now") LocalDateTime now, Pageable pageable);

        long countByUserId(Long userId);

        long countByUserIdAndStatus(Long userId, AppointmentStatus status);

        // ==================== Provider-based queries ====================

        Page<Appointment> findByServiceProviderIdOrderByStartTimeDesc(Long providerId, Pageable pageable);

        List<Appointment> findByServiceProviderIdAndStatus(Long providerId, AppointmentStatus status);

        @Query(value = "SELECT a FROM Appointment a WHERE a.serviceProvider.id = :providerId AND a.startTime >= :now AND a.status IN ('SCHEDULED', 'CONFIRMED') ORDER BY a.startTime ASC",
                countQuery = "SELECT count(a) FROM Appointment a WHERE a.serviceProvider.id = :providerId AND a.startTime >= :now AND a.status IN ('SCHEDULED', 'CONFIRMED')")
        Page<Appointment> findUpcomingByProviderId(@Param("providerId") Long providerId, @Param("now") LocalDateTime now, Pageable pageable);

        long countByServiceProviderId(Long providerId);

        // ==================== Location-based queries ====================

        Page<Appointment> findByLocationId(Long locationId, Pageable pageable);

        List<Appointment> findByLocationIdAndStartTimeBetween(Long locationId, LocalDateTime start, LocalDateTime end);

        // ==================== Status-based queries ====================

        Page<Appointment> findByStatus(AppointmentStatus status, Pageable pageable);

        long countByStatus(AppointmentStatus status);

        // ==================== Date range & reporting queries ====================

        @Query(value = "SELECT a FROM Appointment a WHERE a.startTime BETWEEN :start AND :end ORDER BY a.startTime",
                countQuery = "SELECT count(a) FROM Appointment a WHERE a.startTime BETWEEN :start AND :end")
        Page<Appointment> findByDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end, Pageable pageable);

        long countByStartTimeBetween(LocalDateTime start, LocalDateTime end);

        @Query("SELECT COUNT(a) FROM Appointment a WHERE a.startTime BETWEEN :start AND :end AND a.status = :status")
        long countByDateRangeAndStatus(
                        @Param("start") LocalDateTime start,
                        @Param("end") LocalDateTime end,
                        @Param("status") AppointmentStatus status);

        @Query(value = "SELECT a FROM Appointment a WHERE a.serviceProvider.id = :providerId AND a.startTime BETWEEN :start AND :end ORDER BY a.startTime",
                countQuery = "SELECT count(a) FROM Appointment a WHERE a.serviceProvider.id = :providerId AND a.startTime BETWEEN :start AND :end")
        Page<Appointment> findByProviderIdAndDateRange(
                        @Param("providerId") Long providerId,
                        @Param("start") LocalDateTime start,
                        @Param("end") LocalDateTime end,
                        Pageable pageable);

        long countByServiceProviderIdAndStartTimeBetween(Long providerId, LocalDateTime start, LocalDateTime end);

        @Query("SELECT COUNT(a) FROM Appointment a WHERE a.serviceProvider.id = :providerId AND a.startTime BETWEEN :start AND :end AND a.status = :status")
        long countByProviderIdAndDateRangeAndStatus(
                        @Param("providerId") Long providerId,
                        @Param("start") LocalDateTime start,
                        @Param("end") LocalDateTime end,
                        @Param("status") AppointmentStatus status);

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
