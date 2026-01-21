package com.kartoffan.labinventory.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.kartoffan.labinventory.dto.stockMovement.StockMovementFilter;
import com.kartoffan.labinventory.exception.ResourceNotFoundException;
import com.kartoffan.labinventory.model.StockMovement;
import com.kartoffan.labinventory.repository.StockMovementRepository;
import com.kartoffan.labinventory.service.stockMovement.StockMovementServiceImpl;

@ExtendWith(MockitoExtension.class)
public class StockMovementServiceTest {
  
  @Mock
  private StockMovementRepository stockMovementRepository;

  @InjectMocks
  private StockMovementServiceImpl stockMovementService;

  private UUID movementId;
  private StockMovement movement;

  @BeforeEach
  void setUp() {
    movementId = UUID.randomUUID();
    movement = StockMovement.builder()
        .id(movementId)
        .build();
  }

  @Test
  void getById_success() {
    when(stockMovementRepository.findById(movementId))
        .thenReturn(Optional.of(movement));

    StockMovement result = stockMovementService.getById(movementId);

    assertNotNull(result);
    assertEquals(movementId, result.getId());
  }

  @Test
  void getById_notFound_throwsException() {
    when(stockMovementRepository.findById(movementId))
        .thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class,
        () -> stockMovementService.getById(movementId));
  }

  @Test
  void getAll_success() {
    StockMovementFilter filter = new StockMovementFilter();
    Pageable pageable = PageRequest.of(0, 10);
    Page<StockMovement> page = new PageImpl<>(List.of(movement));

    when(stockMovementRepository.findAll(argThat((Specification<StockMovement> spec) -> true), eq(pageable)))
        .thenReturn(page);

    Page<StockMovement> result = stockMovementService.getAll(filter, pageable);

    assertEquals(1, result.getTotalElements());
    verify(stockMovementRepository)
        .findAll(argThat((Specification<StockMovement> spec) -> true), eq(pageable));
  }
  
  @Test
  void delete_success() {
    when(stockMovementRepository.existsById(movementId))
        .thenReturn(true);

    stockMovementService.delete(movementId);

    verify(stockMovementRepository).deleteById(movementId);
  }

  @Test
  void delete_notFound_throwsException() {
    when(stockMovementRepository.existsById(movementId))
        .thenReturn(false);

    assertThrows(ResourceNotFoundException.class,
        () -> stockMovementService.delete(movementId));

    verify(stockMovementRepository, never()).deleteById(any());
  }
}
