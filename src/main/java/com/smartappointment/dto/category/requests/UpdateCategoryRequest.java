package com.smartappointment.dto.category.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateCategoryRequest(

        @NotBlank(message = "Category name is required") @Size(max = 50, message = "Name cannot exceed 50 characters") String name,

        @Size(max = 200, message = "Description cannot exceed 200 characters") String description,

        @Size(max = 7, message = "Color code cannot exceed 7 characters") String colorCode,

        Integer defaultDurationMinutes) {

        public UpdateCategoryRequest {
                if (defaultDurationMinutes == null) {
                        defaultDurationMinutes = 30;
                }
        }
}
