package com.example.secureauth.repository;

import com.example.secureauth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
//    Spring Data JPA provides repository support on top of JPA, so we don't need to manually write the basic CRUD implementation.

    boolean existsByEmail(String email);


}
