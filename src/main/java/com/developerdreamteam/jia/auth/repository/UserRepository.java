package com.developerdreamteam.jia.auth.repository;

import com.developerdreamteam.jia.auth.model.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
    Optional<User> findByActivationCode(String activationCode);
}
