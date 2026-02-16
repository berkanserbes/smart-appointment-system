package com.smartappointment.dto.location.requests;

import jakarta.validation.constraints.Size;

public record UpdateLocationRequest(

        @Size(max = 100, message = "Name cannot exceed 100 characters") String name,

        @Size(max = 255, message = "Address cannot exceed 255 characters") String address,

        @Size(max = 50, message = "City cannot exceed 50 characters") String city) {
}
