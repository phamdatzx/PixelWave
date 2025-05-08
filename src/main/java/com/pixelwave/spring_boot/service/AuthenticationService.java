package com.pixelwave.spring_boot.service;

import com.pixelwave.spring_boot.DTO.auth.LoginRequest;
import com.pixelwave.spring_boot.DTO.auth.LoginResponse;
import com.pixelwave.spring_boot.DTO.auth.RegisterRequest;
import com.pixelwave.spring_boot.DTO.user.UserResponseDTO;
import com.pixelwave.spring_boot.exception.ConflictException;
import com.pixelwave.spring_boot.exception.InvalidToken;
import com.pixelwave.spring_boot.exception.ResourceNotFoundException;
import com.pixelwave.spring_boot.model.Role;
import com.pixelwave.spring_boot.model.Token;
import com.pixelwave.spring_boot.model.User;
import com.pixelwave.spring_boot.repository.TokenRepository;
import com.pixelwave.spring_boot.repository.UserRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
  // Inject Google OAuth2 properties from application.yml
  @Value("${spring.security.oauth2.client.provider.google.authorization-uri}")
  private String googleAuthorizationUri;
  @Value("${spring.security.oauth2.client.registration.google.client-id}")
  private String googleClientId;
  @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
  private String googleRedirectUri;
  @Value("${spring.security.oauth2.client.registration.google.scope}")
  private String googleScope;
  @Value("${spring.security.oauth2.client.registration.google.authorization-grant-type}")
  private String googleResponseType;
  @Value("${spring.security.oauth2.client.registration.google.client-secret}")
  private String googleClientSecret;
  @Value("${spring.security.oauth2.client.provider.google.user-info-uri}")
  private String googleUserInfoUri;
  @Value("${client.url}")
  private String clientUrl;
  @Value("${refresh-token-expiry-time}")
  private Long refreshTokenExpiryTime;

  private final OAuth2AuthorizedClientManager authorizedClientManager;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;
  private final EmailSenderService emailSenderService;
  private final ModelMapper modelMapper;
  private final TokenRepository tokenRepository;

  public void register(RegisterRequest request) {
    // Validate email existence
    if(userRepository.existsByUsername(request.getUsername())) {
      throw new ConflictException("Email already exists");
    }

    //create new user and save it to the database
    User user = modelMapper.map(request, User.class);
    user.setPassword(passwordEncoder.encode(request.getPassword()));
    user.setRole(Role.USER);
    user.setActivated(false);
    userRepository.save(user);

    //generate jwt token for activating url
    var jwt = jwtService.generateToken(user);
    //send email to verify the email
//    emailSenderService.sendEmail(request.getUsername(), "Activate your account",
//            clientUrl+"/activate/" + jwt);
  }

  public void activeAccount(String token) {
      var userName = jwtService.extractSubject(token);

      var user = userRepository.findByUsername(userName);
      if(user.isEmpty()) {
        throw new ResourceNotFoundException("User not found");
      }
      user.get().setActivated(true);
      userRepository.save(user.get());
  }

  public LoginResponse login(LoginRequest request) {
    var authenticate = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            request.getUsername(),
            request.getPassword()
        )
    );

    var user = (User)authenticate.getPrincipal();

    return getLoginResponse(user);
  }

  public LoginResponse socialLogin(LoginRequest request) {
    var user = userRepository.findByUsername(request.getUsername())
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    return getLoginResponse(user);
  }

  private LoginResponse getLoginResponse(User user) {
    var jwtToken = jwtService.generateToken(user);
    var refreshToken = jwtService.generateOpaqueToken();
    tokenRepository.save(
            Token.builder()
                    .user(user)
                    .token(refreshToken)
                    .expiryDateTime(LocalDateTime.now().plusSeconds(refreshTokenExpiryTime))
                    .isRevoked(false)
                    .build()
    );
    return LoginResponse.builder()
            .accessToken(jwtToken)
            .refreshToken(refreshToken)
            .user(modelMapper.map(user, UserResponseDTO.class))
            .build();
  }

  public LoginResponse refreshToken(String refreshToken) {

    var token = tokenRepository.findByToken(refreshToken)
        .orElseThrow(() -> new InvalidToken("Refresh token not found in database"));
    if (token.isRevoked()) {
      throw new InvalidToken("Refresh token has been revoked");
    }
    var newRefreshToken = jwtService.generateOpaqueToken();
    var newAccessToken = jwtService.generateToken(token.getUser());
    token.setToken(newRefreshToken);
    token.setExpiryDateTime(LocalDateTime.now().plusSeconds(refreshTokenExpiryTime));
    tokenRepository.save(token);

    return LoginResponse.builder()
        .refreshToken(newRefreshToken)
        .accessToken(newAccessToken)
        .build();
  }

  public String generateSocialLoginUrl(String provider) {
    switch (provider) {
      case "google":
        googleScope = googleScope.replace(", ", "%20");
        return String.format("%s?client_id=%s&redirect_uri=%s&response_type=%s&scope=%s&provider=%s",
                googleAuthorizationUri, googleClientId, googleRedirectUri, "code" , googleScope, provider);

    }
    return null;
  }

  public Map<String,Object> authenticateAndFetchProfile(String provider, String code) {
    try {
      RestTemplate restTemplate = new RestTemplate();
      restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
      switch (provider) {
        case "google":
          String accessToken = new GoogleAuthorizationCodeTokenRequest(
                  new NetHttpTransport(), new GsonFactory(),
                  googleClientId,
                  googleClientSecret,
                  code,
                  googleRedirectUri
          ).execute().getAccessToken();

          restTemplate.getInterceptors().add((request, body, execution) -> {
            request.getHeaders().setBearerAuth(accessToken);
            return execution.execute(request, body);
          });

          try {
            return new ObjectMapper().readValue(
                    restTemplate.getForEntity(googleUserInfoUri, String.class).getBody(),
                    new TypeReference<>() {
                    });
          } catch (Exception e) {
            throw new RuntimeException(e);
          }
      }
    }
    catch (Exception e) {
      throw new RuntimeException(e);
    }
    return null;
  }

  public void  registerSocialUser(Map<String, Object> result) {
    String email = (String) result.get("email");
    String name = (String) result.get("name");
    String picture = (String) result.get("picture");

    if(userRepository.existsByUsername(email)) {
      return;
    }

    User user = User.builder()
            .username(email)
            .fullName(name)
            .avatar(picture)
            .password(null)
            .activated(true)
            .role(Role.USER)
            .build();
    userRepository.save(user);
  }
}
