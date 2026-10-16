package com.nttdata.bank.gateway.dto;

/** Token returned after a successful login. */
public record TokenResponse(String token, String type, long expiresIn) {
}
