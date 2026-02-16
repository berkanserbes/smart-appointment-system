package com.smartappointment.mapper;

import org.springframework.stereotype.Component;

import com.smartappointment.dto.category.responses.CategoryResponse;
import com.smartappointment.model.entity.Category;

@Component
public class CategoryMapper {

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
