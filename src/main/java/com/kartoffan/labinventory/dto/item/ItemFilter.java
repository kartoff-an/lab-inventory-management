package com.kartoffan.labinventory.dto.item;

import java.util.UUID;

import lombok.Data;

@Data
public class ItemFilter {
  
  private Boolean active;
  private UUID labId;
  private UUID categoryId;
  private String search;
}
