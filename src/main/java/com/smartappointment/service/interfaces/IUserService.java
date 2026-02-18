package com.smartappointment.service.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.smartappointment.dto.user.requests.ChangePasswordRequest;
import com.smartappointment.dto.user.requests.UpdateUserRequest;
import com.smartappointment.dto.user.responses.UserResponse;

public interface IUserService {

    Page<UserResponse> getAllUsers(Pageable pageable);

    Page<UserResponse> getActiveUsers(Pageable pageable);

    Page<UserResponse> getInactiveUsers(Pageable pageable);

    Page<UserResponse> getUsersByRole(String role, Pageable pageable);

    Page<UserResponse> getActiveUsersByRole(String role, Pageable pageable);

    Page<UserResponse> searchUsersByName(String keyword, Pageable pageable);

    UserResponse getUserById(Long id);

    UserResponse getUserByEmail(String email);

    UserResponse updateUser(Long id, UpdateUserRequest request);

    void changePassword(Long id, ChangePasswordRequest request);

    void hardDeleteUser(Long id);

    void deactivateUser(Long id);

    void activateUser(Long id);

    long countActiveUsers();

    long countInactiveUsers();

    long countUsersByRole(String role);
}
