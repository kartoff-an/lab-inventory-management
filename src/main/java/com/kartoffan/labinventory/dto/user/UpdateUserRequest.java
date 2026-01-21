package com.kartoffan.labinventory.dto.user;

import com.kartoffan.labinventory.security.role.Role;

import lombok.Data;

@Data
public class UpdateUserRequest {
  
  private String firstName;
  private String lastName;
  private String phone;
  private Role role;

}
