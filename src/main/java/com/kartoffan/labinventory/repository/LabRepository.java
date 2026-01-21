package com.kartoffan.labinventory.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.kartoffan.labinventory.model.Lab;

@Repository
public interface LabRepository extends JpaRepository<Lab, UUID> {
  
  boolean existsByNameAndLocationAndIsActiveTrue(String name, String location);

  Optional<Lab> findByIdAndIsActiveTrue(UUID labId);

  Optional<Lab> findByIdAndIsActiveFalse(UUID labId);

  @Query("""
      SELECT l FROM Lab l
      WHERE (:active IS NULL OR l.isActive = :active)
      AND (:search IS NULL OR LOWER(l.name) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%')))
  """)
  Page<Lab> findAllFiltered(Boolean active, String search, Pageable pageable);
}
