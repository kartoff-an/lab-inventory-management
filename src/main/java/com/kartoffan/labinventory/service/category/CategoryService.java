package com.kartoffan.labinventory.service.category;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.kartoffan.labinventory.dto.category.CreateCategoryRequest;
import com.kartoffan.labinventory.dto.category.UpdateCategoryRequest;
import com.kartoffan.labinventory.model.Category;

public interface CategoryService {
  Category create(CreateCategoryRequest request);

  Page<Category> getAll(Boolean active, String search, Pageable pageable);

  Category getById(UUID categoryId);

  Category update(UUID categoryId, UpdateCategoryRequest request);

  void archive(UUID categoryId);

  void unarchive(UUID categoryId);
}
