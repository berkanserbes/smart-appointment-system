package com.smartappointment.service.interfaces;

import com.smartappointment.dto.feedback.requests.CreateFeedbackRequest;
import com.smartappointment.dto.feedback.requests.UpdateFeedbackRequest;
import com.smartappointment.dto.feedback.responses.FeedbackResponse;

import java.util.List;

public interface IFeedbackService {

    FeedbackResponse createFeedback(String userEmail, CreateFeedbackRequest request);

    FeedbackResponse getFeedbackById(Long id);

    FeedbackResponse getFeedbackByAppointment(Long appointmentId);

    List<FeedbackResponse> getUserFeedbacks(String userEmail);

    List<FeedbackResponse> getFeedbacksByProviderId(Long providerId);

    List<FeedbackResponse> getFeedbacksByMinRating(int minRating);

    Double getAverageRatingByProviderId(Long providerId);

    long countByUserId(Long userId);

    boolean existsByAppointmentId(Long appointmentId);

    FeedbackResponse updateFeedback(Long id, String userEmail, UpdateFeedbackRequest request);

    void deleteFeedback(Long id);
}
