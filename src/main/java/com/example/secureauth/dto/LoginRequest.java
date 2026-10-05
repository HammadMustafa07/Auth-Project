package com.example.secureauth.dto;

public record LoginRequest(
        String email,
        String password
) {
}