package com.example.secureauth.service;

import com.example.secureauth.entity.LocalAccount;
import com.example.secureauth.entity.User;
import com.example.secureauth.exception.UserAlreadyExistsException;
import com.example.secureauth.repository.LocalAccountRepository;
import com.example.secureauth.repository.UserRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class LocalAuthService {

    private final UserRepository userRepository;
    private final LocalAccountRepository localAccountRepository;
    private final PasswordEncoder passwordEncoder;

    public LocalAuthService(
            UserRepository userRepository,
            LocalAccountRepository localAccountRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.localAccountRepository = localAccountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void register(
            String name,
            String email,
            String password
    ) {

        email = email.trim().toLowerCase(Locale.ROOT);

        if (userRepository.existsByEmail(email)) {
            throw new UserAlreadyExistsException(
                    "An account already exists for this email"
            );
        }

        User user = new User();

        user.setName(name.trim());
        user.setEmail(email);

        User savedUser = userRepository.save(user);

        LocalAccount localAccount = new LocalAccount();

        localAccount.setUser(savedUser);
        localAccount.setPasswordHash(
                passwordEncoder.encode(password)
        );

        localAccountRepository.save(localAccount);
    }
}