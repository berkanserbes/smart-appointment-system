package com.smartappointment.repository;

import com.smartappointment.model.entity.Feedback;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    Optional<Feedback> findByAppointmentId(Long appointmentId);

    Page<Feedback> findByUserId(Long userId, Pageable pageable);

    boolean existsByAppointmentId(Long appointmentId);

    Page<Feedback> findByRatingGreaterThanEqual(int minRating, Pageable pageable);

    long countByUserId(Long userId);

    /**
     * Calculates the average rating for a specific provider.
     * Used for dashboard and provider profile.
     */
    @Query("SELECT AVG(f.rating) FROM Feedback f WHERE f.appointment.serviceProvider.id = :providerId")
    Double getAverageRatingByProviderId(@Param("providerId") Long providerId);

    @Query(value = "SELECT f FROM Feedback f WHERE f.appointment.serviceProvider.id = :providerId ORDER BY f.createdAt DESC",
           countQuery = "SELECT count(f) FROM Feedback f WHERE f.appointment.serviceProvider.id = :providerId")
    Page<Feedback> findByProviderId(@Param("providerId") Long providerId, Pageable pageable);
}
