package com.kartoffan.labinventory.service;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.kartoffan.labinventory.dto.auth.*;
import com.kartoffan.labinventory.exception.*;
import com.kartoffan.labinventory.model.User;
import com.kartoffan.labinventory.repository.UserRepository;
import com.kartoffan.labinventory.security.jwt.JwtUtil;
import com.kartoffan.labinventory.security.role.Role;
import com.kartoffan.labinventory.service.auth.AuthService;
import com.kartoffan.labinventory.service.auth.AuthService.AuthResult;
import com.kartoffan.labinventory.service.auth.AuthServiceImpl;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
  
  @Mock
  private UserRepository userRepository;

  @Mock
  private BCryptPasswordEncoder passwordEncoder;

  @Mock
  private JwtUtil jwtUtil;

  @InjectMocks
  private AuthServiceImpl authService;

  private User testUser;
  private LoginRequest loginRequest;
  private SignupRequest signupRequest;

  @BeforeEach
  void setUp() {
    testUser = User.builder()
        .id(UUID.randomUUID())
        .firstName("John")
        .lastName("Doe")
        .email("john.doe@test.com")
        .passwordHash("encodedPassword")
        .role(Role.SUPER_ADMIN)
        .isActive(true)
        .build();

    loginRequest = new LoginRequest();
    loginRequest.setEmail("john.doe@test.com");
    loginRequest.setPassword("password123");

    signupRequest = new SignupRequest();
    signupRequest.setFirstName("Jane");
    signupRequest.setLastName("Smith");
    signupRequest.setEmail("jane.smith@test.com");
    signupRequest.setPassword("password123");
    signupRequest.setRole("LAB_ADMIN");
  }
  
  @Test
  void login_WithValidCredentials_ShouldReturnAuthResult() {
    String token = "jwt-token-123";
    when(userRepository.findByEmailAndIsActiveTrue(loginRequest.getEmail()))
        .thenReturn(Optional.of(testUser));
    when(passwordEncoder.matches(loginRequest.getPassword(), testUser.getPasswordHash()))
        .thenReturn(true);
    when(jwtUtil.generateToken(testUser.getEmail()))
        .thenReturn(token);

    AuthService.AuthResult result = authService.login(loginRequest);

    assertNotNull(result);
    assertNotNull(result.cookie());
    // assertNotNull(result.response());

    ResponseCookie cookie = result.cookie();
    assertEquals("ACCESS_TOKEN", cookie.getName());
    assertEquals(token, cookie.getValue());
    assertTrue(cookie.isHttpOnly());
    assertTrue(cookie.isSecure());
    assertEquals("Strict", cookie.getSameSite());
    assertEquals("/", cookie.getPath());

    verify(userRepository).findByEmailAndIsActiveTrue(loginRequest.getEmail());
    verify(passwordEncoder).matches(loginRequest.getPassword(), testUser.getPasswordHash());
    verify(jwtUtil).generateToken(testUser.getEmail());
  }

  @Test
  void login_WithNonExistentEmail_ShouldThrowInvalidCredentialsException() {
    when(userRepository.findByEmailAndIsActiveTrue(loginRequest.getEmail()))
        .thenReturn(Optional.empty());

    InvalidCredentialsException exception = assertThrows(
      InvalidCredentialsException.class,
      () -> authService.login(loginRequest)
    );

    assertEquals("Invalid credentials", exception.getMessage());
    verify(userRepository, never()).save(any());
    verify(jwtUtil, never()).generateToken(anyString());
  }

  @Test
  void login_WithWrongPassword_ShouldThrowInvalidCredentialsException() {
    when(userRepository.findByEmailAndIsActiveTrue(loginRequest.getEmail()))
        .thenReturn(Optional.of(testUser));
    when(passwordEncoder.matches(loginRequest.getPassword(), testUser.getPasswordHash()))
        .thenReturn(false);

    InvalidCredentialsException exception = assertThrows(
        InvalidCredentialsException.class,
        () -> authService.login(loginRequest));

    assertEquals("Invalid credentials", exception.getMessage());
    verify(userRepository, never()).save(any());
    verify(jwtUtil, never()).generateToken(anyString());
  }
  
  @Test
  void login_WithInactiveUser_ShouldThrowInvalidCredentialsException() {
    testUser.setIsActive(false);
    when(userRepository.findByEmailAndIsActiveTrue(loginRequest.getEmail()))
        .thenReturn(Optional.empty());

    InvalidCredentialsException exception = assertThrows(
        InvalidCredentialsException.class,
        () -> authService.login(loginRequest));

    assertEquals("Invalid credentials", exception.getMessage());
    verify(userRepository, never()).save(any());
  }

  @Test
  void signup_WithValidRequest_ShouldCreateUserAndReturnAuthResult() {
    String token = "jwt-token-123";
    when(userRepository.existsByEmail(signupRequest.getEmail())).thenReturn(false);
    when(passwordEncoder.encode(signupRequest.getPassword())).thenReturn("encodedPassword");
    when(jwtUtil.generateToken(signupRequest.getEmail())).thenReturn(token);
    when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

    AuthResult result = authService.signup(signupRequest);

    assertNotNull(result);
    assertNotNull(result.cookie());
    assertNotNull(result.response());
    
    ResponseCookie cookie = result.cookie();
    assertEquals("ACCESS_TOKEN", cookie.getName());
    assertEquals(token, cookie.getValue());
    
    AuthResponse response = result.response();
    assertEquals(token, response.getToken());
    assertEquals(signupRequest.getEmail(), response.getEmail());
    assertEquals("LAB_ADMIN", response.getRole());
    
    verify(userRepository, times(2)).save(any(User.class));
    verify(passwordEncoder).encode(signupRequest.getPassword());
    verify(jwtUtil).generateToken(signupRequest.getEmail());
  }

   @Test
    void signup_WithExistingEmail_ShouldThrowResourceAlreadyExistsException() {
      when(userRepository.existsByEmail(signupRequest.getEmail())).thenReturn(true);

      ResourceAlreadyExistsException exception = assertThrows(
        ResourceAlreadyExistsException.class,
        () -> authService.signup(signupRequest)
      );
      
      assertEquals("Email is already registered", exception.getMessage());
      verify(userRepository, never()).save(any(User.class));
      verify(jwtUtil, never()).generateToken(anyString());
    }

    @Test
    void signup_WithInvalidRole_ShouldThrowBadRequestException() {
      signupRequest.setRole("INVALID_ROLE");
      when(userRepository.existsByEmail(signupRequest.getEmail())).thenReturn(false);

      BadRequestException exception = assertThrows(
        BadRequestException.class,
        () -> authService.signup(signupRequest)
      );
      
      assertEquals("Invalid user role", exception.getMessage());
      verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void signup_WithEmptyRole_ShouldThrowBadRequestException() {
    signupRequest.setRole("");
    when(userRepository.existsByEmail(signupRequest.getEmail())).thenReturn(false);

    BadRequestException exception = assertThrows(
      BadRequestException.class,
      () -> authService.signup(signupRequest));

    assertEquals("Invalid user role", exception.getMessage());
    }
}
