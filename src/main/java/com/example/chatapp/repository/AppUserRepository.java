package com.example.chatapp.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.chatapp.model.AppUser;

public interface AppUserRepository extends MongoRepository<AppUser, String> {
    Optional<AppUser> findByUsername(String username);
    boolean existsByUsername(String username);
}