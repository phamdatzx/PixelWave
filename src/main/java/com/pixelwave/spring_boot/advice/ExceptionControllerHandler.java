package com.pixelwave.spring_boot.advice;

import com.pixelwave.spring_boot.DTO.error.ErrorDTO;
import com.pixelwave.spring_boot.exception.*;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ExceptionControllerHandler {

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getAllErrors().forEach((error) -> {
      String fieldName = ((FieldError) error).getField();
      String errorMessage = error.getDefaultMessage();
      errors.put(fieldName, errorMessage);
    });
    return ResponseEntity.badRequest().body(errors);
  }

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ErrorDTO> handleResourceNotFoundException(ResourceNotFoundException e, HttpServletRequest request) {
    return ResponseEntity.status(404)
            .body(new ErrorDTO(404, e.getMessage(), e.getClass().getName(), request.getRequestURI()));
  }

  @ExceptionHandler(ValidationException.class)
  public ResponseEntity<ErrorDTO> handleValidationException(ValidationException e, HttpServletRequest request) {
    return ResponseEntity.status(400)
            .body(new ErrorDTO(400, e.getMessage(), e.getClass().getName(), request.getRequestURI()));
  }

  @ExceptionHandler(ConflictException.class)
  public ResponseEntity<ErrorDTO> handleConflictException(ConflictException e, HttpServletRequest request) {
    return ResponseEntity.status(409)
            .body(new ErrorDTO(409, e.getMessage(), e.getClass().getName(), request.getRequestURI()));
  }

  @ExceptionHandler(ForbiddenException.class)
  public ResponseEntity<ErrorDTO> handleForbiddenException(ForbiddenException e, HttpServletRequest request) {
    return ResponseEntity.status(403)
            .body(new ErrorDTO(403, e.getMessage(), e.getClass().getName(), request.getRequestURI()));
  }

  @ExceptionHandler(SignatureException.class)
  public ResponseEntity<ErrorDTO> handleSignatureException(SignatureException e, HttpServletRequest request) {
    return ResponseEntity.status(401)
            .body(new ErrorDTO(401, "Invalid JWT signature", e.getClass().getName(), request.getRequestURI()));
  }

  @ExceptionHandler(ExpiredJwtException.class)
  public ResponseEntity<ErrorDTO> handleExpiredJwtException(ExpiredJwtException e, HttpServletRequest request) {
    return ResponseEntity.status(401)
            .body(new ErrorDTO(401, "Token is expired", e.getClass().getName(), request.getRequestURI()));
  }

  @ExceptionHandler(UnauthorizedException.class)
  public ResponseEntity<ErrorDTO> handleUnauthorizedException(UnauthorizedException e, HttpServletRequest request) {
    return ResponseEntity.status(401)
            .body(new ErrorDTO(401, e.getMessage(), e.getClass().getName(), request.getRequestURI()));
  }

  @ExceptionHandler(InvalidToken.class)
  public ResponseEntity<ErrorDTO> handleInvalidTokenException(InvalidToken e, HttpServletRequest request) {
    return ResponseEntity.status(401)
            .body(new ErrorDTO(401, e.getMessage(), e.getClass().getName(), request.getRequestURI()));
  }

  // Catch any other unexpected exceptions
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorDTO> handleGenericException(Exception e, HttpServletRequest request) {
    return ResponseEntity.status(500)
            .body(new ErrorDTO(500, "Internal server error", e.getClass().getName(), request.getRequestURI()));
  }
}