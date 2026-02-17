package com.smartappointment.mapper;

import org.springframework.stereotype.Component;

import com.smartappointment.dto.category.requests.CreateCategoryRequest;
import com.smartappointment.dto.category.requests.UpdateCategoryRequest;
import com.smartappointment.dto.category.responses.CategoryResponse;
import com.smartappointment.model.entity.Category;

@Component
public class CategoryMapper {

    public Category toEntity(CreateCategoryRequest request) {
        if (request == null) {
            return null;
        }

        return Category.builder()
                .name(request.name())
                .description(request.description())
                .colorCode(request.colorCode())
                .defaultDurationMinutes(request.defaultDurationMinutes())
                .build();
    }

    public void updateEntityFromRequest(Category category, UpdateCategoryRequest request) {
        if (category == null || request == null) {
            return;
        }

        if (request.name() != null) {
            category.setName(request.name());
        }
        if (request.description() != null) {
            category.setDescription(request.description());
        }
        if (request.colorCode() != null) {
            category.setColorCode(request.colorCode());
        }
        if (request.defaultDurationMinutes() != null) {
            category.setDefaultDurationMinutes(request.defaultDurationMinutes());
        }
    }

    public CategoryResponse toResponse(Category category) {
        if (category == null) {
            return null;
        }

        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.getColorCode(),
                category.getDefaultDurationMinutes(),
                category.isActive(),
                category.getCreatedAt(),
                category.getUpdatedAt());
    }
}
