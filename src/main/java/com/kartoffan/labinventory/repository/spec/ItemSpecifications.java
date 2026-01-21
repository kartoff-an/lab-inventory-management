package com.kartoffan.labinventory.repository.spec;

import org.springframework.data.jpa.domain.Specification;

import com.kartoffan.labinventory.dto.item.ItemFilter;
import com.kartoffan.labinventory.model.Item;

public final class ItemSpecifications {
  
  public static Specification<Item> fromFilter(ItemFilter filter) {
    Specification<Item> spec = Specification.allOf();

    if (filter.getActive() != null)
      spec = spec.and((root, query, criteria) -> criteria.equal(root.get("isActive"), filter.getActive()));

    if (filter.getLabId() != null)
      spec = spec.and((root, query, criteria) -> criteria.equal(root.get("lab").get("id"), filter.getLabId()));

    if (filter.getCategoryId() != null)
      spec = spec.and((root, query, criteria) -> criteria.equal(root.get("category").get("id"), filter.getCategoryId()));

    if (filter.getSearch() != null && !filter.getSearch().isBlank()) {
      String pattern = "%" + filter.getSearch().toLowerCase() + "%";
      spec = spec.and((root, query, criteria) -> criteria.or(
          criteria.like(criteria.lower(root.get("name")), pattern),
          criteria.like(criteria.lower(root.get("code")), pattern)));
    }
    
    return spec;
  }
}
