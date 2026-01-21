package com.kartoffan.labinventory.dto.category;

import java.util.UUID;

import lombok.Data;

@Data
public class CreateCategoryRequest {
  private String name;
  private String description;
  private UUID labId;
  private UUID parentCategoryId;
}
