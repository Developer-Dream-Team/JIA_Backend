package com.developerdreamteam.jia.auth.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class PasswordEncoderConfig {

    @Autowired
    private Argon2Config argon2Config;

    @Bean
    public PasswordEncoder passwordEncoder() {
        int saltLength = argon2Config.getSaltLength();
        int hashLength = argon2Config.getHashLength();
        int parallelism = argon2Config.getParallelism();
        int memory = argon2Config.getMemory();
        int iterations = argon2Config.getIterations();
        return new Argon2PasswordEncoder(saltLength, hashLength, parallelism, memory, iterations);
    }

    // TODO: Argon2 parameters are optimal for our live environment ; this config uses dynamic values for salt length, hash length, parallelism, memory, and iterations for balanced security and performance.
}
