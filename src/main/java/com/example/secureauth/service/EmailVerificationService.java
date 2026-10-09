package com.example.secureauth.service;

import com.example.secureauth.entity.EmailVerificationToken;
import com.example.secureauth.entity.User;
import com.example.secureauth.repository.EmailVerificationTokenRepository;
import com.example.secureauth.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.HexFormat;


@Service
public class EmailVerificationService {

    private final EmailVerificationTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final EmailSender emailSender;

    public EmailVerificationService(
            EmailVerificationTokenRepository tokenRepository,
            UserRepository userRepository,
            EmailSender emailSender
    ) {
        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
        this.emailSender = emailSender;
    }

    @Transactional
    public void createVerificationToken(User user) {

        String rawToken = generateToken();
        String tokenHash = hashToken(rawToken);

        EmailVerificationToken token =
                new EmailVerificationToken();

        token.setUser(user);
        token.setTokenHash(tokenHash);
        token.setExpiresAt(
                Instant.now().plus(30, ChronoUnit.MINUTES)
        );

        tokenRepository.save(token);

        String verificationUrl =
                "http://localhost:8080/api/auth/verify-email"
                        + "?token="
                        + URLEncoder.encode(
                        rawToken,
                        StandardCharsets.UTF_8
                );

        emailSender.sendVerificationEmail(
                user.getEmail(),
                verificationUrl
        );
    }

    @Transactional
    public void verify(String rawToken) {

        String tokenHash = hashToken(rawToken);

        EmailVerificationToken token =
                tokenRepository.findByTokenHash(tokenHash)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Invalid or expired verification token"
                                )
                        );

        if (token.getUsedAt() != null) {
            throw new IllegalArgumentException(
                    "Invalid or expired verification token"
            );
        }

        if (token.getExpiresAt().isBefore(Instant.now())) {
            throw new IllegalArgumentException(
                    "Invalid or expired verification token"
            );
        }

        User user = token.getUser();

        if (user.getEmailVerifiedAt() == null) {
            user.setEmailVerifiedAt(Instant.now());
            userRepository.save(user);
        }

        token.setUsedAt(Instant.now());
        tokenRepository.save(token);
    }

    private String generateToken() {

        byte[] bytes = new byte[32];

        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(bytes);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);
    }


    private String hashToken(String token) {

        try {
            MessageDigest digest =
                    MessageDigest.getInstance("SHA-256");

            byte[] hash = digest.digest(
                    token.getBytes(StandardCharsets.UTF_8)
            );

            return HexFormat.of().formatHex(hash);

        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException(
                    "SHA-256 algorithm unavailable",
                    ex
            );
        }
    }

}