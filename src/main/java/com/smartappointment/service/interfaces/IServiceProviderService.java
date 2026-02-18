package com.smartappointment.service.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.smartappointment.dto.provider.requests.CreateServiceProviderRequest;
import com.smartappointment.dto.provider.requests.UpdateServiceProviderRequest;
import com.smartappointment.dto.provider.responses.ServiceProviderDetailResponse;
import com.smartappointment.dto.provider.responses.ServiceProviderResponse;
import com.smartappointment.dto.provider.responses.ServiceProviderSummaryResponse;

public interface IServiceProviderService {

    ServiceProviderResponse createProvider(CreateServiceProviderRequest request);

    Page<ServiceProviderSummaryResponse> getAllProviders(Pageable pageable);

    Page<ServiceProviderSummaryResponse> getActiveProviders(Pageable pageable);

    Page<ServiceProviderSummaryResponse> getInactiveProviders(Pageable pageable);

    ServiceProviderDetailResponse getProviderById(Long id);

    Page<ServiceProviderSummaryResponse> getProvidersByCategory(Long categoryId, Pageable pageable);

    Page<ServiceProviderSummaryResponse> getActiveProvidersByCategory(Long categoryId, Pageable pageable);

    Page<ServiceProviderSummaryResponse> searchProvidersByName(String name, Pageable pageable);

    boolean existsByEmail(String email);

    ServiceProviderResponse updateProvider(Long id, UpdateServiceProviderRequest request);

    void softDeleteProvider(Long id);

    void hardDeleteProvider(Long id);

    void activateProvider(Long id);
}
