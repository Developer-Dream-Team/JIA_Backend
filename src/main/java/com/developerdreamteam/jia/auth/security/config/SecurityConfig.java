package com.developerdreamteam.jia.auth.security.config;

import com.developerdreamteam.jia.auth.config.Argon2Config;
import com.developerdreamteam.jia.auth.security.matchers.SecurityMatchers;
import com.developerdreamteam.jia.auth.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private Argon2Config argon2Config;

    @Bean
    public UserDetailsService userDetailsService() {
        return new AuthService();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new Argon2PasswordEncoder(
                argon2Config.getIterations(),
                argon2Config.getMemory(),
                argon2Config.getParallelism(),
                argon2Config.getSaltLength(),
                argon2Config.getHashLength()
        );
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);

        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers(HttpMethod.GET, SecurityMatchers.PUBLIC_ENDPOINTS.getMatchers()).permitAll()
                .requestMatchers(HttpMethod.POST, SecurityMatchers.PUBLIC_ENDPOINTS.getMatchers()).permitAll()
                .requestMatchers(SecurityMatchers.STATIC_RESOURCES.getMatchers()).permitAll()
                .requestMatchers(HttpMethod.GET, SecurityMatchers.ADMIN_GET_ENDPOINTS.getMatchers()).hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, SecurityMatchers.ADMIN_POST_ENDPOINTS.getMatchers()).hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, SecurityMatchers.USER_ENDPOINTS.getMatchers()).hasRole("USER")
                .requestMatchers(HttpMethod.POST, SecurityMatchers.USER_ENDPOINTS.getMatchers()).hasRole("USER")
                .requestMatchers("/test").authenticated()
                .anyRequest().authenticated()
        );
        return http.build();
    }
}
