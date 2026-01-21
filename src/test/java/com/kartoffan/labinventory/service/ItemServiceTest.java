package com.kartoffan.labinventory.service;

import static org.junit.jupiter.api.Assertions.*;
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
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.kartoffan.labinventory.dto.item.CreateItemRequest;
import com.kartoffan.labinventory.dto.item.ItemFilter;
import com.kartoffan.labinventory.dto.item.UpdateItemRequest;
import com.kartoffan.labinventory.exception.ResourceAlreadyExistsException;
import com.kartoffan.labinventory.exception.ResourceNotFoundException;
import com.kartoffan.labinventory.model.Category;
import com.kartoffan.labinventory.model.Item;
import com.kartoffan.labinventory.model.Lab;
import com.kartoffan.labinventory.repository.ItemRepository;
import com.kartoffan.labinventory.service.category.CategoryService;
import com.kartoffan.labinventory.service.item.ItemServiceImpl;
import com.kartoffan.labinventory.service.lab.LabService;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {
  
  @Mock
  private ItemRepository itemRepository;

  @Mock
  private LabService labService;

  @Mock
  private CategoryService categoryService;

  @InjectMocks
  private ItemServiceImpl itemService;

  private UUID itemId;
  private UUID labId;
  private UUID categoryId;

  private Lab lab;
  private Category category;
  private Item item;

  @BeforeEach
  void setup() {
    itemId = UUID.randomUUID();
    labId = UUID.randomUUID();
    categoryId = UUID.randomUUID();

    lab = new Lab();
    category = new Category();

    item = Item.builder()
        .id(itemId)
        .name("Ethanol")
        .lab(lab)
        .category(category)
        .isActive(true)
        .build();
  }

   @Test
   void createItem_success() {
     CreateItemRequest request = new CreateItemRequest();
     request.setName("Ethanol");
     request.setCode("ETH-001");
     request.setLabId(labId);
     request.setCategoryId(categoryId);

     when(itemRepository.existsByNameAndLabId("Ethanol", labId)).thenReturn(false);
     when(labService.getById(labId)).thenReturn(lab);
     when(categoryService.getById(categoryId)).thenReturn(category);
     when(itemRepository.save(any(Item.class))).thenAnswer(i -> i.getArgument(0));

     Item result = itemService.create(request);

     assertEquals("Ethanol", result.getName());
     assertEquals(lab, result.getLab());
     assertEquals(category, result.getCategory());
     verify(itemRepository).save(any(Item.class));
   }
  
  @Test
  void createItem_duplicateName_throwsException() {
    CreateItemRequest request = new CreateItemRequest();
    request.setName("Ethanol");
    request.setLabId(labId);

    when(itemRepository.existsByNameAndLabId("Ethanol", labId)).thenReturn(true);

    assertThrows(ResourceAlreadyExistsException.class,
        () -> itemService.create(request));
  }

  @Test
  void getItemById_success() {
    when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

    Item result = itemService.getById(itemId);

    assertEquals(item, result);
  }

  @Test
  void getItemById_notFound_throwsException() {
    when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class,
        () -> itemService.getById(itemId));
  }

  @Test
  void updateItem_success() {
    UpdateItemRequest request = new UpdateItemRequest();
    request.setName("Updated Ethanol");
    request.setCode("ETH-002");

    when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

    Item updated = itemService.update(itemId, request);

    assertEquals("Updated Ethanol", updated.getName());
    assertEquals("ETH-002", updated.getCode());
  }

  @Test
  void archiveItem_success() {
    when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

    itemService.archive(itemId);

    assertFalse(item.getIsActive());
  }

  @Test
  void unarchiveItem_success() {
    item.setIsActive(false);
    when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

    itemService.unarchive(itemId);

    assertTrue(item.getIsActive());
  }

  @Test
  void getAllItems_delegatesToRepository() {
    ItemFilter filter = new ItemFilter();
    Pageable pageable = Pageable.unpaged();

    Page<Item> page = new PageImpl<>(List.of(item));

    when(itemRepository.findAll(argThat((Specification<Item> spec) -> true), eq(pageable))).thenReturn(page);

    Page<Item> result = itemService.getAll(filter, pageable);

    assertEquals(1, result.getTotalElements());
    verify(itemRepository).findAll(argThat((Specification<Item> spec) -> true), eq(pageable));
  }
}
