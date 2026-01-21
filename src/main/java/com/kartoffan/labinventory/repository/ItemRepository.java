package com.kartoffan.labinventory.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.kartoffan.labinventory.model.Item;

@Repository
public interface ItemRepository extends JpaRepository<Item, UUID>, JpaSpecificationExecutor<Item> {
  
  boolean existsByNameAndLabId(String name, UUID labId);

  Optional<Item> findByIdAndIsActiveTrue(UUID itemId);

  Optional<Item> findByIdAndIsActiveFalse(UUID itemId);
}
