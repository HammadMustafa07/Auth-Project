package com.example.secureauth.controller;

import com.example.secureauth.dto.CurrentUserResponse;
import com.example.secureauth.dto.LoginRequest;
import com.example.secureauth.dto.RegisterRequest;
import com.example.secureauth.service.CurrentUserService;
import com.example.secureauth.service.LocalAuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.*;


@RestController
public class AuthController {
    private final CurrentUserService currentUserService;
    private final LocalAuthService localAuthService;
    private final AuthenticationManager authenticationManager;
    private final SessionAuthenticationStrategy sessionAuthenticationStrategy;
    private final SecurityContextRepository securityContextRepository;

    public AuthController(CurrentUserService currentUserService, LocalAuthService localAuthService, AuthenticationManager authenticationManager, SessionAuthenticationStrategy sessionAuthenticationStrategy, SecurityContextRepository securityContextRepository) {
        this.currentUserService = currentUserService;
        this.localAuthService = localAuthService;
        this.authenticationManager = authenticationManager;
        this.sessionAuthenticationStrategy = sessionAuthenticationStrategy;
        this.securityContextRepository = securityContextRepository;
    }

    @GetMapping("/api/auth/me")
    public CurrentUserResponse me(
//        @AuthenticationPrincipal OidcUser user
            Authentication authentication
    ) {
//        return user.getAttributes(); // here we are returning raw google credentials
        return currentUserService.getCurrentUser(authentication);
    }
    @GetMapping("/api/authentication")
    public Authentication authentication(
            Authentication authentication
    ) {
        return authentication;
    }

    @PostMapping("/api/auth/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void register(
            @RequestBody RegisterRequest request
    ) {
        localAuthService.register(
                request.name(),
                request.email(),
                request.password()
        );
    }

    @PostMapping("/api/auth/login")
    public CurrentUserResponse login(
            @RequestBody LoginRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse
    ) {

        Authentication authenticationRequest =
                UsernamePasswordAuthenticationToken.unauthenticated(
                        request.email(),
                        request.password()
                );

        Authentication authenticationResponse =
                authenticationManager.authenticate(
                        authenticationRequest
                );

        sessionAuthenticationStrategy.onAuthentication(
                authenticationResponse,
                httpRequest,
                httpResponse
        );

        SecurityContext context =
                SecurityContextHolder.createEmptyContext();

        context.setAuthentication(authenticationResponse);

        SecurityContextHolder.setContext(context);

        securityContextRepository.saveContext(
                context,
                httpRequest,
                httpResponse
        );

        return currentUserService.getCurrentUser(
                authenticationResponse
        );
    }



}


//POST /api/auth/login
//        ↓
//AuthenticationManager
//        ↓
//DaoAuthenticationProvider
//        ↓
//UserDetailsService
//        ↓
//Password verification
//        ↓
//Authentication
//        ↓
//change session ID
//        ↓
//SecurityContext
//        ↓
//HTTP Session