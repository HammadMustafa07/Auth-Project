package com.example.secureauth.repository;

import com.example.secureauth.entity.OAuthAccount;
import com.example.secureauth.entity.OAuthProvider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface OAuthAccountRepository extends JpaRepository<OAuthAccount, UUID> {
    Optional<OAuthAccount> findByProviderAndProviderUserId(
            OAuthProvider oAuthProvider,
            String providerUserId
    );
}
