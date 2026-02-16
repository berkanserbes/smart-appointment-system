package com.smartappointment.service.interfaces;

import com.smartappointment.dto.user.UserResponse;
import com.smartappointment.dto.user.UserUpdateRequest;

import java.util.List;

public interface IUserService {

    List<UserResponse> getAllUsers();

    List<UserResponse> getActiveUsers();

    List<UserResponse> getInactiveUsers();

    List<UserResponse> getUsersByRole(String role);

    UserResponse getUserById(Long id);

    UserResponse getUserByEmail(String email);

    UserResponse updateUser(Long id, UserUpdateRequest request);

    void changePassword(Long id, String currentPassword, String newPassword);

    void softDeleteUser(Long id);

    void hardDeleteUser(Long id);

    void deactivateUser(Long id);

    void activateUser(Long id);
}
