package com.kartoffan.labinventory.model;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "items", uniqueConstraints = {
  @UniqueConstraint(columnNames = {"name", "lab_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Item {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id", updatable = false, nullable = false)
  private UUID id;

  @Column(name = "name", nullable = false, length = 150)
  private String name;

  @Column(name = "code", length = 50)
  private String code;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "lab_id", nullable = false)
  private Lab lab;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "category_id", nullable = false)
  private Category category;

  @Column(name = "unit", nullable = false, length = 10)
  private String unit;

  @Column(name = "low_stock_threshold", nullable = false)
  @Builder.Default
  private Integer lowStockThreshold = 5;

  @Column(name = "reorder_level", nullable = false)
  private Integer reorderLevel;

  @Column(name = "min_quantity", nullable = false)
  @Builder.Default
  private Integer minQuantity = 0;

  @Column(name = "max_quantity", nullable = false)
  private Integer maxQuantity;

  @Column(name = "location", length = 100)
  private String location;

  @Column(name = "storage_conditions", length = 255)
  private String storageCondition;

  @Column(name = "is_active", nullable = false)
  @Builder.Default
  private Boolean isActive = true;

  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  @PrePersist
  public void prePersist() {
    createdAt = LocalDateTime.now();
    updatedAt = createdAt;
  }

  @PreUpdate
  public void preUpdate() {
    updatedAt = LocalDateTime.now();
  }
}
