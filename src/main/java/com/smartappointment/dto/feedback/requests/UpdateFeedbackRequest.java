package com.smartappointment.dto.feedback.requests;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record UpdateFeedbackRequest(

        @Min(value = 1, message = "Rating must be at least 1") @Max(value = 5, message = "Rating cannot exceed 5") Integer rating,

        @Size(max = 500, message = "Comment cannot exceed 500 characters") String comment) {
}
