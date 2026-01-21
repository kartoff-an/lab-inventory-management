package com.kartoffan.labinventory.service.item;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kartoffan.labinventory.dto.item.*;
import com.kartoffan.labinventory.exception.ResourceAlreadyExistsException;
import com.kartoffan.labinventory.exception.ResourceNotFoundException;
import com.kartoffan.labinventory.model.Category;
import com.kartoffan.labinventory.model.Item;
import com.kartoffan.labinventory.model.Lab;
import com.kartoffan.labinventory.repository.ItemRepository;
import com.kartoffan.labinventory.repository.spec.ItemSpecifications;
import com.kartoffan.labinventory.service.category.CategoryService;
import com.kartoffan.labinventory.service.lab.LabService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemServiceImpl implements ItemService {
  
  private final ItemRepository itemRepository;

  private final LabService labService;
  private final CategoryService categoryService;

  /**
   * Creates a new item in the lab
   */
  @Override
  public Item create(CreateItemRequest request) {
    validateUniqueItem(request.getName(), request.getLabId());

    Lab lab = labService.getById(request.getLabId());
    Category category = categoryService.getById(request.getCategoryId());

    Item item = Item.builder()
        .name(request.getName())
        .code(request.getCode())
        .lab(lab)
        .category(category)
        .unit(request.getUnit())
        .reorderLevel(request.getReorderLevel())
        .maxQuantity(request.getMaxQuantity())
        .location(request.getLocation())
        .storageCondition(request.getStorageCondition())
        .build();

    return itemRepository.save(item);
  }

  /**
   * Fetches a single item with its unique ID
   */
  @Override
  public Item getById(UUID itemId) {
    return itemRepository.findById(itemId)
        .orElseThrow(() -> new ResourceNotFoundException("No item found with id " + itemId));
  }

  /**
   * Returns a paginated list of all items with optional filtering
   */
  @Override
  public Page<Item> getAll(ItemFilter filter, Pageable pageable) {
    return itemRepository.findAll(ItemSpecifications.fromFilter(filter), pageable);
  }

  /**
   * Updates an existing item's details
   */
  @Override
  public Item update(UUID itemId, UpdateItemRequest request) {
    Item item = this.getById(itemId);

    item.setName(request.getName());
    item.setCode(request.getCode());
    item.setUnit(request.getUnit());
    item.setReorderLevel(request.getReorderLevel());
    item.setMinQuantity(request.getMinQuantity());
    item.setMaxQuantity(request.getMaxQuantity());
    item.setLocation(request.getLocation());
    item.setStorageCondition(request.getStorageCondition());

    return item;
  }

  /**
   * Performs a soft-delete
   */
  @Override
  public void archive(UUID itemId) {
    Item item = this.getById(itemId);
    item.setIsActive(false);
  }

  /**
   * Restore an unarchived item to an active status
   */
  @Override
  public void unarchive(UUID itemId) {
    Item item = this.getById(itemId);
    item.setIsActive(true);
  }

  private void validateUniqueItem(String name, UUID labId) {
    if (itemRepository.existsByNameAndLabId(name, labId)) {
      throw new ResourceAlreadyExistsException("Item already exists in this lab");
    }
  }
}
