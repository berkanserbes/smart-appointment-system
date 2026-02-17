package com.smartappointment.mapper;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.smartappointment.dto.auth.requests.RegisterRequest;
import com.smartappointment.dto.auth.responses.LoginResponse;
import com.smartappointment.dto.auth.responses.RegisterResponse;
import com.smartappointment.dto.user.responses.UserResponse;
import com.smartappointment.model.entity.User;
import com.smartappointment.model.enums.Role;

@Component
public class AuthMapper {

    public User toEntity(RegisterRequest request, String encodedPassword) {
        if (request == null) {
            return null;
        }

        return User.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .password(encodedPassword)
                .phone(request.phone())
                .role(Role.USER)
                .active(true)
                .build();
    }

    public UserDetails toUserDetails(User user) {
        if (user == null) {
            return null;
        }

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .authorities("ROLE_" + user.getRole().name())
                .build();
    }

    public LoginResponse toLoginResponse(String token, long expiresIn, User user) {
        if (user == null) {
            return null;
        }

        return new LoginResponse(
                token,
                expiresIn,
                user.getEmail(),
                user.getRole().name(),
                user.getFullName());
    }

    public RegisterResponse toRegisterResponse(User user) {
        if (user == null) {
            return null;
        }

        return new RegisterResponse(
                user.getEmail(),
                user.getRole().name(),
                user.getFullName());
    }

    public UserResponse toUserResponse(User user) {
        if (user == null) {
            return null;
        }

        return new UserResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhone(),
                user.getRole().name(),
                user.isActive(),
                user.getCreatedAt(),
                user.getUpdatedAt());
    }
}
