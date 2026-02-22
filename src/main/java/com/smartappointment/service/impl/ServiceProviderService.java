package com.smartappointment.service.impl;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    @CacheEvict(value = "providers", allEntries = true)
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
    @Cacheable(value = "providers", key = "'all:p:' + #pageable.pageNumber + ':s:' + #pageable.pageSize")
    public Page<ServiceProviderSummaryResponse> getAllProviders(Pageable pageable) {
        return providerRepository.findAll(pageable).map(serviceProviderMapper::toSummaryResponse);
    }

    @Override
    @Cacheable(value = "providers", key = "'active:p:' + #pageable.pageNumber + ':s:' + #pageable.pageSize")
    public Page<ServiceProviderSummaryResponse> getActiveProviders(Pageable pageable) {
        return providerRepository.findByActiveTrue(pageable).map(serviceProviderMapper::toSummaryResponse);
    }

    @Override
    public Page<ServiceProviderSummaryResponse> getInactiveProviders(Pageable pageable) {
        return providerRepository.findByActiveFalse(pageable).map(serviceProviderMapper::toSummaryResponse);
    }

    @Override
    @Cacheable(value = "providers", key = "'id:' + #id")
    public ServiceProviderDetailResponse getProviderById(Long id) {
        ServiceProvider provider = findProviderOrThrow(id);
        return serviceProviderMapper.toDetailResponse(provider, feedbackRepository.getAverageRatingByProviderId(provider.getId()));
    }

    @Override
    @Cacheable(value = "providers", key = "'category:' + #categoryId + ':p:' + #pageable.pageNumber + ':s:' + #pageable.pageSize")
    public Page<ServiceProviderSummaryResponse> getProvidersByCategory(Long categoryId, Pageable pageable) {
        return providerRepository.findByCategoryId(categoryId, pageable).map(serviceProviderMapper::toSummaryResponse);
    }

    @Override
    public Page<ServiceProviderSummaryResponse> getActiveProvidersByCategory(Long categoryId, Pageable pageable) {
        return providerRepository.findByCategoryIdAndActiveTrue(categoryId, pageable).map(serviceProviderMapper::toSummaryResponse);
    }

    @Override
    public Page<ServiceProviderSummaryResponse> searchProvidersByName(String name, Pageable pageable) {
        return providerRepository.findByNameContainingIgnoreCase(name, pageable).map(serviceProviderMapper::toSummaryResponse);
    }

    @Override
    public boolean existsByEmail(String email) {
        return providerRepository.existsByEmail(email);
    }

    // ==================== UPDATE ====================

    @Override
    @Transactional
    @CacheEvict(value = "providers", allEntries = true)
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
    @CacheEvict(value = "providers", allEntries = true)
    public void activateProvider(Long id) {
        ServiceProvider provider = findProviderOrThrow(id);
        provider.setActive(true);
        providerRepository.save(provider);
    }

    // ==================== DELETE OPERATIONS ====================

    @Override
    @Transactional
    @CacheEvict(value = "providers", allEntries = true)
    public void softDeleteProvider(Long id) {
        ServiceProvider provider = findProviderOrThrow(id);
        provider.setActive(false);
        providerRepository.save(provider);
    }

    @Override
    @Transactional
    @CacheEvict(value = "providers", allEntries = true)
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
