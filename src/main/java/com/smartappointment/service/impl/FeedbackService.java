package com.smartappointment.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smartappointment.dto.feedback.requests.CreateFeedbackRequest;
import com.smartappointment.dto.feedback.requests.UpdateFeedbackRequest;
import com.smartappointment.dto.feedback.responses.FeedbackResponse;
import com.smartappointment.exception.BadRequestException;
import com.smartappointment.exception.ResourceNotFoundException;
import com.smartappointment.mapper.FeedbackMapper;
import com.smartappointment.model.entity.Appointment;
import com.smartappointment.model.entity.Feedback;
import com.smartappointment.model.entity.User;
import com.smartappointment.model.enums.AppointmentStatus;
import com.smartappointment.repository.AppointmentRepository;
import com.smartappointment.repository.FeedbackRepository;
import com.smartappointment.repository.UserRepository;
import com.smartappointment.service.interfaces.IFeedbackService;

@Service
public class FeedbackService implements IFeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final FeedbackMapper feedbackMapper;

    public FeedbackService(FeedbackRepository feedbackRepository,
            AppointmentRepository appointmentRepository, UserRepository userRepository, FeedbackMapper feedbackMapper) {
        this.feedbackRepository = feedbackRepository;
        this.appointmentRepository = appointmentRepository;
        this.userRepository = userRepository;
        this.feedbackMapper = feedbackMapper;
    }

    // ==================== CREATE ====================

    @Override
    @Transactional
    public FeedbackResponse createFeedback(String userEmail, CreateFeedbackRequest request) {
        User user = findUserByEmailOrThrow(userEmail);

        Appointment appointment = appointmentRepository.findById(request.appointmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));

        if (appointment.getStatus() != AppointmentStatus.COMPLETED) {
            throw new BadRequestException("Feedback can only be given for completed appointments");
        }

        if (feedbackRepository.existsByAppointmentId(request.appointmentId())) {
            throw new BadRequestException("Feedback already exists for this appointment");
        }

        if (!appointment.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("You can only give feedback for your own appointments");
        }

        Feedback feedback = feedbackMapper.toEntity(request, appointment, user);

        Feedback saved = feedbackRepository.save(feedback);
        return feedbackMapper.toResponse(saved);
    }

    // ==================== GET OPERATIONS ====================

    @Override
    public FeedbackResponse getFeedbackById(Long id) {
        Feedback feedback = findFeedbackOrThrow(id);
        return feedbackMapper.toResponse(feedback);
    }

    @Override
    public FeedbackResponse getFeedbackByAppointment(Long appointmentId) {
        Feedback feedback = feedbackRepository.findByAppointmentId(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Feedback not found for appointment: " + appointmentId));
        return feedbackMapper.toResponse(feedback);
    }

    @Override
    public List<FeedbackResponse> getUserFeedbacks(String userEmail) {
        User user = findUserByEmailOrThrow(userEmail);

        return feedbackRepository.findByUserId(user.getId()).stream()
            .map(feedbackMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<FeedbackResponse> getFeedbacksByProviderId(Long providerId) {
        return feedbackRepository.findByProviderId(providerId).stream()
                .map(feedbackMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<FeedbackResponse> getFeedbacksByMinRating(int minRating) {
        return feedbackRepository.findByRatingGreaterThanEqual(minRating).stream()
                .map(feedbackMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Double getAverageRatingByProviderId(Long providerId) {
        return feedbackRepository.getAverageRatingByProviderId(providerId);
    }

    @Override
    public long countByUserId(Long userId) {
        return feedbackRepository.countByUserId(userId);
    }

    @Override
    public boolean existsByAppointmentId(Long appointmentId) {
        return feedbackRepository.existsByAppointmentId(appointmentId);
    }

    // ==================== UPDATE ====================

    @Override
    @Transactional
    public FeedbackResponse updateFeedback(Long id, String userEmail, UpdateFeedbackRequest request) {
        Feedback feedback = findFeedbackOrThrow(id);

        if (!feedback.getUser().getEmail().equals(userEmail)) {
            throw new BadRequestException("You can only update your own feedback");
        }

        feedbackMapper.updateEntityFromRequest(feedback, request);

        Feedback updated = feedbackRepository.save(feedback);
        return feedbackMapper.toResponse(updated);
    }

    // ==================== DELETE OPERATIONS ====================

    @Override
    @Transactional
    public void deleteFeedback(Long id) {
        Feedback feedback = findFeedbackOrThrow(id);
        feedbackRepository.delete(feedback);
    }

    // ==================== HELPER METHODS ====================

    private Feedback findFeedbackOrThrow(Long id) {
        return feedbackRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Feedback not found with id: " + id));
    }

    private User findUserByEmailOrThrow(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
