package com.nttdata.bank.gateway.controller;

import com.nttdata.bank.gateway.dto.LoginRequest;
import com.nttdata.bank.gateway.dto.TokenResponse;
import com.nttdata.bank.gateway.service.AuthService;
import io.reactivex.rxjava3.core.Single;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/** Login endpoint that returns a JWT. */
@RestController
public class AuthController {
  private final AuthService authService;

  /** Creates the controller. */
  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  /** Validates the credentials and returns a token. */
  @PostMapping("/auth/login")
  public Single<TokenResponse> login(@RequestBody LoginRequest request) {
    return authService.login(request);
  }
}
