package com.smartappointment.repository;

import com.smartappointment.model.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    Optional<Feedback> findByAppointmentId(Long appointmentId);

    List<Feedback> findByUserId(Long userId);

    boolean existsByAppointmentId(Long appointmentId);

    List<Feedback> findByRatingGreaterThanEqual(int minRating);

    long countByUserId(Long userId);

    /**
     * Calculates the average rating for a specific provider.
     * Used for dashboard and provider profile.
     */
    @Query("SELECT AVG(f.rating) FROM Feedback f WHERE f.appointment.serviceProvider.id = :providerId")
    Double getAverageRatingByProviderId(@Param("providerId") Long providerId);

    @Query("SELECT f FROM Feedback f WHERE f.appointment.serviceProvider.id = :providerId ORDER BY f.createdAt DESC")
    List<Feedback> findByProviderId(@Param("providerId") Long providerId);
}
