package com.example.secureauth.service;

public interface EmailSender {

    void sendVerificationEmail(
            String email,
            String verificationUrl
    );
}