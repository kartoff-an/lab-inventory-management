package com.kartoffan.labinventory.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "stock_movements")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockMovement {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id", updatable = false, nullable = false)
  private UUID id;
  
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "item_id", nullable = false)
  private Item item;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "lab_id", nullable = false)
  private Lab lab;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "performed_by", nullable = false)
  private User performedBy;

  @Enumerated(EnumType.STRING)
  @Column(name = "type", nullable = false, length = 20)
  private MovementType type;
  
  @Column(name = "quantity", nullable = false)
  private double quantity; // Positive for IN, negative for OUT

  @ManyToOne
  private Supplier supplier; // for IN

  private String batchNumber;
  private LocalDate expirationDate;

  private String reference;

  @Column(name = "reason", nullable = false, length = 500)
  private String reason;

  @Column(name = "timestamp", nullable = false)
  private LocalDateTime timestamp;

  @PrePersist
  public void prePersist() {
    timestamp = LocalDateTime.now();
  }

  public enum MovementType {
    IN, // Stock added (delivery, purchase)
    OUT, // Stock consumed / removed
    ADJUST, // Manual adjustment (inventory recount, correction)
    TRANSFER_IN,
    TRANSFER_OUT
  }
}
