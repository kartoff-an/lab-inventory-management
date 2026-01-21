package com.kartoffan.labinventory.config;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.kartoffan.labinventory.model.User;
import com.kartoffan.labinventory.repository.UserRepository;
import com.kartoffan.labinventory.security.role.Role;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SuperAdminInitializer {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @PostConstruct
  public void init() {
    if (!userRepository.existsByRole(Role.SUPER_ADMIN)) {
      User superAdmin = User.builder()
          .email("superadmin@system.com")
          .passwordHash(passwordEncoder.encode("ChangeMe123!"))
          .role(Role.SUPER_ADMIN)
          .isActive(true)
          .build();

      userRepository.save(superAdmin);
    }
  }
}
