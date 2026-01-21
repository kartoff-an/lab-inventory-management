package com.kartoffan.labinventory.service.stockMovement;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.kartoffan.labinventory.dto.stockMovement.StockMovementFilter;
import com.kartoffan.labinventory.model.StockMovement;

public interface StockMovementService {

  StockMovement getById(UUID stockMovementId);

  Page<StockMovement> getAll(StockMovementFilter filter, Pageable pageable);

  void delete(UUID stockMovementId);
}
