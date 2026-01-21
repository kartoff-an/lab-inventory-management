package com.kartoffan.labinventory.dto.item;

import java.util.UUID;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateItemRequest {
  
  @NotBlank
  @Size(max = 150)
  private String name;

  @Size(max = 50)
  private String code;

  @NotNull
  private UUID labId;

  @NotNull
  private UUID categoryId;

  @NotNull
  private String unit;

  @NotNull
  @Min(0)
  private Integer reorderLevel;

  @NotNull
  @Min(0)
  private Integer maxQuantity;

  @Size(max = 100)
  private String location;

  @Size(max = 255)
  private String storageCondition;
}
