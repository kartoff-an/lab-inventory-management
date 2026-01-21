package com.kartoffan.labinventory.security;

import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import com.kartoffan.labinventory.model.User;
import com.kartoffan.labinventory.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    User user = userRepository.findByEmailAndIsActiveTrue(email)
        .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

    return new UserDetailsImpl(user);
  }
}
