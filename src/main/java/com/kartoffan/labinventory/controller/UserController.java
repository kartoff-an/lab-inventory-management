package com.kartoffan.labinventory.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kartoffan.labinventory.dto.user.UpdateUserRequest;
import com.kartoffan.labinventory.dto.user.UserResponse;
import com.kartoffan.labinventory.model.User;
import com.kartoffan.labinventory.service.user.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Users", description = "Endpoints for managing users, including account retrieval, updates, and activation/deactivation")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;
  
  @Operation(summary = "Get user by ID")
  @GetMapping("/{userId}")
  @PreAuthorize("hasAnyRole('ROLE_SUPER_ADMIN')")
  public ResponseEntity<UserResponse> getById(@PathVariable UUID userId) {
    User user = userService.getById(userId);
    return ResponseEntity.ok()
        .body(UserResponse.fromEntity(user));
  }

  @Operation(summary = "Get all users")
  @GetMapping
  @PreAuthorize("hasAnyRole('ROLE_SUPER_ADMIN')")
  public ResponseEntity<List<UserResponse>> getAll() {
    List<User> users = userService.getAll();
    return ResponseEntity.ok()
        .body(users.stream().map(user -> UserResponse.fromEntity(user)).toList());
  }

  @Operation(summary = "Get current user")
  @GetMapping("/me")
  public ResponseEntity<UserResponse> getCurrentUser() {
    User user = userService.getCurrentUser();
    return ResponseEntity.ok()
        .body(UserResponse.fromEntity(user));
  }

  @Operation(summary = "Update a user")
  @PutMapping("/{userId}")
  @PreAuthorize("hasAnyRole('ROLE_SUPER_ADMIN')")
  public ResponseEntity<UserResponse> update(@PathVariable UUID userId, @RequestBody UpdateUserRequest request) {
    User user = userService.update(userId, request);
    return ResponseEntity.ok()
        .body(UserResponse.fromEntity(user));
  }

  @Operation(summary = "Deactivate a user")
  @PatchMapping("/{userId}/deactivate")
  @PreAuthorize("hasAnyRole('ROLE_SUPER_ADMIN')")
  public ResponseEntity<?> deactivate(@PathVariable UUID userId) {
    userService.deactivate(userId);
    return ResponseEntity.ok().build();
  }

  @Operation(summary = "Recover user")
  @PatchMapping("/{userId}/recover")
  @PreAuthorize("hasAnyRole('ROLE_SUPER_ADMIN')")
  public ResponseEntity<?> recover(@PathVariable UUID userId) {
    userService.recover(userId);
    return ResponseEntity.ok().build();
  }
}
