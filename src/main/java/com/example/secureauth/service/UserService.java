package com.example.secureauth.service;

import com.example.secureauth.entity.OAuthAccount;
import com.example.secureauth.entity.OAuthProvider;
import com.example.secureauth.entity.User;
import com.example.secureauth.exception.UserProvisioningException;
import com.example.secureauth.repository.OAuthAccountRepository;
import com.example.secureauth.repository.UserRepository;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final OAuthAccountRepository oauthAccountRepository;

    public UserService(
            UserRepository userRepository,
            OAuthAccountRepository oauthAccountRepository
    ) {
        this.userRepository = userRepository;
        this.oauthAccountRepository = oauthAccountRepository;
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalStateException("Authenticated user not found")
                );
    }

    @Transactional
    public User findOrCreateGoogleUser(
            String providerUserId,
            String email,
            String name,
            String profileImage
    ) {
        email = email.trim().toLowerCase(Locale.ROOT);

        Optional<OAuthAccount> existingOAuthAccount =
                oauthAccountRepository.findByProviderAndProviderUserId(
                        OAuthProvider.GOOGLE,
                        providerUserId
                );

        if (existingOAuthAccount.isPresent()) {
            return existingOAuthAccount.get().getUser();
        }

        Optional<User> existingUser =
                userRepository.findByEmail(email);

        if (existingUser.isPresent()) {

            User user = existingUser.get();

            if (!user.isEnabled()) {
                throw new UserProvisioningException(
                        "User account is disabled",
                        null
                );
            }

            OAuthAccount oauthAccount = new OAuthAccount();
            oauthAccount.setProvider(OAuthProvider.GOOGLE);
            oauthAccount.setProviderUserId(providerUserId);
            oauthAccount.setUser(user);

            oauthAccountRepository.save(oauthAccount);

            return user;
        }

        User user = new User();
        user.setEmail(email);
        user.setName(name);
        user.setProfileImage(profileImage);

        User savedUser = userRepository.save(user);

        OAuthAccount oauthAccount = new OAuthAccount();
        oauthAccount.setProvider(OAuthProvider.GOOGLE);
        oauthAccount.setProviderUserId(providerUserId);
        oauthAccount.setUser(savedUser);

        oauthAccountRepository.save(oauthAccount);

        return savedUser;
    }

//    Google
// ↓
//    No OAuthAccount=
// ↓
//    No User with email
// ↓
//    Create User
// ↓
//    Create OAuthAccount

//    private User createGoogleUser(
//            String providerUserId,
//            String email,
//            String name,
//            String profileImage
//    ) {
//
//        User user = new User();
//
//        user.setEmail(email);
//        user.setName(name);
//        user.setProfileImage(profileImage);
//
//        User savedUser = userRepository.save(user);
//
//        OAuthAccount oauthAccount = new OAuthAccount();
//
//        oauthAccount.setProvider(OAuthProvider.GOOGLE);
//        oauthAccount.setProviderUserId(providerUserId);
//        oauthAccount.setUser(savedUser);
//
//        oauthAccountRepository.save(oauthAccount);
//
//        return savedUser;
//    }
}