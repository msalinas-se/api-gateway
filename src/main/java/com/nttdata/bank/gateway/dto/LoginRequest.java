package com.nttdata.bank.gateway.dto;

/** Credentials of a user. */
public record LoginRequest(String username, String password) {
}
