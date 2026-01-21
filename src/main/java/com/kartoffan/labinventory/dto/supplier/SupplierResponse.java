package com.kartoffan.labinventory.dto.supplier;

import java.time.LocalDateTime;
import java.util.UUID;

import com.kartoffan.labinventory.model.Supplier;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SupplierResponse {
  private UUID id;
  private String name;
  private String contactName;
  private String email;
  private String phone;
  private String address;
  private String website;
  private String notes;
  private Boolean isActive;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public static SupplierResponse fromEntity(Supplier supplier) {
    return SupplierResponse.builder()
        .id(supplier.getId())
        .name(supplier.getName())
        .contactName(supplier.getContactName())
        .email(supplier.getEmail())
        .phone(supplier.getPhone())
        .address(supplier.getAddress())
        .website(supplier.getWebsite())
        .notes(supplier.getNotes())
        .isActive(supplier.getIsActive())
        .createdAt(supplier.getCreatedAt())
        .updatedAt(supplier.getUpdatedAt())
        .build();
  }
}
