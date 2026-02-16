package com.smartappointment.dto.auth.responses;

public record AuthResponse(
        String token,
        String email,
        String role,
        String fullName) {
}
