package com.example.secureauth.service;

import com.example.secureauth.dto.CurrentUserResponse;
import com.example.secureauth.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserService {

    private final UserService userService;

    public CurrentUserService(UserService userService) {
        this.userService = userService;
    }

//    public CurrentUserResponse getCurrentUser(OidcUser oidcUser) {
//
//        User user = userService.findByEmail(
//                oidcUser.getEmail()
//        );
//
//        return new CurrentUserResponse(
//                user.getId(),
//                user.getEmail(),
//                user.getName(),
//                user.getProfileImage(),
//                user.getRole()
//        );
//    }

    // here we are only taking the OidCUser registered oauth user
//    now we can with both strategies

    public CurrentUserResponse getCurrentUser(
            Authentication authentication
    ) {

        String email;

        Object principal = authentication.getPrincipal();

        if (principal instanceof OidcUser oidcUser) {

            email = oidcUser.getEmail();

        } else if (principal instanceof UserDetails userDetails) {

            email = userDetails.getUsername();

        } else {
            throw new IllegalStateException(
                    "Unsupported authentication principal"
            );
        }

        User user = userService.findByEmail(email);

        return new CurrentUserResponse(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getProfileImage(),
                user.getRole()
        );
    }
}