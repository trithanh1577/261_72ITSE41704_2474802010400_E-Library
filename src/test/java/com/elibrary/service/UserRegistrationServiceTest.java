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
        when(userRepository.existsByUsername("newmember")).thenReturn(false);
        when(passwordEncoder.encode("a-long-password")).thenReturn("encoded-password");

        registrationService.register(" NewMember ", " New Member ", "a-long-password");

        ArgumentCaptor<UserAccount> accountCaptor = ArgumentCaptor.forClass(UserAccount.class);
        verify(userRepository).save(accountCaptor.capture());
        UserAccount savedAccount = accountCaptor.getValue();
        assertThat(savedAccount.getUsername()).isEqualTo("newmember");
        assertThat(savedAccount.getDisplayName()).isEqualTo("New Member");
        assertThat(savedAccount.getPassword()).isEqualTo("encoded-password");
        assertThat(savedAccount.getRole()).isEqualTo(Role.MEMBER);
    }

    @Test
    void registrationRejectsAnExistingUsername() {
        when(userRepository.existsByUsername("reader")).thenReturn(true);

        assertThatThrownBy(() -> registrationService.register("reader", "Reader", "a-long-password"))
                .isInstanceOf(UserRegistrationService.UsernameAlreadyTakenException.class);

        verify(userRepository, never()).save(any(UserAccount.class));
        verify(passwordEncoder, never()).encode(any());
    }
}
