package com.kartoffan.labinventory.controller;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.kartoffan.labinventory.dto.item.*;
import com.kartoffan.labinventory.model.Item;
import com.kartoffan.labinventory.service.item.ItemService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Items", description = "Endpoints for managing laboratory inventory items")
@RestController
@RequestMapping("/api/v1/items")
@RequiredArgsConstructor
public class ItemController {
  
  private final ItemService itemService;

  @Operation(summary = "Get all items")
  @GetMapping
  @PreAuthorize("hasAuthority('ITEM_READ')")
  public ResponseEntity<Page<ItemResponse>> getAll(
      @ModelAttribute ItemFilter filter,
      @PageableDefault(
        size = 20,
        sort = "createdAt",
        direction = Sort.Direction.DESC
      ) Pageable pageable
  ) {

    Page<Item> items = itemService.getAll(filter, pageable);
    return ResponseEntity.ok()
        .body(items.map(item -> ItemResponse.fromEntity(item)));
  }

  @Operation(summary = "Get item by ID")
  @GetMapping("/{itemId}")
  @PreAuthorize("hasAuthority('ITEM_READ')")
  public ResponseEntity<ItemResponse> getById(@PathVariable UUID itemId) {
    Item item = itemService.getById(itemId);
    return ResponseEntity.ok()
        .body(ItemResponse.fromEntity(item));
  }

  @Operation(summary = "Create an item")
  @PostMapping
  @PreAuthorize("hasAuthority('ITEM_WRITE')")
  public ResponseEntity<ItemResponse> create(@Valid @RequestBody CreateItemRequest request) {
    Item item = itemService.create(request);
    return ResponseEntity.ok()
        .body(ItemResponse.fromEntity(item));
  }

  @Operation(summary = "Update an item")
  @PutMapping("/{itemId}")
  @PreAuthorize("hasAuthority('ITEM_WRITE')")
  public ResponseEntity<ItemResponse> update(@PathVariable UUID itemId, @Valid @RequestBody UpdateItemRequest request) {
    Item item = itemService.update(itemId, request);
    return ResponseEntity.ok()
        .body(ItemResponse.fromEntity(item));
  }

  @Operation(summary = "Archive an item")
  @PostMapping("/{itemId}/archive")
  @PreAuthorize("hasAuthority('ITEM_WRITE')")
  public ResponseEntity<?> archive(@PathVariable UUID itemId) {
    itemService.archive(itemId);
    return ResponseEntity.ok().build();
  }

  @Operation(summary = "Unarchive an item")
  @PostMapping("/{itemId}/unarchive")
  @PreAuthorize("hasAuthority('ITEM_WRITE')")
  public ResponseEntity<?> unarchive(@PathVariable UUID itemId) {
    itemService.unarchive(itemId);
    return ResponseEntity.ok().build();
  }
}
