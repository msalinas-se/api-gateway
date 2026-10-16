package com.nttdata.bank.gateway.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/** Raised when the username or the password are wrong. */
@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class InvalidCredentialsException extends RuntimeException {

  /** Creates the exception. */
  public InvalidCredentialsException() {
    super("Invalid username or password");
  }
}
