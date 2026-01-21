package com.kartoffan.labinventory.service.user;

import java.util.List;
import java.util.UUID;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.kartoffan.labinventory.dto.user.UpdateUserRequest;
import com.kartoffan.labinventory.exception.ResourceNotFoundException;
import com.kartoffan.labinventory.model.User;
import com.kartoffan.labinventory.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
  private final UserRepository userRepository;
  
  /**
   * Fetches a user by its unique ID
   */
  @Override
  public User getById(UUID userId) {
    return userRepository.findByIdAndIsActiveTrue(userId)
        .orElseThrow(() -> new ResourceNotFoundException("No active user found with id " + userId));
  }

  /**
   * Returns the list of all users
   */
  @Override
  public List<User> getAll() {
    return userRepository.findAll();
  }

  /**
   * Updates an existing user's details
   */
  @Override
  public User update(UUID userId, UpdateUserRequest request) {
    User user = getById(userId);

    if (request.getFirstName() != null) {
      user.setFirstName(request.getFirstName());
    }
    if (request.getLastName() != null) {
      user.setLastName(request.getLastName());
    }
    if (request.getPhone() != null) {
      user.setPhone(request.getPhone());
    }
    if (request.getRole() != null) {
      user.setRole(request.getRole());
    }

    return userRepository.save(user);
  }

  /**
   * Deactivates a user
   */
  @Override
  public void deactivate(UUID userId) {
    User user = getById(userId);
    user.setIsActive(false);
    userRepository.save(user);
  }

  /**
   * Recovers a deactivated user
   */
  @Override
  public void recover(UUID userId) {
    User user = getById(userId);
    user.setIsActive(true);
    userRepository.save(user);
  }

  /**
   * Fetches the details of current logged-in user
   */
  @Override
  public User getCurrentUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null || !authentication.isAuthenticated()) {
      throw new AccessDeniedException("No authenticated user found");
    }

    String email = ((UserDetails) authentication.getPrincipal()).getUsername();

    return userRepository.findByEmailAndIsActiveTrue(email)
        .orElseThrow(() -> new ResourceNotFoundException("User not found"));
  }
}
