package com.smartappointment.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smartappointment.dto.user.requests.ChangePasswordRequest;
import com.smartappointment.dto.user.requests.UpdateUserRequest;
import com.smartappointment.dto.user.responses.UserResponse;
import com.smartappointment.exception.BadRequestException;
import com.smartappointment.exception.ResourceNotFoundException;
import com.smartappointment.mapper.UserMapper;
import com.smartappointment.model.entity.User;
import com.smartappointment.model.enums.Role;
import com.smartappointment.repository.UserRepository;
import com.smartappointment.service.interfaces.IUserService;

@Service
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    // ==================== GET OPERATIONS ====================

    @Override
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(userMapper::toResponse);
    }

    @Override
    public Page<UserResponse> getActiveUsers(Pageable pageable) {
        return userRepository.findByActiveTrue(pageable).map(userMapper::toResponse);
    }

    @Override
    public Page<UserResponse> getInactiveUsers(Pageable pageable) {
        return userRepository.findByActiveFalse(pageable).map(userMapper::toResponse);
    }

    @Override
    public Page<UserResponse> getUsersByRole(String role, Pageable pageable) {
        Role roleEnum = parseRole(role);
        return userRepository.findByRole(roleEnum, pageable).map(userMapper::toResponse);
    }

    @Override
    public Page<UserResponse> getActiveUsersByRole(String role, Pageable pageable) {
        Role roleEnum = parseRole(role);
        return userRepository.findByRoleAndActiveTrue(roleEnum, pageable).map(userMapper::toResponse);
    }

    @Override
    public Page<UserResponse> searchUsersByName(String keyword, Pageable pageable) {
        return userRepository
                .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(keyword, keyword, pageable)
                .map(userMapper::toResponse);
    }

    @Override
    public UserResponse getUserById(Long id) {
        User user = findUserOrThrow(id);
        return userMapper.toResponse(user);
    }

    @Override
    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        return userMapper.toResponse(user);
    }

    // ==================== UPDATE OPERATIONS ====================

    @Override
    @Transactional
    public UserResponse updateUser(Long id, UpdateUserRequest request) {
        User user = findUserOrThrow(id);

        if (request.firstName() != null) {
            user.setFirstName(request.firstName());
        }
        if (request.lastName() != null) {
            user.setLastName(request.lastName());
        }
        if (request.phone() != null) {
            user.setPhone(request.phone());
        }

        User updated = userRepository.save(user);
        return userMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public void changePassword(Long id, ChangePasswordRequest request) {
        User user = findUserOrThrow(id);

        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw new BadRequestException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }

    // ==================== STATUS OPERATIONS ====================

    @Override
    @Transactional
    public void activateUser(Long id) {
        User user = findUserOrThrow(id);
        user.setActive(true);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void deactivateUser(Long id) {
        User user = findUserOrThrow(id);
        user.setActive(false);
        userRepository.save(user);
    }

    // ==================== DELETE OPERATIONS ====================

    @Override
    @Transactional
    public void hardDeleteUser(Long id) {
        User user = findUserOrThrow(id);
        userRepository.delete(user);
    }

    // ==================== COUNT OPERATIONS ====================

    @Override
    public long countActiveUsers() {
        return userRepository.countByActiveTrue();
    }

    @Override
    public long countInactiveUsers() {
        return userRepository.countByActiveFalse();
    }

    @Override
    public long countUsersByRole(String role) {
        Role roleEnum = parseRole(role);
        return userRepository.countByRole(roleEnum);
    }

    // ==================== HELPER METHODS ====================

    private User findUserOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    private Role parseRole(String role) {
        try {
            return Role.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid role: " + role);
        }
    }
}
