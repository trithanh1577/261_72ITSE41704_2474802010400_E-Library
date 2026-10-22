package com.elibrary.service;

import com.elibrary.domain.Role;
import com.elibrary.domain.UserAccount;
import com.elibrary.repository.UserAccountRepository;
import java.util.Locale;
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
    public void register(String username, String displayName, String rawPassword) {
        String normalizedUsername = username.trim().toLowerCase(Locale.ROOT);
        if (userRepository.existsByUsername(normalizedUsername)) {
            throw new UsernameAlreadyTakenException();
        }

        userRepository.save(new UserAccount(
                normalizedUsername,
                passwordEncoder.encode(rawPassword),
                displayName.trim(),
                Role.MEMBER));
    }

    public static class UsernameAlreadyTakenException extends RuntimeException {
    }
}
