package com.kartoffan.labinventory.api;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiError {
  private String code;
  private String message;
  private List<String> details;
  private LocalDateTime timestamp;
}
