package com.pixelwave.spring_boot.DTO.auth;

import com.pixelwave.spring_boot.DTO.user.UserResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
  private String accessToken;
  private String refreshToken;
  private UserResponseDTO user;
}
