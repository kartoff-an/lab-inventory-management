package com.kartoffan.labinventory.dto.stock;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class StockQuantity {
  private UUID itemId;
  private String itemName;
  private Double quantity;
}
