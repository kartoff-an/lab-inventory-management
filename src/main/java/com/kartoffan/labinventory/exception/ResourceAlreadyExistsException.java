package com.kartoffan.labinventory.exception;

import org.springframework.http.HttpStatus;

public class ResourceAlreadyExistsException extends ApiException {
  
  public ResourceAlreadyExistsException(String message) {
    super(
        "RESOURCE_CONFLICT",
        message,
        HttpStatus.CONFLICT
    );
  }
}
