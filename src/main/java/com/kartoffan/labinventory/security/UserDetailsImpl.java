package com.kartoffan.labinventory.security;

import java.util.Collection;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.kartoffan.labinventory.model.User;
import com.kartoffan.labinventory.security.role.SecurityUtils;

import lombok.Getter;

@Getter
public class UserDetailsImpl implements UserDetails {

  private final String id;
  private final String username;
  private final String password;
  private final boolean enabled;
  private final Set<GrantedAuthority> authorities;

  public UserDetailsImpl(User user) {
    this.id = user.getId().toString();
    this.username = user.getEmail();
    this.password = user.getPasswordHash();
    this.enabled = user.getIsActive();
    this.authorities = SecurityUtils.toGrantedAuthorities(user.getRole());
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return username;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return enabled;
  }
}
