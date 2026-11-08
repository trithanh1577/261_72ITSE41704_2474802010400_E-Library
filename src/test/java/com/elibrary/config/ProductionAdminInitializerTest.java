package com.elibrary.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.elibrary.domain.Role;
import com.elibrary.domain.UserAccount;
import com.elibrary.repository.UserAccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class ProductionAdminInitializerTest {

    @Mock private UserAccountRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;

    @Test
    void rejectsBootstrapPasswordLongerThanBcryptUtf8Limit() {
        when(userRepository.count()).thenReturn(0L);
        String password = "\u00e9".repeat(37);
        CommandLineRunner initializer = initializer(password);

        assertThatThrownBy(() -> initializer.run())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("72-byte UTF-8");

        verify(userRepository, never()).save(any(UserAccount.class));
        verify(passwordEncoder, never()).encode(any(CharSequence.class));
    }

    @Test
    void acceptsBootstrapPasswordAtBcryptUtf8Limit() throws Exception {
        when(userRepository.count()).thenReturn(0L);
        String password = "p".repeat(72);
        when(passwordEncoder.encode(password)).thenReturn("bcrypt-hash");
        CommandLineRunner initializer = initializer(password);

        initializer.run();

        ArgumentCaptor<UserAccount> accountCaptor = ArgumentCaptor.forClass(UserAccount.class);
        verify(userRepository).save(accountCaptor.capture());
        assertThat(accountCaptor.getValue().getRole()).isEqualTo(Role.LIBRARIAN);
        assertThat(accountCaptor.getValue().getPassword()).isEqualTo("bcrypt-hash");
    }

    private CommandLineRunner initializer(String password) {
        return new ProductionAdminInitializer().createFirstAdministrator(
                userRepository, passwordEncoder, "librarian", password, "E-Library Administrator");
    }
}
