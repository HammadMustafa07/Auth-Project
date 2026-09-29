package com.example.secureauth.security;

import com.example.secureauth.service.UserService;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

@Service
public class CustomOidcUserService extends OidcUserService {

    private final UserService userService;

    public CustomOidcUserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) {

        OidcUser oidcUser = super.loadUser(userRequest);

        userService.findOrCreateGoogleUser(
                oidcUser.getSubject(),
                oidcUser.getEmail(),
                oidcUser.getFullName(),
                oidcUser.getPicture()
        );

        return oidcUser;
    }
}


//Line 21 (super.loadUser(userRequest)): Spring's default OidcUserService verifies the cryptographic signature of Google's ID Token (fetching Google's public JWK keys), parses claims (sub, email, name, picture), and optionally calls Google's UserInfo endpoint. It returns a fully populated DefaultOidcUser.


