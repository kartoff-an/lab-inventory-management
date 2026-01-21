package com.kartoffan.labinventory.service.category;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kartoffan.labinventory.dto.category.*;
import com.kartoffan.labinventory.exception.ResourceAlreadyExistsException;
import com.kartoffan.labinventory.exception.ResourceNotFoundException;
import com.kartoffan.labinventory.model.Category;
import com.kartoffan.labinventory.model.Lab;
import com.kartoffan.labinventory.model.User;
import com.kartoffan.labinventory.repository.CategoryRepository;
import com.kartoffan.labinventory.service.lab.LabService;
import com.kartoffan.labinventory.service.user.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryServiceImpl implements CategoryService {
  
  private final CategoryRepository categoryRepository;

  private final UserService userService;
  private final LabService labService;

  /**
   * Creates a new inventory category.
   */
  @Override
  public Category create(CreateCategoryRequest request) {
    validateUniqueCategory(request.getName());

    Category parent = null;
    if (request.getParentCategoryId() != null) {
      parent = getActiveCategory(request.getParentCategoryId());
    }

    Lab lab = labService.getById(request.getLabId());
    checkAuthorization(lab);

    Category category = Category.builder()
        .name(request.getName())
        .description(request.getDescription())
        .lab(lab)
        .parentCategory(parent)
        .build();

    return categoryRepository.save(category);
  }
  
  /**
   * Retrieves a paginated list of categories with optional filtering.
   */
  @Override
  public Page<Category> getAll(Boolean active, String search, Pageable pageable) {
    String normalized = (search == null || search.isBlank()) ? null : search.trim();
    return categoryRepository.findAllFiltered(active, normalized, pageable);
  }

  /**
   * Fetches a single category by its unique ID
   */
  @Override
  public Category getById(UUID categoryId) {
    return categoryRepository.findById(categoryId)
        .orElseThrow(() -> new ResourceNotFoundException("No active category found with id " + categoryId));
  }

  /**
   * Updates an existing category's details
   */
  @Override
  public Category update(UUID categoryId, UpdateCategoryRequest request) {
    Category category = getActiveCategory(categoryId);

    checkAuthorization(category.getLab());

    if (request.getName() != null && !request.getName().equals(category.getName())) {
      validateUniqueCategory(request.getName());
      category.setName(request.getName());
    }

    if (request.getDescription() != null)
      category.setDescription(request.getDescription());

    if (request.getParentCategoryId() != null) {
      Category parent = getActiveCategory(request.getParentCategoryId());
      category.setParentCategory(parent);
    }

    return categoryRepository.save(category);
  }
  
  /**
   * Perform a soft-delete
   */
  @Override
  public void archive(UUID categoryId) {
    Category category = categoryRepository.findByIdAndIsActiveTrue(categoryId)
        .orElseThrow(() -> new ResourceNotFoundException("No active category found with id " + categoryId));
    
    checkAuthorization(category.getLab());
      
    category.setIsActive(false);
  }

  /**
   * Restores an archived category to active status
   */
  @Override
  public void unarchive(UUID categoryId) {
    Category category = categoryRepository.findByIdAndIsActiveFalse(categoryId)
        .orElseThrow(() -> new ResourceNotFoundException("No archived category found with id " + categoryId));

    checkAuthorization(category.getLab());
    category.setIsActive(true);
  }

  private void validateUniqueCategory(String name) {
    if (categoryRepository.existsByName(name)) {
      throw new ResourceAlreadyExistsException("Category with name '" + name + "' already exists");
    }
  }

  private Category getActiveCategory(UUID categoryId) {
    return categoryRepository.findByIdAndIsActiveTrue(categoryId)
        .orElseThrow(() -> new ResourceNotFoundException("No active category found with id " + categoryId));
  }

  private void checkAuthorization(Lab lab) {
    User user = userService.getCurrentUser();
    System.out.println(user);
    if (lab.getManager() != user) {
      throw new AccessDeniedException(null);
    }
  }
}
