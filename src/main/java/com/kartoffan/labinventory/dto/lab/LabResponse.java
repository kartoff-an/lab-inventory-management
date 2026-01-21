package com.kartoffan.labinventory.dto.lab;

import java.time.LocalDateTime;
import java.util.UUID;

import com.kartoffan.labinventory.model.Lab;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LabResponse {
  private UUID id;
  private String name;
  private String location;
  private String description;
  private String contactEmail;
  private String phone;
  private Boolean isActive;
  private UUID managerId;
  private String managerName;
  private LocalDateTime createdAt;

  public static LabResponse fromEntity(Lab lab) {
    return LabResponse.builder()
        .id(lab.getId())
        .name(lab.getName())
        .location(lab.getLocation())
        .description(lab.getDescription())
        .contactEmail(lab.getContactEmail())
        .phone(lab.getPhone())
        .isActive(true)
        .managerId((lab.getManager() != null)
            ? lab.getManager().getId()
            : null
        )
        .managerName(
          (lab.getManager() != null)
          ? lab.getManager().getFirstName()
          : null
        )
        .createdAt(LocalDateTime.now())
        .build();
  }
}
