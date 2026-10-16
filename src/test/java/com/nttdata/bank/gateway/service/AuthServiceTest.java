package com.nttdata.bank.gateway.service;

import com.nttdata.bank.gateway.config.SecurityConfig;
import com.nttdata.bank.gateway.config.SecurityProperties;
import com.nttdata.bank.gateway.dto.LoginRequest;
import com.nttdata.bank.gateway.exception.InvalidCredentialsException;
import java.time.Duration;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;

class AuthServiceTest {
  private final SecurityConfig config = new SecurityConfig();
  private final SecurityProperties properties = new SecurityProperties(
      new SecurityProperties.Jwt("test-secret-key-with-at-least-thirty-two-bytes",
          Duration.ofHours(1)),
      List.of(new SecurityProperties.User("admin", "{noop}admin123", "ADMIN")));
  private final AuthService authService = new AuthService(properties, config.passwordEncoder(),
      config.jwtEncoder(properties));

  @Test
  void tokenContainsUserAndRole() {
    authService.login(new LoginRequest("admin", "admin123")).test()
        .assertValue(response -> {
          Jwt jwt = config.jwtDecoder(properties).decode(response.token()).block();
          return "admin".equals(jwt.getSubject())
              && jwt.getClaimAsStringList("roles").equals(List.of("ADMIN"))
              && response.expiresIn() == 3600;
        });
  }

  @Test
  void unknownUser() {
    authService.login(new LoginRequest("ana", "admin123")).test()
        .assertError(InvalidCredentialsException.class);
  }

  @Test
  void missingPassword() {
    authService.login(new LoginRequest("admin", null)).test()
        .assertError(InvalidCredentialsException.class);
  }
}
