package com.kartoffan.labinventory.dto.supplier;

import lombok.Data;

@Data
public class UpdateSupplierRequest {
  private String contactName;
  private String email;
  private String phone;
  private String address;
  private String website;
  private String notes;
}
