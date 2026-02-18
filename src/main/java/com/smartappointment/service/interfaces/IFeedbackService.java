package com.smartappointment.service.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.smartappointment.dto.feedback.requests.CreateFeedbackRequest;
import com.smartappointment.dto.feedback.requests.UpdateFeedbackRequest;
import com.smartappointment.dto.feedback.responses.FeedbackResponse;

public interface IFeedbackService {

    FeedbackResponse createFeedback(String userEmail, CreateFeedbackRequest request);

    FeedbackResponse getFeedbackById(Long id);

    FeedbackResponse getFeedbackByAppointment(Long appointmentId);

    Page<FeedbackResponse> getUserFeedbacks(String userEmail, Pageable pageable);

    Page<FeedbackResponse> getFeedbacksByProviderId(Long providerId, Pageable pageable);

    Page<FeedbackResponse> getFeedbacksByMinRating(int minRating, Pageable pageable);

    Double getAverageRatingByProviderId(Long providerId);

    long countByUserId(Long userId);

    boolean existsByAppointmentId(Long appointmentId);

    FeedbackResponse updateFeedback(Long id, String userEmail, UpdateFeedbackRequest request);

    void deleteFeedback(Long id);
}
