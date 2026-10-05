package com.example.secureauth.repository;

import com.example.secureauth.entity.LocalAccount;
import com.example.secureauth.entity.User;
import org.springframework.cglib.core.Local;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface LocalAccountRepository extends JpaRepository<LocalAccount, UUID> {
    Optional<LocalAccount> findByUser(User user);

}
