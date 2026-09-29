package com.example.secureauth.controller;

import com.example.secureauth.dto.CurrentUserResponse;
import com.example.secureauth.service.CurrentUserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class AuthController {
    private final CurrentUserService currentUserService;

    public AuthController(CurrentUserService currentUserService) {
        this.currentUserService = currentUserService;
    }

    @GetMapping("/api/me")
    public CurrentUserResponse me(
        @AuthenticationPrincipal OidcUser user
    ) {
//        return user.getAttributes(); // here we are returning raw google credentials
        return currentUserService.getCurrentUser(user);
    }
    @GetMapping("/api/authentication")
    public Authentication authentication(
            Authentication authentication
    ) {
        return authentication;
    }



}
