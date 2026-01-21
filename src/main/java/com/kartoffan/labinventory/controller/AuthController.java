package com.kartoffan.labinventory.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.kartoffan.labinventory.dto.auth.*;
import com.kartoffan.labinventory.service.auth.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Authentication", description = "Endpoints for user authentication and account registration")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
  
  private final AuthService authService;

  @Operation(summary = "User login")
  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody LoginRequest request) {
    AuthService.AuthResult result = authService.login(request);

    return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, result.cookie().toString())
        .body(null);
  }

  @Operation(summary = "User registration")
  @PostMapping("/signup")
  @PreAuthorize("hasAuthority('USER_WRITE')")
  public ResponseEntity<AuthResponse> signup(@Valid @RequestBody SignupRequest request) {
    AuthService.AuthResult result = authService.signup(request);

    return ResponseEntity
        .status(HttpStatus.CREATED)
        .header(HttpHeaders.SET_COOKIE, result.cookie().toString())
        .body(result.response());
  }
}
