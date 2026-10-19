package com.nttdata.bank.gateway.config;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;

/** Authentication and authorization of the gateway with JWT. */
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

  /** Login is public, deleting requires the ADMIN role and the rest needs a valid token. */
  @Bean
  public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
    JwtGrantedAuthoritiesConverter authorities = new JwtGrantedAuthoritiesConverter();
    authorities.setAuthoritiesClaimName("roles");
    authorities.setAuthorityPrefix("ROLE_");
    JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
    converter.setJwtGrantedAuthoritiesConverter(authorities);
    return http
        .csrf(ServerHttpSecurity.CsrfSpec::disable)
        .authorizeExchange(exchange -> exchange
            .pathMatchers("/auth/**").permitAll()
            .pathMatchers(HttpMethod.DELETE, "/**").hasRole("ADMIN")
            .anyExchange().authenticated())
        .oauth2ResourceServer(server -> server.jwt(jwt -> jwt
            .jwtAuthenticationConverter(new ReactiveJwtAuthenticationConverterAdapter(converter))))
        .build();
  }

  /** Encoder used to check the passwords of the users. */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return PasswordEncoderFactories.createDelegatingPasswordEncoder();
  }

  /** Signs the tokens with the configured secret. */
  @Bean
  public JwtEncoder jwtEncoder(SecurityProperties properties) {
    return new NimbusJwtEncoder(new ImmutableSecret<>(secretKey(properties)));
  }

  /** Validates the tokens with the configured secret. */
  @Bean
  public ReactiveJwtDecoder jwtDecoder(SecurityProperties properties) {
    return NimbusReactiveJwtDecoder.withSecretKey(secretKey(properties))
        .macAlgorithm(MacAlgorithm.HS256)
        .build();
  }

  /** Key used to sign and validate the tokens. */
  private SecretKey secretKey(SecurityProperties properties) {
    return new SecretKeySpec(properties.jwt().secret().getBytes(StandardCharsets.UTF_8),
        "HmacSHA256");
  }
}
