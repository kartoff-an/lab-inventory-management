package com.kartoffan.labinventory.controller;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.kartoffan.labinventory.dto.supplier.CreateSupplierRequest;
import com.kartoffan.labinventory.dto.supplier.SupplierResponse;
import com.kartoffan.labinventory.dto.supplier.UpdateSupplierRequest;
import com.kartoffan.labinventory.model.Supplier;
import com.kartoffan.labinventory.service.supplier.SupplierService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Suppliers", description = "Endpoints for managing suppliers, including creation, updates, and status management")
@RestController
@RequestMapping("/api/v1/suppliers")
@RequiredArgsConstructor
public class SupplierController {
  private final SupplierService supplierService;

  @Operation(summary = "Create a supplier")
  @PostMapping
  @PreAuthorize("hasAuthority('SUPPLIER_WRITE')")
  public ResponseEntity<SupplierResponse> create(@Valid @RequestBody CreateSupplierRequest request) {
    Supplier supplier = supplierService.create(request);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(SupplierResponse.fromEntity(supplier));
  }

  @Operation(summary = "Get all suppliers")
  @GetMapping
  @PreAuthorize("hasAuthority('SUPPLIER_READ')")
  public ResponseEntity<Page<SupplierResponse>> getAll(
          @RequestParam(required = false) Boolean active,
          @RequestParam(required = false) String search,
          @PageableDefault(size = 20, sort = "createdAt") Pageable pageable
  ) {
    String normalizedSearch = (search == null || search.isBlank()) ? null : search.trim();
    Page<Supplier> suppliers = supplierService.getAll(active, normalizedSearch, pageable);
    return ResponseEntity.ok()
        .body(suppliers.map(supplier -> SupplierResponse.fromEntity(supplier)));
  }

  @Operation(summary = "Get supplier by ID")
  @GetMapping("/{supplierId}")
  @PreAuthorize("hasAuthority('SUPPLIER_READ')")
  public ResponseEntity<SupplierResponse> getById(@PathVariable UUID supplierId) {
    Supplier supplier = supplierService.getById(supplierId);
    return ResponseEntity.ok()
        .body(SupplierResponse.fromEntity(supplier));
  }

  @Operation(summary = "Update a supplier")
  @PutMapping("/{supplierId}")
  @PreAuthorize("hasAuthority('SUPPLIER_WRITE')")
  public ResponseEntity<SupplierResponse> update(@PathVariable UUID supplierId,
        @RequestBody UpdateSupplierRequest request) {
    
    Supplier updated = supplierService.update(supplierId, request);
    return ResponseEntity.ok()
        .body(SupplierResponse.fromEntity(updated));
  }

  @Operation(summary = "Archive a supplier")
  @PatchMapping("/{supplierId}/archive")
  @PreAuthorize("hasAuthority('SUPPLIER_WRITE')")
  public ResponseEntity<?> archive(@PathVariable UUID supplierId) {
    supplierService.archive(supplierId);
    return ResponseEntity.ok().build();
  }

  @Operation(summary = "Unarchive a supplier")
  @PatchMapping("/{supplierId}/unarchive")
  @PreAuthorize("hasAuthority('SUPPLIER_WRITE')")
  public ResponseEntity<?> unarchive(@PathVariable UUID supplierId) {
    supplierService.unarchive(supplierId);
    return ResponseEntity.ok().build();
  }
}
