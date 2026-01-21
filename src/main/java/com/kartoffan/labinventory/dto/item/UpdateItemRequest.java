package com.kartoffan.labinventory.dto.item;

import java.util.UUID;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class UpdateItemRequest {

  private String name;
  private String code;
  private UUID categoryId;
  private String unit;

  @Min(0)
  private Integer quantity;

  @Min(0)
  private Integer reorderLevel;

  @Min(0)
  private Integer minQuantity;

  @Min(0)
  private Integer maxQuantity;

  private String location;

  private String storageCondition;
}