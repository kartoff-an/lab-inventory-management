package com.kartoffan.labinventory.repository.spec;

import org.springframework.data.jpa.domain.Specification;

import com.kartoffan.labinventory.dto.stockMovement.StockMovementFilter;
import com.kartoffan.labinventory.model.StockMovement;

public final class StockMovementSpecifications {
  
  public static Specification<StockMovement> fromFilter(StockMovementFilter filter) {
    Specification<StockMovement> spec = Specification.allOf();

    if (filter.getItemId() != null) {
      spec = spec.and((root, query, criteria) -> criteria.equal(root.get("item").get("id"), filter.getItemId()));
    }
    
    if (filter.getLabId() != null) {
      spec = spec.and((root, query, criteria) -> criteria.equal(root.get("lab").get("id"), filter.getLabId()));
    }

    if (filter.getType() != null) {
      spec = spec.and((root, query, criteria) -> criteria.equal(root.get("type"), filter.getType()));
    }

    if (filter.getSupplierId() != null) {
      spec = spec
          .and((root, query, criteria) -> criteria.equal(root.get("supplier").get("id"), filter.getSupplierId()));
    }

    if (filter.getFrom() != null) {
      spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("createdAt"), filter.getFrom()));
    }

    if (filter.getTo() != null) {
      spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("createdAt"), filter.getTo()));
    }

    return spec;
  }
}
