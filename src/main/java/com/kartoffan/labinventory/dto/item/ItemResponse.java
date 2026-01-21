package com.kartoffan.labinventory.dto.item;

import java.time.LocalDateTime;
import java.util.UUID;

import com.kartoffan.labinventory.model.Item;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemResponse {
  private UUID id;
  private String name;
  private String code;
  private String unit;
  private Integer reorderLevel;
  private Integer minQuantity;
  private Integer maxQuantity;
  private String location;
  private Boolean isActive;

  private UUID labId;
  private UUID categoryId;

  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public static ItemResponse fromEntity(Item item) {
    return ItemResponse.builder()
        .id(item.getId())
        .name(item.getName())
        .code(item.getCode())
        .unit(item.getUnit())
        .minQuantity(item.getMinQuantity())
        .maxQuantity(item.getMaxQuantity())
        .location(item.getLocation())
        .isActive(item.getIsActive())
        .labId(item.getLab() != null ? item.getLab().getId() : null)
        .categoryId(item.getCategory() != null ? item.getCategory().getId() : null)
        .createdAt(item.getCreatedAt())
        .updatedAt(item.getUpdatedAt())
        .build();
  }
}
