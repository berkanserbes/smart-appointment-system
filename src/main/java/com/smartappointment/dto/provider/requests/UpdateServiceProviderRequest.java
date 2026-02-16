package com.smartappointment.dto.provider.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateServiceProviderRequest(

        @NotBlank(message = "Name is required") @Size(max = 100, message = "Name cannot exceed 100 characters") String name,

        @NotBlank(message = "Title is required") @Size(max = 100, message = "Title cannot exceed 100 characters") String title,

        @Size(max = 20, message = "Phone cannot exceed 20 characters") String phone,

        @Email(message = "Invalid email format") String email,

        @Size(max = 1000, message = "Description cannot exceed 1000 characters") String description,

        Long categoryId) {
}
