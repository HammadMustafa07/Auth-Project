package com.example.secureauth.service;

import com.example.secureauth.dto.CurrentUserResponse;
import com.example.secureauth.entity.User;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserService {

    private final UserService userService;

    public CurrentUserService(UserService userService) {
        this.userService = userService;
    }

    public CurrentUserResponse getCurrentUser(OidcUser oidcUser) {

        User user = userService.findByEmail(
                oidcUser.getEmail()
        );

        return new CurrentUserResponse(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getProfileImage(),
                user.getRole()
        );
    }
}