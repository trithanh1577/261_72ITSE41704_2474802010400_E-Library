package com.elibrary.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.elibrary.domain.Role;
import com.elibrary.domain.UserAccount;
import com.elibrary.repository.UserAccountRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock private UserAccountRepository repository;

    @Test
    void usernameLoginPreservesExactCase() {
        UserAccount account = new UserAccount("ReaderOne", "hash", "Reader", Role.MEMBER);
        when(repository.findByUsernameKey(UserAccount.usernameKeyFor("ReaderOne")))
                .thenReturn(Optional.of(account));

        var details = new CustomUserDetailsService(repository).loadUserByUsername("ReaderOne");

        assertThat(details.getUsername()).isEqualTo("ReaderOne");
        assertThat(details.getAuthorities()).extracting("authority").containsExactly("ROLE_MEMBER");
        verify(repository).findByUsernameKey(UserAccount.usernameKeyFor("ReaderOne"));
    }

    @Test
    void emailLoginIgnoresEmailCaseAndWhitespace() {
        UserAccount account = new UserAccount("ReaderOne", "reader@example.com", "hash", "Reader", Role.MEMBER);
        when(repository.findByEmailIgnoreCase("Reader@Example.com")).thenReturn(Optional.of(account));

        var details = new CustomUserDetailsService(repository).loadUserByUsername(" Reader@Example.com ");

        assertThat(details.getUsername()).isEqualTo("ReaderOne");
        verify(repository).findByEmailIgnoreCase("Reader@Example.com");
    }

    @Test
    void blankLoginIsRejected() {
        assertThatThrownBy(() -> new CustomUserDetailsService(repository).loadUserByUsername("  "))
                .isInstanceOf(UsernameNotFoundException.class);
    }
}
