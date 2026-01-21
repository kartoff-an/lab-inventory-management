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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.kartoffan.labinventory.dto.lab.LabRequest;
import com.kartoffan.labinventory.exception.ResourceAlreadyExistsException;
import com.kartoffan.labinventory.exception.ResourceNotFoundException;
import com.kartoffan.labinventory.model.Lab;
import com.kartoffan.labinventory.model.User;
import com.kartoffan.labinventory.repository.LabRepository;
import com.kartoffan.labinventory.service.lab.LabServiceImpl;
import com.kartoffan.labinventory.service.user.UserService;

@ExtendWith(MockitoExtension.class)
public class LabServiceTest {
  
  @Mock
  private LabRepository labRepository;

  @Mock
  private UserService userService;

  @InjectMocks
  private LabServiceImpl labService;

  private UUID labId;
  private UUID managerId;
  private Lab lab;
  private User manager;

  @BeforeEach
  void setUp() {
    labId = UUID.randomUUID();
    managerId = UUID.randomUUID();

    manager = new User();
    manager.setId(managerId);

    lab = Lab.builder()
        .id(labId)
        .name("Chem lab")
        .location("BUilding A")
        .isActive(true)
        .build();
  }

  @Test
  void createLab_success() {
    LabRequest request = new LabRequest();
    request.setName("Chem Lab");
    request.setLocation("Building A");

    when(labRepository.existsByNameAndLocationAndIsActiveTrue("Chem Lab", "Building A"))
        .thenReturn(false);
    when(labRepository.save(any(Lab.class)))
        .thenAnswer(i -> i.getArgument(0));

    Lab result = labService.create(request);

    assertEquals("Chem Lab", result.getName());
    assertEquals("Building A", result.getLocation());
  }

  @Test
  void createLab_withManager_success() {
    LabRequest request = new LabRequest();
    request.setName("Chem Lab");
    request.setLocation("Building A");
    request.setManagerId(managerId);

    when(labRepository.existsByNameAndLocationAndIsActiveTrue(any(), any()))
        .thenReturn(false);
    when(userService.getById(managerId))
        .thenReturn(manager);
    when(labRepository.save(any(Lab.class)))
        .thenAnswer(i -> i.getArgument(0));

    Lab result = labService.create(request);

    assertEquals(manager, result.getManager());
  }

  @Test
  void createLab_duplicate_throwsException() {
    LabRequest request = new LabRequest();
    request.setName("Chem Lab");
    request.setLocation("Building A");

    when(labRepository.existsByNameAndLocationAndIsActiveTrue(any(), any()))
        .thenReturn(true);

    assertThrows(ResourceAlreadyExistsException.class,
        () -> labService.create(request));
  }

  @Test
  void getById_success() {
    when(labRepository.findByIdAndIsActiveTrue(labId))
        .thenReturn(Optional.of(lab));

    Lab result = labService.getById(labId);

    assertEquals(labId, result.getId());
  }

  @Test
  void getById_notFound_throwsException() {
    when(labRepository.findByIdAndIsActiveTrue(labId))
        .thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class,
        () -> labService.getById(labId));
  }

  @Test
  void getAll_returnsPage() {
    Pageable pageable = PageRequest.of(0, 10);
    Page<Lab> page = new PageImpl<>(List.of(lab));

    when(labRepository.findAllFiltered(true, "chem", pageable))
        .thenReturn(page);

    Page<Lab> result = labService.getAll(true, "chem", pageable);

    assertEquals(1, result.getTotalElements());
  }

  @Test
  void getAll_blankSearch_normalizedToNull() {
    Pageable pageable = PageRequest.of(0, 10);

    labService.getAll(null, "   ", pageable);

    verify(labRepository)
        .findAllFiltered(null, null, pageable);
  }

  @Test
  void updateLab_success() {
    LabRequest request = new LabRequest();
    request.setName("Updated Lab");
    request.setLocation("Building B");

    when(labRepository.findByIdAndIsActiveTrue(labId))
        .thenReturn(Optional.of(lab));
    when(labRepository.existsByNameAndLocationAndIsActiveTrue("Updated Lab", "Building B"))
        .thenReturn(false);

    Lab result = labService.update(labId, request);

    assertEquals("Updated Lab", result.getName());
    assertEquals("Building B", result.getLocation());
  }

  @Test
  void updateLab_notFound_throwsException() {
    when(labRepository.findByIdAndIsActiveTrue(labId))
        .thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class,
        () -> labService.update(labId, new LabRequest()));
  }

  @Test
  void archiveLab_success() {
    when(labRepository.findByIdAndIsActiveTrue(labId))
        .thenReturn(Optional.of(lab));

    labService.archive(labId);

    assertFalse(lab.getIsActive());
  }

  @Test
  void archiveLab_notFound_throwsException() {
    when(labRepository.findByIdAndIsActiveTrue(labId))
        .thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class,
        () -> labService.archive(labId));
  }

  @Test
  void unarchiveLab_success() {
    lab.setIsActive(false);

    when(labRepository.findByIdAndIsActiveFalse(labId))
        .thenReturn(Optional.of(lab));

    labService.unarchive(labId);

    assertTrue(lab.getIsActive());
  }

  @Test
  void assignManager_success() {
    when(labRepository.findByIdAndIsActiveTrue(labId))
        .thenReturn(Optional.of(lab));
    when(userService.getById(managerId))
        .thenReturn(manager);

    labService.assignManager(labId, managerId);

    assertEquals(manager, lab.getManager());
  }
}
