package com.smartappointment.service.interfaces;

import com.smartappointment.dto.auth.responses.AuthResponse;
import com.smartappointment.dto.auth.requests.LoginRequest;
import com.smartappointment.dto.auth.requests.RegisterRequest;
import com.smartappointment.dto.user.responses.UserResponse;

public interface IAuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    AuthResponse refreshToken(String token);

    UserResponse getCurrentUser(String email);
}
