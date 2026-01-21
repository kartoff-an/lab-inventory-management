package com.kartoffan.labinventory.service.stock;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kartoffan.labinventory.dto.stock.*;
import com.kartoffan.labinventory.exception.InsufficientStockException;
import com.kartoffan.labinventory.model.*;
import com.kartoffan.labinventory.model.StockMovement.MovementType;
import com.kartoffan.labinventory.repository.StockMovementRepository;
import com.kartoffan.labinventory.service.item.ItemService;
import com.kartoffan.labinventory.service.lab.LabService;
import com.kartoffan.labinventory.service.supplier.SupplierService;
import com.kartoffan.labinventory.service.user.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class StockServiceImpl implements StockService {
  
  private final StockMovementRepository stockMovementRepository;
  
  private final ItemService itemService;
  private final LabService labService;
  private final SupplierService supplierService;
  private final UserService userService;

  /**
   * Increases stock levels for an item within a specific lab
   * used for receiving new shipments or returning items to inventory
   */
  @Override
  public void stockIn(StockInRequest request) {
    Item item = itemService.getById(request.getItemId());
    Lab lab = labService.getById(request.getLabId());
    User performedBy = userService.getById(request.getPerformedBy());
    Supplier supplier = null;

    if (request.getSupplierId() != null) {
      supplier = supplierService.getById(request.getSupplierId());
    }

    StockMovement movement = StockMovement.builder()
        .item(item)
        .lab(lab)
        .type(MovementType.IN)
        .quantity(request.getQuantity())
        .supplier(supplier)
        .batchNumber(request.getBatchNumber())
        .expirationDate(request.getExpirationDate())
        .reason(request.getReason())
        .reference(request.getReference())
        .performedBy(performedBy)
        .build();

    stockMovementRepository.save(movement);
  }

  /**
   * Decreases stock levels for an item
   * Validates that sufficient stock exists before committing the movement
   */
  @Override
  public void stockOut(StockOutRequest request) {
    Item item = itemService.getById(request.getItemId());
    Lab lab = labService.getById(request.getLabId());
    User performedBy = userService.getById(request.getPerformedBy());

    double available = getCurrentQuantityByLab(item.getId(), lab.getId()).getQuantity();
    if (available < request.getQuantity()) {
      throw new InsufficientStockException(item.getName());
    }

    StockMovement movement = StockMovement.builder()
        .item(item)
        .lab(lab)
        .type(MovementType.OUT)
        .quantity(-request.getQuantity())
        .reference(request.getPurpose())
        .reason(request.getReason())
        .performedBy(performedBy)
        .build();

    stockMovementRepository.save(movement);
  }

  /**
   * Manually adjusts stock levels
   */
  @Override
  public void adjustStock(StockAdjustRequest request) {
    Item item = itemService.getById(request.getItemId());
    Lab lab = labService.getById(request.getLabId());
    User performedBy = userService.getById(request.getPerformedBy());

    StockMovement movement = StockMovement.builder()
        .item(item)
        .lab(lab)
        .type(MovementType.ADJUST)
        .quantity(request.getAdjustment())
        .reason(request.getReason())
        .performedBy(performedBy)
        .build();

    stockMovementRepository.save(movement);
  }

  /**
   * Transfers stock from one lab to another
   */
  @Override
  public void transferStock(StockTransferRequest request) {
    stockOut(
      new StockOutRequest(
        request.getItemId(),
        request.getFromLabId(),
        request.getQuantity(),
        "Transfer to lab " + request.getToLabId(),
        request.getReason(),
        request.getPerformedBy()
      )
    );

    stockIn(
      new StockInRequest(
        request.getItemId(),
        request.getToLabId(),
        request.getQuantity(),
        null,
        request.getBatchNumber(),
        request.getExpirationDate(),
        "Transfer from lab " + request.getFromLabId(),
        request.getReason(),
        request.getPerformedBy()
      )
    );
  }
  
  /**
   * Calculates the current balance for a specific item in a specific lab
   */
  @Override
  public StockQuantity getCurrentQuantityByLab(UUID itemId, UUID labId) {
    Item item = itemService.getById(itemId);
    double quantity = stockMovementRepository.getCurrentQuantityByLab(itemId, labId);
    return StockQuantity.builder()
        .itemId(itemId)
        .itemName(item.getName())
        .quantity(quantity)
        .build();
  }

  /**
   * Retrieves a list of all item quantities for a specific lab
   */
  @Override
  public List<StockQuantity> getAllItemQuantities(UUID labId) {
    List<Object[]> results = stockMovementRepository.getAllItemQuantitiesByLab(labId);
    List<StockQuantity> quantities = new ArrayList<>();

    for (Object[] row : results) {
      Item item = itemService.getById((UUID) row[0]);
      StockQuantity quantity = StockQuantity.builder()
          .itemId((UUID) row[0])
          .itemName(item.getName())
          .quantity(((Double) row[1]).doubleValue())
          .build();
      quantities.add(quantity);
    }
    return quantities;
  }
  
  /**
   * Identifies items whose stock levels have fallen below their defined minimum
   * threshold
   */
  @Override
  public List<Item> getLowStockItems(UUID labId) {
    labService.getById(labId);
    return stockMovementRepository.findLowStockItemsByLab(labId);
  }

  /**
   * Identifies items that have a balance of zero or less in the specified lab
   */
  @Override
  public List<Item> getOutOfStockItems(UUID labId) {
    labService.getById(labId);
    return stockMovementRepository.findOutOfStockItemsByLab(labId);
  }
}
