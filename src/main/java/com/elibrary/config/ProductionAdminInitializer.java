package com.elibrary.config;

import com.elibrary.domain.Role;
import com.elibrary.domain.UserAccount;
import com.elibrary.repository.UserAccountRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@Profile("prod")
public class ProductionAdminInitializer {

    @Bean
    CommandLineRunner createFirstAdministrator(UserAccountRepository userRepository,
                                               PasswordEncoder passwordEncoder,
                                               @Value("${BOOTSTRAP_ADMIN_USERNAME:}") String username,
                                               @Value("${BOOTSTRAP_ADMIN_PASSWORD:}") String password,
                                               @Value("${BOOTSTRAP_ADMIN_DISPLAY_NAME:E-Library Administrator}") String displayName) {
        return args -> {
            if (userRepository.count() > 0) {
                return;
            }
            if (username.isBlank() || password.length() < 12) {
                throw new IllegalStateException(
                        "Set BOOTSTRAP_ADMIN_USERNAME and a BOOTSTRAP_ADMIN_PASSWORD of at least 12 characters before starting with an empty production database.");
            }

            userRepository.save(new UserAccount(
                    username, passwordEncoder.encode(password), displayName, Role.LIBRARIAN));
        };
    }
}
