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

import com.kartoffan.labinventory.dto.supplier.CreateSupplierRequest;
import com.kartoffan.labinventory.dto.supplier.UpdateSupplierRequest;
import com.kartoffan.labinventory.exception.ResourceAlreadyExistsException;
import com.kartoffan.labinventory.exception.ResourceNotFoundException;
import com.kartoffan.labinventory.model.Supplier;
import com.kartoffan.labinventory.repository.SupplierRepository;
import com.kartoffan.labinventory.service.supplier.SupplierServiceImpl;

@ExtendWith(MockitoExtension.class)
public class SupplierServiceTest {
  
  @Mock
  private SupplierRepository supplierRepository;

  @InjectMocks
  private SupplierServiceImpl supplierService;

  private UUID supplierId;
  private Supplier supplier;

  @BeforeEach
  void setUp() {
    supplierId = UUID.randomUUID();
    supplier = Supplier.builder()
        .id(supplierId)
        .name("Sigma-Aldrich")
        .isActive(true)
        .build();
  }

  @Test
  void createSupplier_success() {
    CreateSupplierRequest request = new CreateSupplierRequest();
    request.setName("Sigma-Aldrich");

    when(supplierRepository.existsByNameIgnoreCase("Sigma-Aldrich"))
        .thenReturn(false);
    when(supplierRepository.save(any(Supplier.class)))
        .thenAnswer(i -> i.getArgument(0));

    Supplier result = supplierService.create(request);

    assertEquals("Sigma-Aldrich", result.getName());
  }

  @Test
  void createSupplier_duplicateName_throwsException() {
    CreateSupplierRequest request = new CreateSupplierRequest();
    request.setName("Sigma-Aldrich");

    when(supplierRepository.existsByNameIgnoreCase("Sigma-Aldrich"))
        .thenReturn(true);

    assertThrows(ResourceAlreadyExistsException.class,
        () -> supplierService.create(request));
  }

  @Test
  void getById_success() {
    when(supplierRepository.findById(supplierId))
        .thenReturn(Optional.of(supplier));

    Supplier result = supplierService.getById(supplierId);

    assertEquals(supplierId, result.getId());
  }

  @Test
  void getById_notFound_throwsException() {
    when(supplierRepository.findById(supplierId))
        .thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class,
        () -> supplierService.getById(supplierId));
  }

  @Test
  void getAll_returnsPage() {
    Pageable pageable = PageRequest.of(0, 10);
    Page<Supplier> page = new PageImpl<>(List.of(supplier));

    when(supplierRepository.findAllFiltered(true, "sig", pageable))
        .thenReturn(page);

    Page<Supplier> result =
        supplierService.getAll(true, "sig", pageable);

    assertEquals(1, result.getTotalElements());
  }

  @Test
  void getAll_blankSearch_normalizedToNull() {
    Pageable pageable = PageRequest.of(0, 10);

    supplierService.getAll(null, "   ", pageable);

    verify(supplierRepository)
        .findAllFiltered(null, null, pageable);
  }

  @Test
  void updateSupplier_success() {
    UpdateSupplierRequest request = new UpdateSupplierRequest();
    request.setEmail("support@sigma.com");
    request.setPhone("123456");

    when(supplierRepository.findById(supplierId))
        .thenReturn(Optional.of(supplier));

    Supplier result = supplierService.update(supplierId, request);

    assertEquals("support@sigma.com", result.getEmail());
    assertEquals("123456", result.getPhone());
  }

  @Test
  void updateSupplier_notFound_throwsException() {
    when(supplierRepository.findById(supplierId))
        .thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class,
        () -> supplierService.update(supplierId, new UpdateSupplierRequest()));
  }

  @Test
  void archiveSupplier_success() {
    when(supplierRepository.findByIdAndIsActiveTrue(supplierId))
        .thenReturn(Optional.of(supplier));

    supplierService.archive(supplierId);

    assertFalse(supplier.getIsActive());
  }

  @Test
  void archiveSupplier_notFound_throwsException() {
    when(supplierRepository.findByIdAndIsActiveTrue(supplierId))
        .thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class,
        () -> supplierService.archive(supplierId));
  }

  @Test
  void unarchiveSupplier_success() {
    supplier.setIsActive(false);

    when(supplierRepository.findByIdAndIsActiveFalse(supplierId))
        .thenReturn(Optional.of(supplier));

    supplierService.unarchive(supplierId);

    assertTrue(supplier.getIsActive());
  }

  @Test
  void unarchiveSupplier_notFound_throwsException() {
    when(supplierRepository.findByIdAndIsActiveFalse(supplierId))
        .thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class,
        () -> supplierService.unarchive(supplierId));
  }
}
