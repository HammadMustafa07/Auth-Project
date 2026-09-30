package com.example.secureauth.dto;

//public record CurrentUserResponse(
//     String providerUserId,
//     String email,
//     String name,
//     String profileImage
//) {}


//since now we have our own user saved in database
//we will return it


import com.example.secureauth.entity.UserRole;

import java.util.UUID;

public record CurrentUserResponse(
        UUID id,
        String email,
        String name,
        String profileImage,
        UserRole role
) {}