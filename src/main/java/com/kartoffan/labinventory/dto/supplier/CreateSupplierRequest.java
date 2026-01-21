package com.kartoffan.labinventory.dto.supplier;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateSupplierRequest {
  @NotBlank
  @Size(max = 200)
  private String name;

  private String contactName;
  private String email;
  private String phone;
  private String address;
  private String website;
  private String notes;
}
