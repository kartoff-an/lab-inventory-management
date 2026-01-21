package com.kartoffan.labinventory.dto.stock;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StockTransferRequest {
  
  @NotNull
  private UUID itemId;

  @NotNull
  private UUID fromLabId;

  @NotNull
  private UUID toLabId;

  @NotNull
  @Min(0)
  private Double quantity;

  private String batchNumber;
  private LocalDate expirationDate;
  
  @Size(max = 255)
  private String reason;

  @NotNull
  private UUID performedBy;
}
