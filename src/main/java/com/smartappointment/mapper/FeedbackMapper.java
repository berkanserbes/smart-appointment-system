package com.smartappointment.mapper;

import org.springframework.stereotype.Component;

import com.smartappointment.dto.feedback.responses.FeedbackResponse;
import com.smartappointment.model.entity.Feedback;

@Component
public class FeedbackMapper {

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
