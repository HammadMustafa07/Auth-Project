package com.example.secureauth.dto;

public record RegisterRequest(
        String name,
        String email,
        String password
) {
}