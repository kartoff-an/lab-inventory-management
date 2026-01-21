package com.kartoffan.labinventory.dto.user;

import java.time.LocalDateTime;
import java.util.UUID;

import com.kartoffan.labinventory.model.User;
import com.kartoffan.labinventory.security.role.Role;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {
  
  private UUID id;
  private String firstName;
  private String lastName;
  private String email;
  private Role role;
  private String phone;
  private LocalDateTime createdAt;

  public static UserResponse fromEntity(User user) {
    return UserResponse.builder()
        .id(user.getId())
        .firstName(user.getFirstName())
        .lastName(user.getLastName())
        .email(user.getEmail())
        .role(user.getRole())
        .phone(user.getPhone())
        .createdAt(user.getCreatedAt())
        .build();
  }
}
