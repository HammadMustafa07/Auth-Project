package com.example.secureauth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class PasswordConfig {

    @Bean
    PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}

//This gives us Spring Security's DelegatingPasswordEncoder. Its current encoding strategy uses a prefixed format such as:
//
//        {bcrypt}...
//
//and supports future password-encoding upgrades.
//
//Never use NoOpPasswordEncoder or store plain passwords. Spring explicitly considers that insecure.
