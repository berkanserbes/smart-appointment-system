package com.smartappointment.service.interfaces;

import com.smartappointment.dto.provider.requests.CreateServiceProviderRequest;
import com.smartappointment.dto.provider.requests.UpdateServiceProviderRequest;
import com.smartappointment.dto.provider.responses.ServiceProviderDetailResponse;
import com.smartappointment.dto.provider.responses.ServiceProviderResponse;
import com.smartappointment.dto.provider.responses.ServiceProviderSummaryResponse;

import java.util.List;

public interface IServiceProviderService {

    ServiceProviderResponse createProvider(CreateServiceProviderRequest request);

    List<ServiceProviderSummaryResponse> getAllProviders();

    List<ServiceProviderSummaryResponse> getActiveProviders();

    List<ServiceProviderSummaryResponse> getInactiveProviders();

    ServiceProviderDetailResponse getProviderById(Long id);

    List<ServiceProviderSummaryResponse> getProvidersByCategory(Long categoryId);

    List<ServiceProviderSummaryResponse> getActiveProvidersByCategory(Long categoryId);

    List<ServiceProviderSummaryResponse> searchProvidersByName(String name);

    boolean existsByEmail(String email);

    ServiceProviderResponse updateProvider(Long id, UpdateServiceProviderRequest request);

    void softDeleteProvider(Long id);

    void hardDeleteProvider(Long id);

    void activateProvider(Long id);
}
