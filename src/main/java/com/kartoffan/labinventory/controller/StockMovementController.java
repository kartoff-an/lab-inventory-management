package com.kartoffan.labinventory.controller;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.kartoffan.labinventory.dto.stockMovement.*;
import com.kartoffan.labinventory.model.StockMovement;
import com.kartoffan.labinventory.service.stockMovement.StockMovementService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Stock Movements", description = "Endpoints for tracking stock movement history and managing inventory transactions")
@RestController
@RequestMapping("/api/v1/stocks/movements")
@RequiredArgsConstructor
public class StockMovementController {
  
  private final StockMovementService stockMovementService;

  @Operation(summary = "Get stock movement by ID")
  @GetMapping("/{movementId}")
  @PreAuthorize("hasAuthority('STOCK_READ')")
  public ResponseEntity<StockMovementResponse> getById(@PathVariable UUID movementId) {
    StockMovement movement = stockMovementService.getById(movementId);
    return ResponseEntity.ok()
        .body(StockMovementResponse.fromEntity(movement));
  }

  @Operation(summary = "Get all stock movements")
  @GetMapping
  @PreAuthorize("hasAuthority('STOCK_READ')")
  public ResponseEntity<Page<StockMovementResponse>> getAll(
      @ModelAttribute StockMovementFilter filter,
      @PageableDefault(
        size = 20,
        sort = "timestamp",
        direction = Sort.Direction.DESC
      ) Pageable pageable
  ) {

    Page<StockMovement> movements = stockMovementService.getAll(filter, pageable);
    return ResponseEntity.ok()
        .body(movements.map(movement -> StockMovementResponse.fromEntity(movement)));
  }
  
  @Operation(summary = "Delete stock movements")
  @DeleteMapping("/{movementId}")
  @PreAuthorize("hasAuthority('STOCK_READ')")
  public ResponseEntity<?> delete(@PathVariable UUID movementId) {
    stockMovementService.delete(movementId);
    return ResponseEntity.ok().build();
  }
}
