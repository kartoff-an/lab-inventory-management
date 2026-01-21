package com.kartoffan.labinventory.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kartoffan.labinventory.dto.stock.StockAdjustRequest;
import com.kartoffan.labinventory.dto.stock.StockInRequest;
import com.kartoffan.labinventory.dto.stock.StockOutRequest;
import com.kartoffan.labinventory.dto.stock.StockQuantity;
import com.kartoffan.labinventory.dto.stock.StockTransferRequest;
import com.kartoffan.labinventory.exception.InsufficientStockException;
import com.kartoffan.labinventory.model.Item;
import com.kartoffan.labinventory.model.Lab;
import com.kartoffan.labinventory.model.StockMovement.MovementType;
import com.kartoffan.labinventory.model.Supplier;
import com.kartoffan.labinventory.model.User;
import com.kartoffan.labinventory.repository.StockMovementRepository;
import com.kartoffan.labinventory.service.item.ItemService;
import com.kartoffan.labinventory.service.lab.LabService;
import com.kartoffan.labinventory.service.stock.StockServiceImpl;
import com.kartoffan.labinventory.service.supplier.SupplierService;
import com.kartoffan.labinventory.service.user.UserService;

@ExtendWith(MockitoExtension.class)
public class StockServiceTest {
  
  @Mock
  private StockMovementRepository stockMovementRepository;

  @Mock
  private ItemService itemService;

  @Mock
  private LabService labService;

  @Mock
  private SupplierService supplierService;

  @Mock
  private UserService userService;

  @InjectMocks
  private StockServiceImpl stockService;

  private UUID itemId;
  private UUID labId;
  private UUID userId;
  private UUID supplierId;

  private Item item;
  private Lab lab;
  private User user;
  private Supplier supplier;

  @BeforeEach
  void setUp() {
    itemId = UUID.randomUUID();
    labId = UUID.randomUUID();
    userId = UUID.randomUUID();
    supplierId = UUID.randomUUID();

    item = Item.builder()
        .id(itemId)
        .name("Ethanol")
        .build();

    lab = Lab.builder()
        .id(labId)
        .build();

    user = User.builder()
        .id(userId)
        .build();

    supplier = Supplier.builder()
        .id(supplierId)
        .build();
  }

  @Test
  void stockIn_success_withoutSupplier() {
    StockInRequest request = new StockInRequest(
        itemId,
        labId, 10.0,
        null,
        "BATCH-1",
        LocalDate.now(),
        "PO-001",
        "Restock",
        userId);

    when(itemService.getById(itemId)).thenReturn(item);
    when(labService.getById(labId)).thenReturn(lab);
    when(userService.getById(userId)).thenReturn(user);

    stockService.stockIn(request);

    verify(stockMovementRepository).save(argThat(m -> m.getType() == MovementType.IN &&
        m.getQuantity() == 10.0 &&
        m.getItem().equals(item)));
  }
  
  @Test
  void stockIn_withSupplier_success() {
    StockInRequest request = new StockInRequest(
        itemId,
        labId,
        5.0,
        supplierId,
        null,
        null,
        "Supplier delivery",
        null,
        userId);

    when(itemService.getById(itemId)).thenReturn(item);
    when(labService.getById(labId)).thenReturn(lab);
    when(userService.getById(userId)).thenReturn(user);
    when(supplierService.getById(supplierId)).thenReturn(supplier);

    stockService.stockIn(request);

    verify(stockMovementRepository).save(argThat(m -> m.getSupplier().equals(supplier) &&
        m.getQuantity() == 5.0));
  }

  @Test
  void stockOut_success() {
    StockOutRequest request = new StockOutRequest(
        itemId,
        labId,
        3.0,
        "Experiment",
        "Usage",
        userId);

    when(itemService.getById(itemId)).thenReturn(item);
    when(labService.getById(labId)).thenReturn(lab);
    when(userService.getById(userId)).thenReturn(user);
    when(stockMovementRepository.getCurrentQuantityByLab(itemId, labId))
        .thenReturn(10.0);

    stockService.stockOut(request);

    verify(stockMovementRepository).save(argThat(m -> m.getType() == MovementType.OUT &&
        m.getQuantity() == -3.0));
  }
  
  @Test
  void stockOut_insufficientStock_throwsException() {
    StockOutRequest request = new StockOutRequest(
        itemId,
        labId,
        20.0,
        "Experiment",
        "Usage",
        userId);

    when(itemService.getById(itemId)).thenReturn(item);
    when(labService.getById(labId)).thenReturn(lab);
    when(userService.getById(userId)).thenReturn(user);
    when(stockMovementRepository.getCurrentQuantityByLab(itemId, labId))
        .thenReturn(5.0);

    assertThrows(InsufficientStockException.class,
        () -> stockService.stockOut(request));

    verify(stockMovementRepository, never()).save(any());
  }
  
  @Test
  void adjustStock_success() {
    StockAdjustRequest request = new StockAdjustRequest(
        itemId,
        labId,
        -2.0,
        "Damaged",
        userId);

    when(itemService.getById(itemId)).thenReturn(item);
    when(labService.getById(labId)).thenReturn(lab);
    when(userService.getById(userId)).thenReturn(user);

    stockService.adjustStock(request);

    verify(stockMovementRepository).save(argThat(m -> m.getType() == MovementType.ADJUST &&
        m.getQuantity() == -2));
  }
  
  @Test
  void transferStock_success() {
    StockTransferRequest request = new StockTransferRequest(
        itemId,
        labId,
        UUID.randomUUID(),
        4.0,
        "BATCH-T",
        null,
        "Inter-lab transfer",
        userId);

    when(itemService.getById(itemId)).thenReturn(item);
    when(labService.getById(any())).thenReturn(lab);
    when(userService.getById(userId)).thenReturn(user);
    when(stockMovementRepository.getCurrentQuantityByLab(itemId, labId))
        .thenReturn(10.0);

    stockService.transferStock(request);

    verify(stockMovementRepository, times(2)).save(any());
  }
  
  @Test
  void getCurrentQuantityByLab_success() {
    when(itemService.getById(itemId)).thenReturn(item);
    when(stockMovementRepository.getCurrentQuantityByLab(itemId, labId))
        .thenReturn(7.5);

    StockQuantity result =
        stockService.getCurrentQuantityByLab(itemId, labId);

    assertEquals(7.5, result.getQuantity());
    assertEquals("Ethanol", result.getItemName());
  }

  // TODO Test for getAllItemQuantities

  @Test
  void getLowStockItems_success() {
    when(labService.getById(labId)).thenReturn(lab);
    when(stockMovementRepository.findLowStockItemsByLab(labId))
        .thenReturn(List.of(item));

    List<Item> result = stockService.getLowStockItems(labId);

    assertEquals(1, result.size());
  }

  @Test
  void getOutOfStockItems_success() {
    when(labService.getById(labId)).thenReturn(lab);
    when(stockMovementRepository.findOutOfStockItemsByLab(labId))
        .thenReturn(List.of(item));

    List<Item> result = stockService.getOutOfStockItems(labId);

    assertEquals(1, result.size());
  }
}
