package com.smartappointment.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smartappointment.dto.provider.requests.CreateServiceProviderRequest;
import com.smartappointment.dto.provider.requests.UpdateServiceProviderRequest;
import com.smartappointment.dto.provider.responses.ServiceProviderDetailResponse;
import com.smartappointment.dto.provider.responses.ServiceProviderResponse;
import com.smartappointment.dto.provider.responses.ServiceProviderSummaryResponse;
import com.smartappointment.exception.BadRequestException;
import com.smartappointment.exception.ResourceNotFoundException;
import com.smartappointment.mapper.ServiceProviderMapper;
import com.smartappointment.model.entity.Category;
import com.smartappointment.model.entity.ServiceProvider;
import com.smartappointment.repository.CategoryRepository;
import com.smartappointment.repository.FeedbackRepository;
import com.smartappointment.repository.ServiceProviderRepository;
import com.smartappointment.service.interfaces.IServiceProviderService;

@Service
public class ServiceProviderService implements IServiceProviderService {

    private final ServiceProviderRepository providerRepository;
    private final CategoryRepository categoryRepository;
    private final FeedbackRepository feedbackRepository;
    private final ServiceProviderMapper serviceProviderMapper;

    public ServiceProviderService(ServiceProviderRepository providerRepository,
            CategoryRepository categoryRepository, FeedbackRepository feedbackRepository,
            ServiceProviderMapper serviceProviderMapper) {
        this.providerRepository = providerRepository;
        this.categoryRepository = categoryRepository;
        this.feedbackRepository = feedbackRepository;
        this.serviceProviderMapper = serviceProviderMapper;
    }

    // ==================== CREATE ====================

    @Override
    @Transactional
    public ServiceProviderResponse createProvider(CreateServiceProviderRequest request) {
        if (request.email() != null && providerRepository.existsByEmail(request.email())) {
            throw new BadRequestException("Provider with this email already exists: " + request.email());
        }

        Category category = findCategoryOrThrow(request.categoryId());

        ServiceProvider provider = serviceProviderMapper.toEntity(request, category);

        ServiceProvider saved = providerRepository.save(provider);
        return serviceProviderMapper.toResponse(saved);
    }

    // ==================== GET OPERATIONS ====================

    @Override
    public List<ServiceProviderSummaryResponse> getAllProviders() {
        return providerRepository.findAll().stream()
                .map(serviceProviderMapper::toSummaryResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ServiceProviderSummaryResponse> getActiveProviders() {
        return providerRepository.findByActiveTrue().stream()
                .map(serviceProviderMapper::toSummaryResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ServiceProviderSummaryResponse> getInactiveProviders() {
        return providerRepository.findByActiveFalse().stream()
                .map(serviceProviderMapper::toSummaryResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ServiceProviderDetailResponse getProviderById(Long id) {
        ServiceProvider provider = findProviderOrThrow(id);
        return serviceProviderMapper.toDetailResponse(provider, feedbackRepository.getAverageRatingByProviderId(provider.getId()));
    }

    @Override
    public List<ServiceProviderSummaryResponse> getProvidersByCategory(Long categoryId) {
        return providerRepository.findByCategoryId(categoryId).stream()
                .map(serviceProviderMapper::toSummaryResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ServiceProviderSummaryResponse> getActiveProvidersByCategory(Long categoryId) {
        return providerRepository.findByCategoryIdAndActiveTrue(categoryId).stream()
                .map(serviceProviderMapper::toSummaryResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ServiceProviderSummaryResponse> searchProvidersByName(String name) {
        return providerRepository.findByNameContainingIgnoreCase(name).stream()
                .map(serviceProviderMapper::toSummaryResponse)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByEmail(String email) {
        return providerRepository.existsByEmail(email);
    }

    // ==================== UPDATE ====================

    @Override
    @Transactional
    public ServiceProviderResponse updateProvider(Long id, UpdateServiceProviderRequest request) {
        ServiceProvider provider = findProviderOrThrow(id);

        if (request.email() != null && providerRepository.existsByEmailAndIdNot(request.email(), id)) {
            throw new BadRequestException("Another provider with this email already exists: " + request.email());
        }

        Category category = request.categoryId() != null ? findCategoryOrThrow(request.categoryId()) : null;
        serviceProviderMapper.updateEntityFromRequest(provider, request, category);

        ServiceProvider updated = providerRepository.save(provider);
        return serviceProviderMapper.toResponse(updated);
    }

    // ==================== STATUS OPERATIONS ====================

    @Override
    @Transactional
    public void activateProvider(Long id) {
        ServiceProvider provider = findProviderOrThrow(id);
        provider.setActive(true);
        providerRepository.save(provider);
    }

    // ==================== DELETE OPERATIONS ====================

    @Override
    @Transactional
    public void softDeleteProvider(Long id) {
        ServiceProvider provider = findProviderOrThrow(id);
        provider.setActive(false);
        providerRepository.save(provider);
    }

    @Override
    @Transactional
    public void hardDeleteProvider(Long id) {
        ServiceProvider provider = findProviderOrThrow(id);
        providerRepository.delete(provider);
    }

    // ==================== HELPER METHODS ====================

    private ServiceProvider findProviderOrThrow(Long id) {
        return providerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service provider not found with id: " + id));
    }

    private Category findCategoryOrThrow(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId));
    }
}
