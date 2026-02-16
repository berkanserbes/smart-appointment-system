package com.smartappointment.dto.location.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateLocationRequest(

        @NotBlank(message = "Location name is required") @Size(max = 100, message = "Name cannot exceed 100 characters") String name,

        @NotBlank(message = "Address is required") @Size(max = 255, message = "Address cannot exceed 255 characters") String address,

        @NotBlank(message = "City is required") @Size(max = 50, message = "City cannot exceed 50 characters") String city) {
}
