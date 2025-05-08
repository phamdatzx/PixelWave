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
public class RegisterRequest {
  @NotBlank(message = "Username is required")
  @Email(message = "Username must be a valid email address")
  private String username;

  @NotBlank(message = "Password is required")
  @Pattern(
          regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$",
          message = "Password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, and one digit"
  )
  private String password;

  @NotBlank(message = "Full name is required")
  private String fullName;

  private int age;
}
