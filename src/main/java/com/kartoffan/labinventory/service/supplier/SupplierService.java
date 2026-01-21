package com.kartoffan.labinventory.service.supplier;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.kartoffan.labinventory.dto.supplier.CreateSupplierRequest;
import com.kartoffan.labinventory.dto.supplier.UpdateSupplierRequest;
import com.kartoffan.labinventory.model.Supplier;

public interface SupplierService {
  Supplier create(CreateSupplierRequest request);

  Page<Supplier> getAll(Boolean active, String search, Pageable pageable);

  Supplier getById(UUID supplierId);

  Supplier update(UUID supplierId, UpdateSupplierRequest request);

  void archive(UUID supplierId);

  void unarchive(UUID supplierId);
}
