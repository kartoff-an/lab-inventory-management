package com.kartoffan.labinventory.controller;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.kartoffan.labinventory.dto.category.*;
import com.kartoffan.labinventory.model.Category;
import com.kartoffan.labinventory.service.category.CategoryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Categories", description = "Endpoints for managing item categories in the lab inventory")
@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {
  
  private final CategoryService categoryService;

  @Operation(summary = "Create a category")
  @PostMapping
  @PreAuthorize("hasAuthority('CATEGORY_WRITE')")
  public ResponseEntity<CategoryResponse> create(@Valid @RequestBody CreateCategoryRequest request) {
    Category category = categoryService.create(request);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(CategoryResponse.fromEntity(category));
  }

  @Operation(summary = "Get all categories")
  @GetMapping
  @PreAuthorize("hasAuthority('CATEGORY_READ')")
  public ResponseEntity<Page<CategoryResponse>> getAll(
          @RequestParam(required = false) Boolean active,
          @RequestParam(required = false) String search,
          @PageableDefault(size = 20, sort = "createdAt") Pageable pageable
  ) {
    String normalizedSearch = (search == null || search.isBlank()) ? null : search.trim();
    Page<Category> categories = categoryService.getAll(active, normalizedSearch, pageable);
    return ResponseEntity.ok()
        .body(categories.map(category -> CategoryResponse.fromEntity(category)));
  }

  @Operation(summary = "Get category by ID")
  @GetMapping("/{categoryId}")
  @PreAuthorize("hasAuthority('CATEGORY_READ')")
  public ResponseEntity<CategoryResponse> getById(@PathVariable UUID categoryId) {
    Category category = categoryService.getById(categoryId);
    return ResponseEntity.ok()
        .body(CategoryResponse.fromEntity(category));
  }

  @Operation(summary = "Update a category")
  @PutMapping("/{categoryId}")
  @PreAuthorize("hasAuthority('CATEGORY_WRITE')")
  public ResponseEntity<CategoryResponse> update(@PathVariable UUID categoryId,
        @RequestBody UpdateCategoryRequest request) {
    
    Category updated = categoryService.update(categoryId, request);
    return ResponseEntity.ok()
        .body(CategoryResponse.fromEntity(updated));
  }

  @Operation(summary = "Archive a category")
  @PatchMapping("/{categoryId}/archive")
  @PreAuthorize("hasAuthority('CATEGORY_WRITE')")
  public ResponseEntity<?> archive(@PathVariable UUID categoryId) {
    categoryService.archive(categoryId);
    return ResponseEntity.ok().build();
  }

  @Operation(summary = "Unarchive a category")
  @PatchMapping("/{categoryId}/unarchive")
  @PreAuthorize("hasAuthority('CATEGORY_WRITE')")
  public ResponseEntity<?> unarchive(@PathVariable UUID categoryId) {
    categoryService.unarchive(categoryId);
    return ResponseEntity.ok().build();
  }
}
