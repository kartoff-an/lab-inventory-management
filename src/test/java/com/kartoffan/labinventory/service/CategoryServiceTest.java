package com.kartoffan.labinventory.service;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;

import com.kartoffan.labinventory.dto.category.CreateCategoryRequest;
import com.kartoffan.labinventory.dto.category.UpdateCategoryRequest;
import com.kartoffan.labinventory.exception.ResourceAlreadyExistsException;
import com.kartoffan.labinventory.exception.ResourceNotFoundException;
import com.kartoffan.labinventory.model.Category;
import com.kartoffan.labinventory.model.Lab;
import com.kartoffan.labinventory.model.User;
import com.kartoffan.labinventory.repository.CategoryRepository;
import com.kartoffan.labinventory.service.category.CategoryServiceImpl;
import com.kartoffan.labinventory.service.lab.LabService;
import com.kartoffan.labinventory.service.user.UserService;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

  @Mock
  private CategoryRepository categoryRepository;

  @Mock
  private UserService userService;

  @Mock
  private LabService labService;

  @InjectMocks
  private CategoryServiceImpl categoryService;

  private UUID categoryId;
  private UUID labId;
  private User manager;
  private Lab lab;
  private Category category;

  @BeforeEach
  void setUp() {
    categoryId = UUID.randomUUID();
    labId = UUID.randomUUID();

    manager = new User();
    lab = new Lab();
    lab.setManager(manager);

    category = Category.builder()
        .id(categoryId)
        .name("Chemicals")
        .lab(lab)
        .isActive(true)
        .build();
  }

  @Test
  void createCategory_success() {
    CreateCategoryRequest request = new CreateCategoryRequest();
    request.setName("Chemicals");
    request.setDescription("Lab chemicals");
    request.setLabId(labId);

    when(categoryRepository.existsByName("Chemicals")).thenReturn(false);
    when(labService.getById(labId)).thenReturn(lab);
    when(userService.getCurrentUser()).thenReturn(manager);
    when(categoryRepository.save(any(Category.class))).thenAnswer(i -> i.getArgument(0));

    Category result = categoryService.create(request);

    assertEquals("Chemicals", result.getName());
    assertEquals(lab, result.getLab());
    verify(categoryRepository).save(any(Category.class));
  }

  @Test
  void createCategory_duplicateName_throwsException() {
    CreateCategoryRequest request = new CreateCategoryRequest();
    request.setName("Chemicals");

    when(categoryRepository.existsByName("Chemicals")).thenReturn(true);

    assertThrows(ResourceAlreadyExistsException.class,
        () -> categoryService.create(request));
  }

  @Test
  void createCategory_notLabManager_throwsAccessDenied() {
    CreateCategoryRequest request = new CreateCategoryRequest();
    request.setName("Chemicals");
    request.setLabId(labId);

    User otherUser = new User();

    when(categoryRepository.existsByName("Chemicals")).thenReturn(false);
    when(labService.getById(labId)).thenReturn(lab);
    when(userService.getCurrentUser()).thenReturn(otherUser);

    assertThrows(AccessDeniedException.class,
        () -> categoryService.create(request));
  }

  @Test
  void getById_success() {
    when(categoryRepository.findById(categoryId))
        .thenReturn(Optional.of(category));

    Category result = categoryService.getById(categoryId);

    assertNotNull(result);
    assertEquals(categoryId, result.getId());
  }

  @Test
  void getById_notFound_throwsException() {
    when(categoryRepository.findById(categoryId))
        .thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class,
        () -> categoryService.getById(categoryId));
  }

  @Test
  void getAll_returnsPage() {
    Pageable pageable = PageRequest.of(0, 10);
    Page<Category> page = new PageImpl<>(List.of(category));

    when(categoryRepository.findAllFiltered(
        true,
        "chem",
        pageable
    )).thenReturn(page);

    Page<Category> result = categoryService.getAll(true, "chem", pageable);

    assertEquals(1, result.getTotalElements());
    verify(categoryRepository).findAllFiltered(true, "chem", pageable);
  }

  @Test
  void getAll_blankSearch_isNormalizedToNull() {
    Pageable pageable = PageRequest.of(0, 10);
    Page<Category> page = Page.empty();

    when(categoryRepository.findAllFiltered(
        null,
        null,
        pageable)).thenReturn(page);

    categoryService.getAll(null, "   ", pageable);

    verify(categoryRepository)
        .findAllFiltered(null, null, pageable);
  }

  @Test
  void updateCategory_success() {
    UpdateCategoryRequest request = new UpdateCategoryRequest();
    request.setName("Updated Name");

    when(categoryRepository.findByIdAndIsActiveTrue(categoryId))
        .thenReturn(Optional.of(category));
    when(categoryRepository.existsByName("Updated Name")).thenReturn(false);
    when(userService.getCurrentUser()).thenReturn(manager);
    when(categoryRepository.save(any(Category.class))).thenAnswer(i -> i.getArgument(0));

    Category updated = categoryService.update(categoryId, request);

    assertEquals("Updated Name", updated.getName());
  }

  @Test
  void updateCategory_notFound_throwsException() {
    when(categoryRepository.findByIdAndIsActiveTrue(categoryId))
        .thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class,
        () -> categoryService.update(categoryId, new UpdateCategoryRequest()));
  }
  
  @Test
  void archiveCategory_success() {
    when(categoryRepository.findByIdAndIsActiveTrue(categoryId))
        .thenReturn(Optional.of(category));
    when(userService.getCurrentUser()).thenReturn(manager);

    categoryService.archive(categoryId);

    assertFalse(category.getIsActive());
  }

  @Test
  void archiveCategory_notFound_throwsException() {
    when(categoryRepository.findByIdAndIsActiveTrue(categoryId))
        .thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class,
          () -> categoryService.archive(categoryId)
    );
  }

  @Test
  void unarchiveCategory_success() {
    category.setIsActive(true);

    when(categoryRepository.findByIdAndIsActiveFalse(categoryId))
        .thenReturn(Optional.of(category));
    when(userService.getCurrentUser()).thenReturn(manager);

    categoryService.unarchive(categoryId);

    assertTrue(category.getIsActive());
  }

  @Test
  void unarchiveCategory_notFound_throwsException() {
    when(categoryRepository.findByIdAndIsActiveFalse(categoryId))
        .thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class,
          () -> categoryService.unarchive(categoryId)
    );
  }

}