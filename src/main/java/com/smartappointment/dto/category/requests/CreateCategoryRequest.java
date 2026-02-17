package com.smartappointment.dto.category.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCategoryRequest(

        @NotBlank(message = "Category name is required")
        @Size(max = 50, message = "Name cannot exceed 50 characters")
        String name,

        @Size(max = 200, message = "Description cannot exceed 200 characters")
        String description,

        @Schema(example = "#3B82F6", defaultValue = "#3B82F6")
        @Size(max = 7, message = "Color code cannot exceed 7 characters")
        String colorCode,

        @Schema(example = "30", defaultValue = "30")
        Integer defaultDurationMinutes) {

        public CreateCategoryRequest {
                if (colorCode == null || colorCode.isBlank()) {
                        colorCode = "#3B82F6";
                }

                if (defaultDurationMinutes == null) {
                        defaultDurationMinutes = 30;
                }
        }
}
