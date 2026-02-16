package com.smartappointment.service.interfaces;

import com.smartappointment.dto.user.requests.ChangePasswordRequest;
import com.smartappointment.dto.user.requests.UpdateUserRequest;
import com.smartappointment.dto.user.responses.UserResponse;

import java.util.List;

public interface IUserService {

    List<UserResponse> getAllUsers();

    List<UserResponse> getActiveUsers();

    List<UserResponse> getInactiveUsers();

    List<UserResponse> getUsersByRole(String role);

    List<UserResponse> getActiveUsersByRole(String role);

    List<UserResponse> searchUsersByName(String keyword);

    UserResponse getUserById(Long id);

    UserResponse getUserByEmail(String email);

    UserResponse updateUser(Long id, UpdateUserRequest request);

    void changePassword(Long id, ChangePasswordRequest request);

    void softDeleteUser(Long id);

    void hardDeleteUser(Long id);

    void deactivateUser(Long id);

    void activateUser(Long id);

    long countActiveUsers();

    long countInactiveUsers();

    long countUsersByRole(String role);
}
