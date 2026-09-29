package com.example.secureauth.dto;

public record CurrentUserResponse(
     String providerUserId,
     String email,
     String name,
     String profileImage
) {}
