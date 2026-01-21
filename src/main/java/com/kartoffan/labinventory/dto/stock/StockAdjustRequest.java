package com.kartoffan.labinventory.dto.stock;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StockAdjustRequest {
  
  @NotNull
  private UUID itemId;

  @NotNull
  private UUID labId;

  @NotNull
  private Double adjustment;

  @Size(max = 255)
  private String reason;

  @NotNull
  private UUID performedBy;
}
