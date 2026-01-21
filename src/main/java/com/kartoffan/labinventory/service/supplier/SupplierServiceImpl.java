package com.kartoffan.labinventory.service.supplier;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kartoffan.labinventory.dto.supplier.CreateSupplierRequest;
import com.kartoffan.labinventory.dto.supplier.UpdateSupplierRequest;
import com.kartoffan.labinventory.exception.ResourceAlreadyExistsException;
import com.kartoffan.labinventory.exception.ResourceNotFoundException;
import com.kartoffan.labinventory.model.Supplier;
import com.kartoffan.labinventory.repository.SupplierRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class SupplierServiceImpl implements SupplierService {
  
  private final SupplierRepository supplierRepository;

  @Override
  public Supplier create(CreateSupplierRequest request) {
    if (supplierRepository.existsByNameIgnoreCase(request.getName())) {
      throw new ResourceAlreadyExistsException("Supplier name already exists");
    }

    Supplier supplier = Supplier.builder()
        .name(request.getName())
        .contactName(request.getContactName())
        .email(request.getEmail())
        .phone(request.getPhone())
        .address(request.getAddress())
        .website(request.getWebsite())
        .notes(request.getNotes())
        .build();

    return supplierRepository.save(supplier);
  }
  
  @Override
  public Page<Supplier> getAll(Boolean active, String search, Pageable pageable) {
    String normalized = (search == null || search.isBlank()) ? null : search.trim();
    return supplierRepository.findAllFiltered(active, normalized, pageable);
  }

  @Override
  public Supplier getById(UUID supplierId) {
    return supplierRepository.findById(supplierId)
        .orElseThrow(() -> new ResourceNotFoundException("No supplier found with id " + supplierId));
  }

  @Override
  public Supplier update(UUID supplierId, UpdateSupplierRequest request) {
    Supplier supplier = supplierRepository.findById(supplierId)
        .orElseThrow(() -> new ResourceNotFoundException("No supplier found with id " + supplierId));

    if (request.getContactName() != null)
      supplier.setContactName(request.getContactName());
    if (request.getEmail() != null)
      supplier.setEmail(request.getEmail());
    if (request.getPhone() != null)
      supplier.setPhone(request.getPhone());
    if (request.getAddress() != null)
      supplier.setAddress(request.getAddress());
    if (request.getWebsite() != null)
      supplier.setWebsite(request.getWebsite());
    if (request.getNotes() != null)
      supplier.setNotes(request.getNotes());

    return supplier;
  }

  @Override
  public void archive(UUID supplierId) {
    Supplier supplier = supplierRepository.findByIdAndIsActiveTrue(supplierId)
        .orElseThrow(() -> new ResourceNotFoundException("No active supplier found with id " + supplierId));
      
    supplier.setIsActive(false);
  }

  @Override
  public void unarchive(UUID supplierId) {
    Supplier supplier = supplierRepository.findByIdAndIsActiveFalse(supplierId)
        .orElseThrow(() -> new ResourceNotFoundException("No inactive supplier found with id " + supplierId));
    supplier.setIsActive(true);
  }

}
