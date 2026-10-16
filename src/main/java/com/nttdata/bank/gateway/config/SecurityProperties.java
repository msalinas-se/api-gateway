package com.nttdata.bank.gateway.config;

import java.time.Duration;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

/** JWT settings and users loaded from the config server. */
@ConfigurationProperties("security")
public record SecurityProperties(Jwt jwt, List<User> users) {

  /** Secret and expiration of the tokens. */
  public record Jwt(String secret, Duration expiration) {
  }

  /** User that can log in, the password is encoded. */
  public record User(String username, String password, String role) {
  }
}
