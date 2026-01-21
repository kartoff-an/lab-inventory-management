package com.kartoffan.labinventory.exception;

import org.springframework.http.HttpStatus;

public class InvalidCredentialsException extends ApiException {
  
  public InvalidCredentialsException(String message) {
    super(
      "INVALID_CREDENTIALS",
      message,
      HttpStatus.BAD_REQUEST
    );
  }
}
