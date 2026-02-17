package com.smartappointment.controller;

import com.smartappointment.config.swagger.ApiResponseAnnotations.DeleteResponses;
import com.smartappointment.config.swagger.ApiResponseAnnotations.GetResponses;
import com.smartappointment.config.swagger.ApiResponseAnnotations.StandardResponses;
import com.smartappointment.config.swagger.ApiResponseAnnotations.UpdateResponses;
import com.smartappointment.dto.user.requests.ChangePasswordRequest;
import com.smartappointment.dto.user.requests.UpdateUserRequest;
import com.smartappointment.dto.user.responses.UserResponse;
import com.smartappointment.service.impl.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "User management and profile endpoints")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all users", description = "Retrieves all users including inactive ones (Admin only)")
    @StandardResponses
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/active")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get active users", description = "Retrieves only active users (Admin only)")
    @StandardResponses
    public ResponseEntity<List<UserResponse>> getActiveUsers() {
        return ResponseEntity.ok(userService.getActiveUsers());
    }

    @GetMapping("/inactive")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get inactive users", description = "Retrieves only inactive users (Admin only)")
    @StandardResponses
    public ResponseEntity<List<UserResponse>> getInactiveUsers() {
        return ResponseEntity.ok(userService.getInactiveUsers());
    }

    @GetMapping("/role/{role}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get users by role", description = "Retrieves all users with a specific role (Admin only)")
    @StandardResponses
    public ResponseEntity<List<UserResponse>> getUsersByRole(@PathVariable String role) {
        return ResponseEntity.ok(userService.getUsersByRole(role));
    }

    @GetMapping("/role/{role}/active")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get active users by role", description = "Retrieves only active users with a specific role (Admin only)")
    @StandardResponses
    public ResponseEntity<List<UserResponse>> getActiveUsersByRole(@PathVariable String role) {
        return ResponseEntity.ok(userService.getActiveUsersByRole(role));
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Search users by name", description = "Searches users by name keyword (case-insensitive, Admin only)")
    @StandardResponses
    public ResponseEntity<List<UserResponse>> searchUsersByName(@RequestParam String keyword) {
        return ResponseEntity.ok(userService.searchUsersByName(keyword));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", description = "Retrieves a specific user by their ID")
    @GetResponses
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping("/me")
    @Operation(summary = "Get current user profile", description = "Retrieves the profile of the currently authenticated user")
    @StandardResponses
    public ResponseEntity<UserResponse> getCurrentUser(Authentication authentication) {
        return ResponseEntity.ok(userService.getUserByEmail(authentication.getName()));
    }

    @GetMapping("/count/active")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Count active users", description = "Returns the total number of active users (Admin only)")
    @StandardResponses
    public ResponseEntity<Map<String, Long>> countActiveUsers() {
        return ResponseEntity.ok(Map.of("count", userService.countActiveUsers()));
    }

    @GetMapping("/count/inactive")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Count inactive users", description = "Returns the total number of inactive users (Admin only)")
    @StandardResponses
    public ResponseEntity<Map<String, Long>> countInactiveUsers() {
        return ResponseEntity.ok(Map.of("count", userService.countInactiveUsers()));
    }

    @GetMapping("/count/role/{role}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Count users by role", description = "Returns the total number of users with a specific role (Admin only)")
    @StandardResponses
    public ResponseEntity<Map<String, Long>> countUsersByRole(@PathVariable String role) {
        return ResponseEntity.ok(Map.of("count", userService.countUsersByRole(role)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update user profile", description = "Updates user profile information")
    @UpdateResponses
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(userService.updateUser(id, request));
    }

    @PutMapping("/{id}/password")
    @Operation(summary = "Change password", description = "Changes the user's password")
    @UpdateResponses
    public ResponseEntity<Void> changePassword(@PathVariable Long id,
            @Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(id, request);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Activate user", description = "Activates an inactive user account (Admin only)")
    @DeleteResponses
    public ResponseEntity<Void> activateUser(@PathVariable Long id) {
        userService.activateUser(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Deactivate user", description = "Deactivates an active user account (Admin only)")
    @DeleteResponses
    public ResponseEntity<Void> deactivateUser(@PathVariable Long id) {
        userService.deactivateUser(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete user", description = "Permanently removes user from database (Admin only)")
    @DeleteResponses
    public ResponseEntity<Void> hardDeleteUser(@PathVariable Long id) {
        userService.hardDeleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
