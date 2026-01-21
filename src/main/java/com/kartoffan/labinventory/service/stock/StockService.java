package com.kartoffan.labinventory.service.stock;

import java.util.List;
import java.util.UUID;

import com.kartoffan.labinventory.dto.stock.StockAdjustRequest;
import com.kartoffan.labinventory.dto.stock.StockInRequest;
import com.kartoffan.labinventory.dto.stock.StockOutRequest;
import com.kartoffan.labinventory.dto.stock.StockQuantity;
import com.kartoffan.labinventory.dto.stock.StockTransferRequest;
import com.kartoffan.labinventory.model.Item;

public interface StockService {

  void stockIn(StockInRequest request);

  void stockOut(StockOutRequest request);

  void adjustStock(StockAdjustRequest request);

  void transferStock(StockTransferRequest request);

  StockQuantity getCurrentQuantityByLab(UUID itemId, UUID labId);

  List<StockQuantity> getAllItemQuantities(UUID labId);

  List<Item> getLowStockItems(UUID labId);

  List<Item> getOutOfStockItems(UUID labId);

}
