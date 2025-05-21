package com.pixelwave.spring_boot.exception;

public class ConflictException extends RuntimeException {

  public ConflictException(String message) {
    super(message);
  }

}
