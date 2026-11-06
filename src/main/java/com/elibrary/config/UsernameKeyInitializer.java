package com.elibrary.config;

import com.elibrary.domain.UserAccount;
import com.elibrary.repository.UserAccountRepository;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UsernameKeyInitializer {

    @Bean
    CommandLineRunner populateCaseSensitiveUsernameKeys(UserAccountRepository repository) {
        return args -> {
            List<UserAccount> accountsWithOutdatedKeys = repository.findAll().stream()
                    .filter(account -> !UserAccount.usernameKeyFor(account.getUsername())
                            .equals(account.getUsernameKey()))
                    .toList();
            accountsWithOutdatedKeys.forEach(account -> account.setUsernameKey(
                    UserAccount.usernameKeyFor(account.getUsername())));
            if (!accountsWithOutdatedKeys.isEmpty()) {
                repository.saveAll(accountsWithOutdatedKeys);
            }
        };
    }
}
