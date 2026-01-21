package com.kartoffan.labinventory.security.role;

import java.util.Set;

public enum Role {
  SUPER_ADMIN(Set.of(
    Authority.CATEGORY_READ,
    Authority.CATEGORY_WRITE,
    Authority.ITEM_READ,
    Authority.ITEM_WRITE,
    Authority.STOCK_READ,
    Authority.STOCK_WRITE,
    Authority.LAB_READ,
    Authority.LAB_WRITE,
    Authority.USER_READ,
    Authority.USER_WRITE,
    Authority.SUPPLIER_READ,
    Authority.SUPPLIER_WRITE
  )),
      
  LAB_ADMIN(Set.of(
    Authority.CATEGORY_READ,
    Authority.CATEGORY_WRITE,
    Authority.ITEM_READ,
    Authority.ITEM_WRITE,
    Authority.STOCK_READ,
    Authority.STOCK_WRITE,
    Authority.SUPPLIER_READ,
    Authority.SUPPLIER_WRITE
  )),
      
  STAFF(Set.of(
    Authority.CATEGORY_READ,
    Authority.ITEM_READ,
    Authority.STOCK_READ,
    Authority.SUPPLIER_READ
  ));

  private final Set<Authority> authorities;

  Role(Set<Authority> authorities) {
    this.authorities = authorities;
  }

  public Set<Authority> getAuthorities() {
    return authorities;
  }
}
