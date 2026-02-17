package com.smartappointment.dto.auth.responses;

public record RegisterResponse(
        String email,
        String role,
        String fullName) {
}
