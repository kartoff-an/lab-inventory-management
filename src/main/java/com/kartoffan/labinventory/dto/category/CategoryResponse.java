package com.kartoffan.labinventory.dto.category;

import java.time.LocalDateTime;
import java.util.UUID;

import com.kartoffan.labinventory.model.Category;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoryResponse {
  private UUID id;
  private String name;
  private String description;
  private UUID labId;
  private UUID parentCategoryId;
  private Boolean isActive;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public static CategoryResponse fromEntity(Category category) {
    return CategoryResponse.builder()
        .id(category.getId())
        .name(category.getName())
        .description(category.getDescription())
        .labId(category.getLab().getId())
        .parentCategoryId(
            category.getParentCategory() != null
            ? category.getParentCategory().getId()
            : null
        )
        .isActive(category.getIsActive())
        .createdAt(category.getCreatedAt())
        .updatedAt(category.getUpdatedAt())
        .build();
  }
}
