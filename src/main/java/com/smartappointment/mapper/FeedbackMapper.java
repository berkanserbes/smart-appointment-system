package com.smartappointment.mapper;

import org.springframework.stereotype.Component;

import com.smartappointment.dto.feedback.requests.CreateFeedbackRequest;
import com.smartappointment.dto.feedback.requests.UpdateFeedbackRequest;
import com.smartappointment.dto.feedback.responses.FeedbackResponse;
import com.smartappointment.model.entity.Appointment;
import com.smartappointment.model.entity.Feedback;
import com.smartappointment.model.entity.User;

@Component
public class FeedbackMapper {

    public Feedback toEntity(CreateFeedbackRequest request, Appointment appointment, User user) {
        if (request == null) {
            return null;
        }

        return Feedback.builder()
                .appointment(appointment)
                .user(user)
                .rating(request.rating())
                .comment(request.comment())
                .build();
    }

    public void updateEntityFromRequest(Feedback feedback, UpdateFeedbackRequest request) {
        if (feedback == null || request == null) {
            return;
        }

        if (request.rating() != null) {
            feedback.setRating(request.rating());
        }
        if (request.comment() != null) {
            feedback.setComment(request.comment());
        }
    }

    public FeedbackResponse toResponse(Feedback feedback) {
        if (feedback == null) {
            return null;
        }

        return new FeedbackResponse(
                feedback.getId(),
                feedback.getAppointment() != null ? feedback.getAppointment().getId() : null,
                feedback.getAppointment() != null && feedback.getAppointment().getServiceProvider() != null
                        ? feedback.getAppointment().getServiceProvider().getName()
                        : null,
                feedback.getUser() != null ? feedback.getUser().getId() : null,
                feedback.getUser() != null ? feedback.getUser().getFullName() : null,
                feedback.getRating(),
                feedback.getComment(),
                feedback.getCreatedAt(),
                feedback.getUpdatedAt());
    }
}
