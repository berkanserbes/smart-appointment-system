package com.smartappointment.mapper;

import org.springframework.stereotype.Component;

import com.smartappointment.dto.provider.requests.CreateServiceProviderRequest;
import com.smartappointment.dto.provider.requests.UpdateServiceProviderRequest;
import com.smartappointment.model.entity.Category;
import com.smartappointment.dto.provider.responses.ServiceProviderDetailResponse;
import com.smartappointment.dto.provider.responses.ServiceProviderResponse;
import com.smartappointment.dto.provider.responses.ServiceProviderSummaryResponse;
import com.smartappointment.model.entity.ServiceProvider;

@Component
public class ServiceProviderMapper {

    public ServiceProvider toEntity(CreateServiceProviderRequest request, Category category) {
        if (request == null) {
            return null;
        }

        return ServiceProvider.builder()
                .name(request.name())
                .title(request.title())
                .phone(request.phone())
                .email(request.email())
                .description(request.description())
                .category(category)
                .active(true)
                .build();
    }

    public void updateEntityFromRequest(ServiceProvider provider, UpdateServiceProviderRequest request, Category category) {
        if (provider == null || request == null) {
            return;
        }

        if (request.name() != null) {
            provider.setName(request.name());
        }
        if (request.title() != null) {
            provider.setTitle(request.title());
        }
        if (request.phone() != null) {
            provider.setPhone(request.phone());
        }
        if (request.email() != null) {
            provider.setEmail(request.email());
        }
        if (request.description() != null) {
            provider.setDescription(request.description());
        }
        if (category != null) {
            provider.setCategory(category);
        }
    }

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
