package com.smartappointment.dto.auth.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(

        @NotBlank(message = "First name is required") @Size(max = 50, message = "First name cannot exceed 50 characters") String firstName,

        @NotBlank(message = "Last name is required") @Size(max = 50, message = "Last name cannot exceed 50 characters") String lastName,

        @NotBlank(message = "Email is required") @Email(message = "Invalid email format") String email,

        @NotBlank(message = "Password is required") @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters") String password,

        @Size(max = 20, message = "Phone cannot exceed 20 characters") String phone) {
}
