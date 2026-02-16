package com.smartappointment.dto.user.requests;

import jakarta.validation.constraints.Size;

public record UpdateUserRequest(

        @Size(max = 50, message = "First name cannot exceed 50 characters") String firstName,

        @Size(max = 50, message = "Last name cannot exceed 50 characters") String lastName,

        @Size(max = 20, message = "Phone cannot exceed 20 characters") String phone) {
}
