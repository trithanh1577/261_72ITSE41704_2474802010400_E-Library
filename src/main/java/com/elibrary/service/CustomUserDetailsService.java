package com.elibrary.service;

import com.elibrary.domain.UserAccount;
import com.elibrary.repository.UserAccountRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserAccountRepository userAccountRepository;

    public CustomUserDetailsService(UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        String login = identifier == null ? "" : identifier.trim();
        if (login.isBlank()) {
            throw new UsernameNotFoundException("Account was not found.");
        }
        var accountLookup = login.contains("@")
                ? userAccountRepository.findByEmailIgnoreCase(login)
                : userAccountRepository.findByUsernameKey(UserAccount.usernameKeyFor(login));
        UserAccount account = accountLookup
                .orElseThrow(() -> new UsernameNotFoundException("Account was not found."));
        return User.withUsername(account.getUsername())
                .password(account.getPassword())
                .authorities(new SimpleGrantedAuthority("ROLE_" + account.getRole().name()))
                .disabled(!account.isEnabled())
                .build();
    }
}
