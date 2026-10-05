package com.example.secureauth.security;

import com.example.secureauth.entity.LocalAccount;
import com.example.secureauth.entity.User;
import com.example.secureauth.repository.LocalAccountRepository;
import com.example.secureauth.repository.UserRepository;

import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class LocalUserDetailsService
        implements UserDetailsService {

    private final UserRepository userRepository;
    private final LocalAccountRepository localAccountRepository;

    public LocalUserDetailsService(
            UserRepository userRepository,
            LocalAccountRepository localAccountRepository
    ) {
        this.userRepository = userRepository;
        this.localAccountRepository = localAccountRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "User not found"
                        )
                );

        LocalAccount localAccount =
                localAccountRepository.findByUser(user)
                        .orElseThrow(() ->
                                new UsernameNotFoundException(
                                        "Local account not found"
                                )
                        );

        return org.springframework.security.core.userdetails.User.withUsername(user.getEmail())
                .password(localAccount.getPasswordHash())
                .roles(user.getRole().name())
                .disabled(!user.isEnabled())
                .build();
    }
}

//Important Clarification: This class does not actually verify or check the user's password. Its only job is to fetch the user's data from the database and package it into a format Spring Security understands. Spring Security will do the actual password comparison and verification later.

// why does this class exist we have our own custom entities like User, LocalAccountUser
//and spring doesn't a word about it and it will handle our authentication so it should know
//so when user submits email, password we are giving spring security the data of the user in the
//format it can understand and that format is interface UserDetails

//spring will compare the hash password with it's encoder and will authroize the roles of the user