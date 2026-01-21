package com.kartoffan.labinventory.dto.stockMovement;

import java.time.LocalDateTime;
import java.util.UUID;

import com.kartoffan.labinventory.model.StockMovement.MovementType;

import lombok.Data;

@Data
public class StockMovementFilter {
  
  private UUID itemId;
  private UUID labId;
  private UUID supplierId;
  private MovementType type;

  private LocalDateTime from;
  private LocalDateTime to;
}
