package com.kartoffan.labinventory.service.stockMovement;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kartoffan.labinventory.dto.stockMovement.StockMovementFilter;
import com.kartoffan.labinventory.exception.ResourceNotFoundException;
import com.kartoffan.labinventory.model.StockMovement;
import com.kartoffan.labinventory.repository.StockMovementRepository;
import com.kartoffan.labinventory.repository.spec.StockMovementSpecifications;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class StockMovementServiceImpl implements StockMovementService {
  
  private final StockMovementRepository stockMovementRepository;

  public StockMovement getById(UUID movementId) {
    return stockMovementRepository.findById(movementId)
        .orElseThrow(() -> new ResourceNotFoundException("No stock movement found with id " + movementId));
  }

  public Page<StockMovement> getAll(StockMovementFilter filter, Pageable pageable) {
    return stockMovementRepository
        .findAll(StockMovementSpecifications.fromFilter(filter), pageable);
  }

  public void delete(UUID movementId) {
    if (!stockMovementRepository.existsById(movementId)) {
      throw new ResourceNotFoundException("No stock movement found with id " + movementId);
    }
    stockMovementRepository.deleteById(movementId);
  }
}
