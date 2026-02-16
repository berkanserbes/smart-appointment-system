package com.smartappointment.service.interfaces;

import com.smartappointment.dto.provider.ServiceProviderRequest;
import com.smartappointment.dto.provider.ServiceProviderResponse;

import java.util.List;

public interface IServiceProviderService {

    ServiceProviderResponse createProvider(ServiceProviderRequest request);

    List<ServiceProviderResponse> getAllProviders();

    List<ServiceProviderResponse> getActiveProviders();

    List<ServiceProviderResponse> getInactiveProviders();

    ServiceProviderResponse getProviderById(Long id);

    List<ServiceProviderResponse> getProvidersByCategory(Long categoryId);

    List<ServiceProviderResponse> getActiveProvidersByCategory(Long categoryId);

    List<ServiceProviderResponse> searchProvidersByName(String name);

    boolean existsByEmail(String email);

    ServiceProviderResponse updateProvider(Long id, ServiceProviderRequest request);

    void softDeleteProvider(Long id);

    void hardDeleteProvider(Long id);

    void activateProvider(Long id);
}
