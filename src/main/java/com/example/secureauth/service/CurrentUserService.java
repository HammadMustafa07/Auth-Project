package com.example.secureauth.service;

import com.example.secureauth.dto.CurrentUserResponse;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserService {
    public CurrentUserResponse getCurrentUser(OidcUser user) {
        return new CurrentUserResponse(
                user.getSubject(),
                user.getEmail(),
                user.getFullName(),
                user.getPicture()
        );
    }
}
