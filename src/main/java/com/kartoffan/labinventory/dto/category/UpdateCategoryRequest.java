package com.kartoffan.labinventory.dto.category;

import java.util.UUID;

import lombok.Data;

@Data
public class UpdateCategoryRequest {
  private String name;
  private String description;
  private UUID parentCategoryId;
}
