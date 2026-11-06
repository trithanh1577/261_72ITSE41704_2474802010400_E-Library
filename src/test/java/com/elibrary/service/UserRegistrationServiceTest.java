package com.elibrary.service;

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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserRegistrationServiceTest {

    @Mock private UserAccountRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @InjectMocks private UserRegistrationService registrationService;

    @Test
    void registrationCreatesOnlyAMemberAndStoresEncodedPassword() {
        when(userRepository.existsByUsernameKey(UserAccount.usernameKeyFor("NewMember"))).thenReturn(false);
        when(userRepository.existsByEmailIgnoreCase("reader@example.com")).thenReturn(false);
        when(passwordEncoder.encode("a-long-password")).thenReturn("encoded-password");

        registrationService.register(" NewMember ", " Reader@Example.com ", " New Member ", "a-long-password");

        ArgumentCaptor<UserAccount> accountCaptor = ArgumentCaptor.forClass(UserAccount.class);
        verify(userRepository).save(accountCaptor.capture());
        UserAccount savedAccount = accountCaptor.getValue();
        assertThat(savedAccount.getUsername()).isEqualTo("NewMember");
        assertThat(savedAccount.getEmail()).isEqualTo("reader@example.com");
        assertThat(savedAccount.getDisplayName()).isEqualTo("New Member");
        assertThat(savedAccount.getPassword()).isEqualTo("encoded-password");
        assertThat(savedAccount.getRole()).isEqualTo(Role.MEMBER);
    }

    @Test
    void registrationRejectsAnExistingUsername() {
        when(userRepository.existsByUsernameKey(UserAccount.usernameKeyFor("reader"))).thenReturn(true);

        assertThatThrownBy(() -> registrationService.register("reader", "reader@example.com", "Reader", "a-long-password"))
                .isInstanceOf(UserRegistrationService.UsernameAlreadyTakenException.class);

        verify(userRepository, never()).save(any(UserAccount.class));
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    void registrationRejectsAnExistingEmailIgnoringCase() {
        when(userRepository.existsByUsernameKey(UserAccount.usernameKeyFor("reader2"))).thenReturn(false);
        when(userRepository.existsByEmailIgnoreCase("reader@example.com")).thenReturn(true);

        assertThatThrownBy(() -> registrationService.register(
                "reader2", "Reader@Example.com", "Reader", "a-long-password"))
                .isInstanceOf(UserRegistrationService.UsernameAlreadyTakenException.class);
        verify(userRepository, never()).save(any(UserAccount.class));
    }

    @Test
    void registrationRejectsPasswordLongerThanBcryptUtf8Limit() {
        String password = "ệ".repeat(25);

        assertThatThrownBy(() -> registrationService.register(
                "reader3", "reader3@example.com", "Reader", password))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("72-byte UTF-8");

        verify(userRepository, never()).save(any(UserAccount.class));
        verify(passwordEncoder, never()).encode(any());
    }
}
