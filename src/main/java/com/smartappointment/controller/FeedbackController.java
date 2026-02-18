package com.smartappointment.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smartappointment.config.swagger.ApiResponseAnnotations.CreatedResponses;
import com.smartappointment.config.swagger.ApiResponseAnnotations.DeleteResponses;
import com.smartappointment.config.swagger.ApiResponseAnnotations.GetResponses;
import com.smartappointment.config.swagger.ApiResponseAnnotations.StandardResponses;
import com.smartappointment.config.swagger.ApiResponseAnnotations.UpdateResponses;
import com.smartappointment.dto.feedback.requests.CreateFeedbackRequest;
import com.smartappointment.dto.feedback.requests.UpdateFeedbackRequest;
import com.smartappointment.dto.feedback.responses.FeedbackResponse;
import com.smartappointment.service.interfaces.IFeedbackService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/feedbacks")
@Tag(name = "Feedbacks", description = "Feedback and rating management endpoints")
@SecurityRequirement(name = "bearerAuth")
public class FeedbackController {

    private final IFeedbackService feedbackService;

    public FeedbackController(IFeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @PostMapping
    @Operation(summary = "Create feedback", description = "Creates feedback for a completed appointment")
    @CreatedResponses
    public ResponseEntity<FeedbackResponse> createFeedback(
            Authentication authentication,
            @Valid @RequestBody CreateFeedbackRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(feedbackService.createFeedback(authentication.getName(), request));
    }

    @GetMapping
    @Operation(summary = "Get my feedbacks", description = "Retrieves all feedbacks created by the current user")
    @StandardResponses
    public ResponseEntity<List<FeedbackResponse>> getMyFeedbacks(Authentication authentication) {
        return ResponseEntity.ok(feedbackService.getUserFeedbacks(authentication.getName()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get feedback by ID", description = "Retrieves a specific feedback by its ID")
    @GetResponses
    public ResponseEntity<FeedbackResponse> getFeedbackById(@PathVariable Long id) {
        return ResponseEntity.ok(feedbackService.getFeedbackById(id));
    }

    @GetMapping("/appointment/{appointmentId}")
    @Operation(summary = "Get feedback by appointment", description = "Retrieves feedback for a specific appointment")
    @GetResponses
    public ResponseEntity<FeedbackResponse> getFeedbackByAppointment(@PathVariable Long appointmentId) {
        return ResponseEntity.ok(feedbackService.getFeedbackByAppointment(appointmentId));
    }

    @GetMapping("/appointment/{appointmentId}/exists")
    @Operation(summary = "Check if feedback exists", description = "Checks if feedback exists for a specific appointment")
    @StandardResponses
    public ResponseEntity<Boolean> existsByAppointment(@PathVariable Long appointmentId) {
        return ResponseEntity.ok(feedbackService.existsByAppointmentId(appointmentId));
    }

    @GetMapping("/provider/{providerId}")
    @Operation(summary = "Get provider feedbacks", description = "Retrieves all feedbacks for a specific service provider")
    @StandardResponses
    public ResponseEntity<List<FeedbackResponse>> getFeedbacksByProvider(@PathVariable Long providerId) {
        return ResponseEntity.ok(feedbackService.getFeedbacksByProviderId(providerId));
    }

    @GetMapping("/provider/{providerId}/average")
    @Operation(summary = "Get provider average rating", description = "Retrieves the average rating for a specific service provider")
    @StandardResponses
    public ResponseEntity<Double> getAverageRating(@PathVariable Long providerId) {
        return ResponseEntity.ok(feedbackService.getAverageRatingByProviderId(providerId));
    }

    @GetMapping("/min-rating/{minRating}")
    @Operation(summary = "Get feedbacks by minimum rating", description = "Retrieves all feedbacks with rating greater than or equal to specified value")
    @StandardResponses
    public ResponseEntity<List<FeedbackResponse>> getFeedbacksByMinRating(@PathVariable int minRating) {
        return ResponseEntity.ok(feedbackService.getFeedbacksByMinRating(minRating));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update feedback", description = "Updates your own feedback")
    @UpdateResponses
    public ResponseEntity<FeedbackResponse> updateFeedback(
            @PathVariable Long id,
            Authentication authentication,
            @Valid @RequestBody UpdateFeedbackRequest request) {
        return ResponseEntity.ok(feedbackService.updateFeedback(id, authentication.getName(), request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete feedback", description = "Deletes a feedback (Admin only)")
    @DeleteResponses
    public ResponseEntity<Void> deleteFeedback(@PathVariable Long id) {
        feedbackService.deleteFeedback(id);
        return ResponseEntity.noContent().build();
    }
}
