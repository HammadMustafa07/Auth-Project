package com.example.secureauth.security;

import com.example.secureauth.exception.UserProvisioningException;
import com.example.secureauth.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

@Service
public class CustomOidcUserService extends OidcUserService {

    private static final Logger log =
            LoggerFactory.getLogger(CustomOidcUserService.class);

    private final UserService userService;

    public CustomOidcUserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) {

        OidcUser oidcUser = super.loadUser(userRequest);

        String providerUserId = oidcUser.getSubject();
        String email = oidcUser.getEmail();

        if (providerUserId == null || providerUserId.isBlank()) {
            throw new OAuth2AuthenticationException(
                    new OAuth2Error("invalid_user_info"),
                    "Required Google user information is missing"
            );
        }

        if (email == null || email.isBlank()) {
            throw new OAuth2AuthenticationException(
                    new OAuth2Error("invalid_user_info"),
                    "Required Google user information is missing"
            );
        }

        Boolean emailVerified = oidcUser.getEmailVerified();

        if (!Boolean.TRUE.equals(emailVerified)) {
            throw new OAuth2AuthenticationException(
                    new OAuth2Error("unverified_email"),
                    "Google email is not verified"
            );
        }

        try {

            userService.findOrCreateGoogleUser(
                    providerUserId,
                    email,
                    oidcUser.getFullName(),
                    oidcUser.getPicture()
            );

            return oidcUser;

        } catch (UserProvisioningException ex) {

            log.error(
                    "Failed to provision Google user",
                    ex
            );

            throw new OAuth2AuthenticationException(
                    new OAuth2Error("server_error"),
                    "Unable to complete authentication",
                    ex
            );
        }
    }
}


//Line 21 (super.loadUser(userRequest)): Spring's default OidcUserService verifies the cryptographic signature of Google's ID Token (fetching Google's public JWK keys), parses claims (sub, email, name, picture), and optionally calls Google's UserInfo endpoint. It returns a fully populated DefaultOidcUser.


