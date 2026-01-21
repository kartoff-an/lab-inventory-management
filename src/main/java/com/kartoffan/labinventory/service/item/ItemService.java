package com.kartoffan.labinventory.service.item;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.kartoffan.labinventory.dto.item.CreateItemRequest;
import com.kartoffan.labinventory.dto.item.ItemFilter;
import com.kartoffan.labinventory.dto.item.UpdateItemRequest;
import com.kartoffan.labinventory.model.Item;

public interface ItemService {
  
  Item create(CreateItemRequest request);

  Page<Item> getAll(ItemFilter filter, Pageable pageable);

  Item getById(UUID itemId);

  Item update(UUID itemId, UpdateItemRequest request);

  void archive(UUID itemId);

  void unarchive(UUID itemId);
}
