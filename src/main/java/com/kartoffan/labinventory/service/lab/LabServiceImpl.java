package com.kartoffan.labinventory.service.lab;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kartoffan.labinventory.dto.lab.LabRequest;
import com.kartoffan.labinventory.exception.ResourceAlreadyExistsException;
import com.kartoffan.labinventory.exception.ResourceNotFoundException;
import com.kartoffan.labinventory.model.Lab;
import com.kartoffan.labinventory.model.User;
import com.kartoffan.labinventory.repository.LabRepository;
import com.kartoffan.labinventory.service.user.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class LabServiceImpl implements LabService {
  
  private final LabRepository labRepository;
  
  private final UserService userService;

  /**
   * Creates a new laboratory
   */
  @Override
  public Lab create(LabRequest request) {
    validateUniqueLab(request.getName(), request.getLocation());

    User manager = null;
    if (request.getManagerId() != null) {
      manager = userService.getById(request.getManagerId());
    }

    Lab lab = Lab.builder()
        .name(request.getName())
        .location(request.getLocation())
        .description(request.getDescription())
        .contactEmail(request.getContactEmail())
        .phone(request.getPhone())
        .manager(manager)
        .build();

    return labRepository.save(lab);
  }

  /**
   * Returns a paginated list of all labs in the system
   */
  @Override
  public Page<Lab> getAll(Boolean active, String search, Pageable pageable) {
    String normalized = (search == null || search.isBlank()) ? null : search.trim();
    return labRepository.findAllFiltered(active, normalized, pageable);
  }

  /**
   * Fetches a single lab in the system with its unique ID
   */
  @Override
  public Lab getById(UUID labId){
    return labRepository.findByIdAndIsActiveTrue(labId)
        .orElseThrow(() -> new ResourceNotFoundException("No active lab found with id " + labId));
  }

  /**
   * Updates an existing lab's details
   */
  @Override
  public Lab update(UUID labId, LabRequest request) {
    Lab lab = getById(labId);
    
    if (!lab.getName().equals(request.getName())) {
      validateUniqueLab(request.getName(), request.getLocation());
    }

    lab.setName(request.getName());
    lab.setLocation(request.getLocation());
    lab.setDescription(request.getDescription());
    lab.setContactEmail(request.getContactEmail());
    lab.setPhone(request.getPhone());

    if (request.getManagerId() != null) {
      lab.setManager(userService.getById(request.getManagerId()));
    }

    return lab;
  }

  /**
   * Perform a soft-delete
   */
  @Override
  public void archive(UUID labId) {
    Lab lab = labRepository.findByIdAndIsActiveTrue(labId)
        .orElseThrow(() -> new ResourceNotFoundException("No active lab found with id " + labId));
      
    lab.setIsActive(false);
  }

  /**
   * Restore an archived lab to an active status
   */
  @Override
  public void unarchive(UUID labId) {
    Lab lab = labRepository.findByIdAndIsActiveFalse(labId)
        .orElseThrow(() -> new ResourceNotFoundException("No inactive lab found with id " + labId));
    lab.setIsActive(true);
  }

  /**
   * Assigns a new manager for the lab
   */
  @Override
  public void assignManager(UUID labId, UUID userId) {
    Lab lab = getById(labId);
    lab.setManager(userService.getById(userId));
  }

  private void validateUniqueLab(String name, String location) {
    if (labRepository.existsByNameAndLocationAndIsActiveTrue(name, location)) {
      throw new ResourceAlreadyExistsException("Lab already exists in this location");
    }
  }
}
