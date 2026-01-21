package com.kartoffan.labinventory.service.lab;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.kartoffan.labinventory.dto.lab.LabRequest;
import com.kartoffan.labinventory.model.Lab;

public interface LabService {
  Lab create(LabRequest request);

  Page<Lab> getAll(Boolean active, String search, Pageable pageable);

  Lab getById(UUID labId);

  Lab update(UUID labId, LabRequest request);

  void archive(UUID labId);

  void unarchive(UUID labId);

  void assignManager(UUID labId, UUID userId);
}
