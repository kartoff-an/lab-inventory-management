package com.kartoffan.labinventory.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.kartoffan.labinventory.dto.stock.*;
import com.kartoffan.labinventory.model.Item;
import com.kartoffan.labinventory.service.stock.StockService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Stock", description = "Endpoints for managing inventory stock movements, adjustments, and quantity tracking")
@RestController
@RequestMapping("/api/v1/stocks")

@RequiredArgsConstructor
public class StockController {
  
  private final StockService stockService;

  @Operation(summary = "Add stock")
  @PostMapping("/in")
  @PreAuthorize("hasAuthority('STOCK_WRITE')")
  public ResponseEntity<?> stockIn(@RequestBody StockInRequest request) {
    stockService.stockIn(request);
    return ResponseEntity.ok("Stock added successfully");
  }

  @Operation(summary = "Issue stock")
  @PostMapping("/out")
  @PreAuthorize("hasAuthority('STOCK_WRITE')")
  public ResponseEntity<?> stockOut(@RequestBody StockOutRequest request) {
    stockService.stockOut(request);
    return ResponseEntity.ok("Stock issued successfully");
  }

  @Operation(summary = "Adjust stock")
  @PostMapping("/adjust")
  @PreAuthorize("hasAuthority('STOCK_WRITE')")
  public ResponseEntity<?> adjustStock(@RequestBody StockAdjustRequest request) {
    stockService.adjustStock(request);
    return ResponseEntity.ok("Stock adjusted successfully");
  }

  @Operation(summary = "Transfer stock")
  @PostMapping("/transfer")
  @PreAuthorize("hasAuthority('STOCK_WRITE')")
  public ResponseEntity<?> transferStock(@RequestBody StockTransferRequest request) {
    stockService.transferStock(request);
    return ResponseEntity.ok("Stock transferred successfully");
  }

  @Operation(summary = "Get item quantity by lab")
  @GetMapping("/{itemId}/quantity")
  @PreAuthorize("hasAuthority('STOCK_READ')")
  public ResponseEntity<StockQuantity> getQuantity(
      @PathVariable UUID itemId,
      @RequestParam UUID labId
  ) {

    StockQuantity quantity = stockService.getCurrentQuantityByLab(itemId, labId);
    return ResponseEntity.ok()
        .body(quantity);
  }

  @Operation(summary = "Get all item quantities in a lab")
  @GetMapping("/quantities")
  @PreAuthorize("hasAuthority('STOCK_READ')")
  public ResponseEntity<List<StockQuantity>> getAllQuantities(@RequestParam UUID labId) {
    List<StockQuantity> quantities = stockService.getAllItemQuantities(labId);
    return ResponseEntity.ok(quantities);
  }

  @Operation(summary = "Get low stock items")
  @GetMapping("/low-stock")
  @PreAuthorize("hasAuthority('STOCK_READ')")
  public ResponseEntity<List<Item>> getLowStockItems(@RequestParam UUID labId) {
    List<Item> items = stockService.getLowStockItems(labId);
    return ResponseEntity.ok(items);
  }

  @Operation(summary = "Get out-of-stock items")
  @GetMapping("/out-of-stock")
  @PreAuthorize("hasAuthority('STOCK_READ')")
  public ResponseEntity<List<Item>> getOutOfStockItems(@RequestParam UUID labId) {
    List<Item> items = stockService.getOutOfStockItems(labId);
    return ResponseEntity.ok(items);
  }
}
