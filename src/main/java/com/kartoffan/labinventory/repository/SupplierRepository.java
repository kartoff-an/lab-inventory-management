package com.kartoffan.labinventory.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.kartoffan.labinventory.model.Supplier;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, UUID> {
  boolean existsByNameIgnoreCase(String name);

  Optional<Supplier> findByIdAndIsActiveTrue(UUID supplierId);

  Optional<Supplier> findByIdAndIsActiveFalse(UUID supplierId);

  @Query("""
      SELECT s FROM Supplier s
      WHERE (:active IS NULL OR s.isActive = :active)
      AND (:search IS NULL OR LOWER(s.name) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%')))
  """)
  Page<Supplier> findAllFiltered(Boolean active, String search, Pageable pageable);
}
