package com.kartoffan.labinventory.dto.lab;

import java.util.UUID;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LabRequest {
  @NotBlank
  private String name;

  private String location;
  private String description;

  @Email
  private String contactEmail;

  private String phone;
  private UUID managerId;
}
