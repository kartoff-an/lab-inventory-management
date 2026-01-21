package com.kartoffan.labinventory.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kartoffan.labinventory.model.User;
import com.kartoffan.labinventory.security.role.Role;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
  
  Optional<User> findByIdAndIsActiveTrue(UUID userId);

  Optional<User> findByEmailAndIsActiveTrue(String email);

  Optional<User> findByIdAndRole(UUID userId, Role role);

  boolean existsByEmail(String email);

  boolean existsByRole(Role role);
}
