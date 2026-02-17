package com.smartappointment.service.interfaces;

import com.smartappointment.dto.auth.requests.LoginRequest;
import com.smartappointment.dto.auth.requests.RegisterRequest;
import com.smartappointment.dto.auth.responses.LoginResponse;
import com.smartappointment.dto.auth.responses.RegisterResponse;
import com.smartappointment.dto.user.responses.UserResponse;

public interface IAuthService {

    RegisterResponse register(RegisterRequest request);

    LoginResponse login(LoginRequest request);

    UserResponse getCurrentUser(String email);
}
