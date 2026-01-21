package com.kartoffan.labinventory.service.auth;

import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.kartoffan.labinventory.dto.auth.*;
import com.kartoffan.labinventory.exception.InvalidCredentialsException;
import com.kartoffan.labinventory.exception.ResourceAlreadyExistsException;
import com.kartoffan.labinventory.exception.BadRequestException;
import com.kartoffan.labinventory.model.User;
import com.kartoffan.labinventory.repository.UserRepository;
import com.kartoffan.labinventory.security.jwt.JwtUtil;
import com.kartoffan.labinventory.security.role.Role;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
  
  private final String ACCESS_TOKEN_COOKIE = "ACCESS_TOKEN";
  
  private final UserRepository userRepository;
  private final BCryptPasswordEncoder passwordEncoder;
  private final JwtUtil jwtUtil;

  /**
   * Login user
   */
  public AuthResult login(LoginRequest loginRequest) {

    User user = userRepository.findByEmailAndIsActiveTrue(loginRequest.getEmail())
        .orElseThrow(() -> new InvalidCredentialsException("Invalid credentials"));

    if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPasswordHash())) {
      throw new InvalidCredentialsException("Invalid credentials");
    }

    String token = jwtUtil.generateToken(user.getEmail());
    ResponseCookie cookie = buildCookie(ACCESS_TOKEN_COOKIE, token, 15);

    user.setLastLogin(LocalDateTime.now());
    userRepository.save(user);

    return new AuthResult(cookie, null);
  }
  
  /**
   * Create a user
   */
  public AuthResult signup(SignupRequest request) {

    if (userRepository.existsByEmail(request.getEmail())) {
      throw new ResourceAlreadyExistsException("Email is already registered");
    }

    Role userRole = getEnumRole(request.getRole());

    User user = User.builder()
        .firstName(request.getFirstName())
        .lastName(request.getLastName())
        .email(request.getEmail())
        .passwordHash(passwordEncoder.encode(request.getPassword()))
        .role(userRole)
        .isActive(true)
        .build();

    userRepository.save(user);
    String token = jwtUtil.generateToken(user.getEmail());
    ResponseCookie cookie = buildCookie(ACCESS_TOKEN_COOKIE, token, 15);

    user.setLastLogin(LocalDateTime.now());
    userRepository.save(user);

    AuthResponse response = new AuthResponse(token, user.getEmail(), user.getRole().name());
    return new AuthResult(cookie, response);
  } 

  private ResponseCookie buildCookie(String cookieName, String value, int maxAgeMins) {
    return ResponseCookie.from(cookieName, value)
        .httpOnly(true)
        .secure(true)
        .sameSite("Strict")
        .path("/")
        .maxAge(Duration.ofMinutes(maxAgeMins))
        .build();
  }

  private Role getEnumRole(String role) {
    try {
      return Role.valueOf(role.toUpperCase());
    } catch (IllegalArgumentException exception) {
      throw new BadRequestException("Invalid user role");
    }
  }
}
