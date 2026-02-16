package com.smartappointment.mapper;

import org.springframework.stereotype.Component;

import com.smartappointment.dto.user.responses.UserResponse;
import com.smartappointment.model.entity.User;

@Component
public class UserMapper {

    public UserResponse toResponse(User user) {
        if (user == null) {
            return null;
        }

        return new UserResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhone(),
                user.getRole() != null ? user.getRole().name() : null,
                user.isActive(),
                user.getCreatedAt(),
                user.getUpdatedAt());
    }
}
