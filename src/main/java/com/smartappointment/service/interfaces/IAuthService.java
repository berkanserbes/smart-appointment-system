package com.smartappointment.service.interfaces;

import com.smartappointment.dto.auth.AuthResponse;
import com.smartappointment.dto.auth.LoginRequest;
import com.smartappointment.dto.auth.RegisterRequest;
import com.smartappointment.dto.user.UserResponse;

public interface IAuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    AuthResponse refreshToken(String token);

    UserResponse getCurrentUser(String email);
}
