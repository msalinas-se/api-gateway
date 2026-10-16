package com.nttdata.bank.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/** API gateway that routes the requests to the microservices. */
@SpringBootApplication
@ConfigurationPropertiesScan
public class ApiGatewayApplication {

  /** Starts the API gateway. */
  public static void main(String[] args) {
    SpringApplication.run(ApiGatewayApplication.class, args);
  }
}
