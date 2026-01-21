package com.kartoffan.labinventory.controller;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.kartoffan.labinventory.dto.lab.*;
import com.kartoffan.labinventory.model.Lab;
import com.kartoffan.labinventory.service.lab.LabService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Labs", description = "Endpoints for managing laboratories and assigning lab managers")
@RestController
@RequestMapping("/api/v1/labs")
@PreAuthorize("hasAnyRole('SUPER_ADMIN')")
@RequiredArgsConstructor
public class LabController {
  
  private final LabService labService;

  @Operation(summary = "Create a laboratory")
  @PostMapping
  @PreAuthorize("hasAuthority('LAB_WRITE')")
  public ResponseEntity<LabResponse> create(@Valid @RequestBody LabRequest request) {
    Lab lab = labService.create(request);
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(LabResponse.fromEntity(lab));
  }

  @Operation(summary = "Get all laboratories")
  @GetMapping
  @PreAuthorize("hasAuthority('LAB_READ')")
  public ResponseEntity<Page<LabResponse>> getAll(
      @RequestParam(required = false) Boolean active,
      @RequestParam(required = false) String search,
      @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
    
    String normalizedSearch = (search == null || search.isBlank()) ? null : search.trim();
    Page<Lab> labs = labService.getAll(active, normalizedSearch, pageable);
    return ResponseEntity.ok()
        .body(labs.map(lab -> LabResponse.fromEntity(lab)));
  }

  @Operation(summary = "Get laboratory by ID")
  @GetMapping("/{labId}")
  @PreAuthorize("hasAuthority('LAB_READ')")
  public ResponseEntity<LabResponse> getById(@PathVariable UUID labId) {
    Lab lab = labService.getById(labId);
    return ResponseEntity.ok()
        .body(LabResponse.fromEntity(lab));
  }

  @Operation(summary = "Update a laboratory")
  @PutMapping("/{labId}")
  @PreAuthorize("hasAuthority('LAB_WRITE')")
  public ResponseEntity<LabResponse> update(@PathVariable UUID labId, @Valid @RequestBody LabRequest request) {
    Lab updatedLab = labService.update(labId, request);
    return ResponseEntity.ok()
        .body(LabResponse.fromEntity(updatedLab));
  }

  @Operation(summary = "Archive a laboratory")
  @PatchMapping("/{labId}/archive")
  @PreAuthorize("hasAuthority('LAB_WRITE')")
  public ResponseEntity<?> archive(@PathVariable UUID labId) {
    labService.archive(labId);
    return ResponseEntity.ok().build();
  }

  @Operation(summary = "Unarchive a laboratory")
  @PatchMapping("/{labId}/unarchive")
  @PreAuthorize("hasAuthority('LAB_WRITE')")
  public ResponseEntity<?> unarchive(@PathVariable UUID labId) {
    labService.unarchive(labId);
    return ResponseEntity.ok().build();
  }
  
  @Operation(summary = "Assign laboratory manager")
  @PatchMapping("/{labId}/manager")
  @PreAuthorize("hasAuthority('LAB_WRITE')")
  public ResponseEntity<?> assignManager(@PathVariable UUID labId, @RequestParam UUID managerId) {
    labService.assignManager(labId, managerId);
    return ResponseEntity.ok().build();
  }
}
