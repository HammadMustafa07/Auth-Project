package com.example.secureauth.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class SmtpEmailSender implements EmailSender {

    private final JavaMailSender mailSender;
    private final String from;
    private final String verifyBaseUrl;

    public SmtpEmailSender(
            JavaMailSender mailSender,
            @Value("${app.mail.from}") String from,
            @Value("${app.auth.verify-email-url}") String verifyBaseUrl
    ) {
        this.mailSender = mailSender;
        this.from = from;
        this.verifyBaseUrl = verifyBaseUrl;
    }

    @Override
    public void sendVerificationEmail(
            String email,
            String verificationUrl
    ) {
        SimpleMailMessage message =
                new SimpleMailMessage();

        message.setFrom(from);
        message.setTo(email);
        message.setSubject("Verify your SecureAuth email");
        message.setText(
                "Verify your email by opening this link:\n\n"
                        + verificationUrl
                        + "\n\n"
                        + "This link expires in 30 minutes."
        );

        mailSender.send(message);
    }
}