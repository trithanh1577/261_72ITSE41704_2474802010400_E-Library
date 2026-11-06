package com.elibrary.service;

import com.elibrary.domain.Role;
import com.elibrary.domain.UserAccount;
import com.elibrary.repository.UserAccountRepository;
import java.nio.charset.StandardCharsets;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserRegistrationService {

    private final UserAccountRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserRegistrationService(UserAccountRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void register(String username, String email, String displayName, String rawPassword) {
        if (username == null || username.isBlank() || email == null || email.isBlank()
                || displayName == null || displayName.isBlank() || rawPassword == null || rawPassword.isBlank()) {
            throw new IllegalArgumentException("Username, email, display name and password are required.");
        }
        if (rawPassword.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new IllegalArgumentException("Password must not exceed BCrypt's 72-byte UTF-8 input limit.");
        }
        String normalizedUsername = username.trim();
        String normalizedEmail = email.trim().toLowerCase(java.util.Locale.ROOT);
        if (!normalizedEmail.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            throw new IllegalArgumentException("Enter a valid email address.");
        }
        if (userRepository.existsByUsernameKey(UserAccount.usernameKeyFor(normalizedUsername))
                || userRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            throw new UsernameAlreadyTakenException();
        }

        userRepository.save(new UserAccount(
                normalizedUsername,
                normalizedEmail,
                passwordEncoder.encode(rawPassword),
                displayName.trim(),
                Role.MEMBER));
    }

    public static class UsernameAlreadyTakenException extends RuntimeException {
    }
}
