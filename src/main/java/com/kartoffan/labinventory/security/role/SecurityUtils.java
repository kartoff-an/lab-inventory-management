package com.kartoffan.labinventory.security.role;

import java.util.HashSet;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class SecurityUtils {
  
  public static Set<GrantedAuthority> toGrantedAuthorities(Role role) {
    Set<GrantedAuthority> authorities = new HashSet<>();

    authorities.add(new SimpleGrantedAuthority("ROLE_" + role.name()));

    role.getAuthorities().forEach(auth -> authorities.add(new SimpleGrantedAuthority(auth.name())));

    return authorities;
  }
}
