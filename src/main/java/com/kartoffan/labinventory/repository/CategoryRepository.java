package com.kartoffan.labinventory.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.kartoffan.labinventory.model.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {
  boolean existsByName(String name);

  Optional<Category> findByIdAndIsActiveTrue(UUID categoryId);

  Optional<Category> findByIdAndIsActiveFalse(UUID categoryId);

  @Query("""
      SELECT c FROM Category c
      WHERE (:active IS NULL OR c.isActive = :active)
      AND (:search IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%')))
  """)
  Page<Category> findAllFiltered(Boolean active, String search, Pageable pageable);
}
