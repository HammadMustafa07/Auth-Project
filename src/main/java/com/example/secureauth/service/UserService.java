package com.example.secureauth.service;

import com.example.secureauth.entity.OAuthAccount;
import com.example.secureauth.entity.OAuthProvider;
import com.example.secureauth.entity.User;
import com.example.secureauth.repository.OAuthAccountRepository;
import com.example.secureauth.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public User findOrCreateGoogleUser(
            String providerUserId,
            String email,
            String name,
            String profileImage
    ) {

        return oauthAccountRepository
                .findByProviderAndProviderUserId(
                        OAuthProvider.GOOGLE,
                        providerUserId
                )
                .map(OAuthAccount::getUser)
                .orElseGet(() -> createGoogleUser(
                        providerUserId,
                        email,
                        name,
                        profileImage
                ));
    }

    private User createGoogleUser(
            String providerUserId,
            String email,
            String name,
            String profileImage
    ) {

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
}