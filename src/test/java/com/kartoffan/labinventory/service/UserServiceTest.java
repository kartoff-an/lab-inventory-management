package com.kartoffan.labinventory.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import com.kartoffan.labinventory.dto.user.UpdateUserRequest;
import com.kartoffan.labinventory.exception.ResourceNotFoundException;
import com.kartoffan.labinventory.model.User;
import com.kartoffan.labinventory.repository.UserRepository;
import com.kartoffan.labinventory.service.user.UserServiceImpl;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private UserServiceImpl userService;

  private UUID userId;
  private User user;

  @BeforeEach
  void setUp() {
    userId = UUID.randomUUID();
    user = User.builder()
        .id(userId)
        .email("user@test.com")
        .isActive(true)
        .build();
  }

  @AfterEach
  void clearSecurityContext() {
    SecurityContextHolder.clearContext();
  }

  @Test
  void getById_success() {
    when(userRepository.findByIdAndIsActiveTrue(userId))
        .thenReturn(Optional.of(user));

    User result = userService.getById(userId);

    assertEquals(userId, result.getId());
  }

  @Test
  void getById_notFound_throwsException() {
    when(userRepository.findByIdAndIsActiveTrue(userId))
        .thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class,
        () -> userService.getById(userId));
  }

  @Test
  void getAll_success() {
    when(userRepository.findAll())
        .thenReturn(List.of(user));

    List<User> result = userService.getAll();

    assertEquals(1, result.size());
  }

  @Test
  void updateUser_success() {
    UpdateUserRequest request = new UpdateUserRequest();
    request.setFirstName("John");
    request.setLastName("Doe");

    when(userRepository.findByIdAndIsActiveTrue(userId))
        .thenReturn(Optional.of(user));
    when(userRepository.save(any(User.class)))
        .thenAnswer(i -> i.getArgument(0));

    User result = userService.update(userId, request);

    assertEquals("John", result.getFirstName());
    assertEquals("Doe", result.getLastName());
  }

  @Test
  void updateUser_notFound_throwsException() {
    when(userRepository.findByIdAndIsActiveTrue(userId))
        .thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class,
        () -> userService.update(userId, new UpdateUserRequest()));
  }

  @Test
  void deactivateUser_success() {
    when(userRepository.findByIdAndIsActiveTrue(userId))
        .thenReturn(Optional.of(user));

    userService.deactivate(userId);

    assertFalse(user.getIsActive());
    verify(userRepository).save(user);
  }

  @Test
  void recoverUser_success() {
    when(userRepository.findByIdAndIsActiveTrue(userId))
        .thenReturn(Optional.of(user));

    userService.recover(userId);

    assertTrue(user.getIsActive());
    verify(userRepository).save(user);
  }

  @Test
  void getCurrentUser_success() {
    UserDetails userDetails = org.springframework.security.core.userdetails.User
        .withUsername("user@test.com")
        .password("password")
        .roles("USER")
        .build();

    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
        userDetails,
        null,
        userDetails.getAuthorities());

    SecurityContextHolder.getContext().setAuthentication(auth);

    when(userRepository.findByEmailAndIsActiveTrue("user@test.com"))
        .thenReturn(Optional.of(user));

    User result = userService.getCurrentUser();

    assertEquals("user@test.com", result.getEmail());
  }

  @Test
  void getCurrentUser_noAuthentication_throwsException() {
    SecurityContextHolder.clearContext();

    assertThrows(AccessDeniedException.class,
        () -> userService.getCurrentUser());
  }

  @Test
  void getCurrentUser_userNotFound_throwsException() {
    UserDetails userDetails = org.springframework.security.core.userdetails.User
        .withUsername("missing@test.com")
        .password("password")
        .roles("USER")
        .build();

    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
        userDetails,
        null,
        userDetails.getAuthorities());

    SecurityContextHolder.getContext().setAuthentication(auth);

    when(userRepository.findByEmailAndIsActiveTrue("missing@test.com"))
        .thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class,
        () -> userService.getCurrentUser());
  }
}
