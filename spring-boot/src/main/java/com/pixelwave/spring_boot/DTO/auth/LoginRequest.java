package com.pixelwave.spring_boot.DTO.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {

  @NotBlank(message = "Username is required")
  @Email(message = "Username must be a valid email address")
  private String username;

  @NotBlank(message = "Password is required")
  private String password;
}
