package com.smartappointment.dto.auth.responses;

public record LoginResponse(
        String token,
        long expiresIn,
        String email,
        String role,
        String fullName) {
}
