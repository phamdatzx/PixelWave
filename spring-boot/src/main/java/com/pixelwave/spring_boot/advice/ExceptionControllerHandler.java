package com.pixelwave.spring_boot.advice;

import com.pixelwave.spring_boot.DTO.error.ErrorDTO;
import com.pixelwave.spring_boot.exception.*;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class ExceptionControllerHandler {
  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ErrorDTO> handleResourceNotFoundException(ResourceNotFoundException e) {
    return ResponseEntity.status(404).body(new ErrorDTO(e.getMessage(), LocalDateTime.now()));
  }

  @ExceptionHandler(ValidationException.class)
  public ResponseEntity<ErrorDTO> handleValidationException(ValidationException e) {
    return ResponseEntity.badRequest().body(new ErrorDTO(e.getMessage(), LocalDateTime.now()));
  }

  @ExceptionHandler(ConflictException.class)
  public ResponseEntity<ErrorDTO> handleConflictException(ConflictException e) {
    return ResponseEntity.status(409).body(new ErrorDTO(e.getMessage(), LocalDateTime.now()));
  }

  @ExceptionHandler(ForbiddenException.class)
  public ResponseEntity<ErrorDTO> handleForbiddenException(ForbiddenException e) {
    return ResponseEntity.status(403).body(new ErrorDTO(e.getMessage(), LocalDateTime.now()));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<String> handleException(Exception e) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
  }

  @ExceptionHandler(SignatureException.class)
  public ResponseEntity<ErrorDTO> handleSignatureException(SignatureException e) {
    return ResponseEntity.status(401).body(new ErrorDTO("Invalid JWT signature", LocalDateTime.now()));
  }

  @ExceptionHandler(ExpiredJwtException.class)
  public ResponseEntity<ErrorDTO> handleExpiredJwtException(ExpiredJwtException e) {
    return ResponseEntity.status(401).body(new ErrorDTO("Token is expired", LocalDateTime.now()));
  }
}
