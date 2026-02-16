package com.smartappointment.mapper;

import org.springframework.stereotype.Component;

import com.smartappointment.dto.provider.responses.ServiceProviderDetailResponse;
import com.smartappointment.dto.provider.responses.ServiceProviderResponse;
import com.smartappointment.dto.provider.responses.ServiceProviderSummaryResponse;
import com.smartappointment.model.entity.ServiceProvider;

@Component
public class ServiceProviderMapper {

    public ServiceProviderResponse toResponse(ServiceProvider provider) {
        if (provider == null) {
            return null;
        }

        return new ServiceProviderResponse(
                provider.getId(),
                provider.getName(),
                provider.getTitle(),
                provider.getPhone(),
                provider.getEmail(),
                provider.getDescription(),
                provider.getCategory() != null ? provider.getCategory().getId() : null,
                provider.getCategory() != null ? provider.getCategory().getName() : null,
                provider.isActive(),
                provider.getCreatedAt(),
                provider.getUpdatedAt());
    }

    public ServiceProviderDetailResponse toDetailResponse(ServiceProvider provider, Double averageRating) {
        if (provider == null) {
            return null;
        }

        return new ServiceProviderDetailResponse(
                provider.getId(),
                provider.getName(),
                provider.getTitle(),
                provider.getPhone(),
                provider.getEmail(),
                provider.getDescription(),
                provider.getCategory() != null ? provider.getCategory().getId() : null,
                provider.getCategory() != null ? provider.getCategory().getName() : null,
                provider.isActive(),
                averageRating,
                provider.getCreatedAt(),
                provider.getUpdatedAt());
    }

    public ServiceProviderSummaryResponse toSummaryResponse(ServiceProvider provider) {
        if (provider == null) {
            return null;
        }

        return new ServiceProviderSummaryResponse(
                provider.getId(),
                provider.getName(),
                provider.getTitle(),
                provider.getCategory() != null ? provider.getCategory().getName() : null,
                provider.isActive());
    }
}
