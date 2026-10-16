package com.nttdata.bank.gateway;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest
@AutoConfigureWebTestClient
class ApiGatewayApplicationTest {

  @Autowired
  private WebTestClient webTestClient;

  private String token(String username, String password) {
    return webTestClient.post().uri("/auth/login")
        .bodyValue(Map.of("username", username, "password", password))
        .exchange()
        .expectStatus().isOk()
        .expectBody(JsonNode.class)
        .returnResult().getResponseBody().get("token").asText();
  }

  @Test
  void loginReturnsToken() {
    webTestClient.post().uri("/auth/login")
        .bodyValue(Map.of("username", "admin", "password", "admin123"))
        .exchange()
        .expectStatus().isOk()
        .expectBody().jsonPath("$.type").isEqualTo("Bearer");
  }

  @Test
  void loginWithWrongPassword() {
    webTestClient.post().uri("/auth/login")
        .bodyValue(Map.of("username", "admin", "password", "wrong"))
        .exchange()
        .expectStatus().isUnauthorized();
  }

  @Test
  void requestWithoutTokenIsRejected() {
    webTestClient.get().uri("/customers").exchange().expectStatus().isUnauthorized();
  }

  @Test
  void deleteNeedsAdminRole() {
    webTestClient.delete().uri("/customers/1")
        .header("Authorization", "Bearer " + token("user", "user123"))
        .exchange()
        .expectStatus().isForbidden();
  }

  @Test
  void requestWithTokenIsAccepted() {
    webTestClient.get().uri("/unknown")
        .header("Authorization", "Bearer " + token("user", "user123"))
        .exchange()
        .expectStatus().isNotFound();
  }
}
