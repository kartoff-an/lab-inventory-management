package com.kartoffan.labinventory.dto.stockMovement;

import java.time.LocalDateTime;
import java.util.UUID;

import com.kartoffan.labinventory.model.StockMovement;
import com.kartoffan.labinventory.model.StockMovement.MovementType;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StockMovementResponse {
  
  private UUID id;
  private UUID itemId;
  private UUID userId;
  private MovementType type;
  private double quantity;
  private String reason;
  private LocalDateTime timestamp;

  public static StockMovementResponse fromEntity(StockMovement movement) {
    return StockMovementResponse.builder()
        .id(movement.getId())
        .itemId(movement.getItem().getId())
        .userId(movement.getPerformedBy().getId())
        .type(movement.getType())
        .quantity(movement.getQuantity())
        .reason(movement.getReason())
        .timestamp(movement.getTimestamp())
        .build();
  }
}
