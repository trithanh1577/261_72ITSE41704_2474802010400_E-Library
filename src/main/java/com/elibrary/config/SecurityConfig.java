package com.elibrary.config;

import com.elibrary.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http,
                                            CustomUserDetailsService userDetailsService) throws Exception {
        http
                .userDetailsService(userDetailsService)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css/**", "/login", "/register", "/error").permitAll()
                        .requestMatchers("/books/new", "/books/save", "/books/*/edit", "/books/*/deactivate",
                                "/books/*/copies/**", "/copies/**").hasRole("LIBRARIAN")
                        .requestMatchers("/circulation/requests", "/circulation/requests/*/approve",
                                "/circulation/requests/*/reject", "/circulation/loans",
                                "/circulation/loans/*/return").hasRole("LIBRARIAN")
                        .requestMatchers("/books", "/books/*").permitAll()
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/books", true)
                        .permitAll())
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                        .permitAll())
                .exceptionHandling(exception -> exception.accessDeniedPage("/error/403"));
        return http.build();
    }
}
