package com.nttdata.bank.gateway.service;

import com.nttdata.bank.gateway.config.SecurityProperties;
import com.nttdata.bank.gateway.dto.LoginRequest;
import com.nttdata.bank.gateway.dto.TokenResponse;
import com.nttdata.bank.gateway.exception.InvalidCredentialsException;
import io.reactivex.rxjava3.core.Single;
import java.time.Instant;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

/** Validates the users and creates their JWT. */
@Service
public class AuthService {
  private final SecurityProperties properties;
  private final PasswordEncoder passwordEncoder;
  private final JwtEncoder jwtEncoder;

  /** Creates the service. */
  public AuthService(SecurityProperties properties, PasswordEncoder passwordEncoder,
      JwtEncoder jwtEncoder) {
    this.properties = properties;
    this.passwordEncoder = passwordEncoder;
    this.jwtEncoder = jwtEncoder;
  }

  /** Returns a token when the credentials are valid. */
  public Single<TokenResponse> login(LoginRequest request) {
    return Single.fromCallable(() -> properties.users().stream()
        .filter(user -> user.username().equals(request.username()))
        .filter(user -> request.password() != null
            && passwordEncoder.matches(request.password(), user.password()))
        .findFirst()
        .map(this::token)
        .orElseThrow(InvalidCredentialsException::new));
  }

  private TokenResponse token(SecurityProperties.User user) {
    Instant now = Instant.now();
    long expiresIn = properties.jwt().expiration().toSeconds();
    JwtClaimsSet claims = JwtClaimsSet.builder()
        .issuer("bank-api-gateway")
        .subject(user.username())
        .issuedAt(now)
        .expiresAt(now.plusSeconds(expiresIn))
        .claim("roles", List.of(user.role()))
        .build();
    String token = jwtEncoder.encode(JwtEncoderParameters.from(
        JwsHeader.with(MacAlgorithm.HS256).build(), claims)).getTokenValue();
    return new TokenResponse(token, "Bearer", expiresIn);
  }
}
