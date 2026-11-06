package com.elibrary.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.elibrary.domain.Role;
import com.elibrary.domain.UserAccount;
import com.elibrary.repository.UserAccountRepository;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.boot.CommandLineRunner;

class UsernameKeyInitializerTest {

    @Test
    void regeneratesLegacyBase64UsernameKeys() throws Exception {
        UserAccountRepository repository = org.mockito.Mockito.mock(UserAccountRepository.class);
        UserAccount account = new UserAccount("ReaderCase", "hash", "Reader", Role.MEMBER);
        account.setUsernameKey(Base64.getUrlEncoder().withoutPadding()
                .encodeToString("ReaderCase".getBytes(StandardCharsets.UTF_8)));
        when(repository.findAll()).thenReturn(List.of(account));

        CommandLineRunner initializer = new UsernameKeyInitializer()
                .populateCaseSensitiveUsernameKeys(repository);
        initializer.run();

        assertThat(account.getUsernameKey()).isEqualTo(UserAccount.usernameKeyFor("ReaderCase"));
        verify(repository).saveAll(List.of(account));
    }

    @Test
    void generatedKeysStayDistinctUnderCaseInsensitiveComparison() {
        String upperCaseKey = UserAccount.usernameKeyFor("ReaderCase");
        String lowerCaseKey = UserAccount.usernameKeyFor("readercase");

        assertThat(upperCaseKey).matches("[0-9a-f]{64}");
        assertThat(lowerCaseKey).matches("[0-9a-f]{64}");
        assertThat(upperCaseKey.equalsIgnoreCase(lowerCaseKey)).isFalse();
    }

    @Test
    void doesNotWriteAccountsWhoseKeysAreAlreadyCurrent() throws Exception {
        UserAccountRepository repository = org.mockito.Mockito.mock(UserAccountRepository.class);
        UserAccount account = new UserAccount("ReaderCase", "hash", "Reader", Role.MEMBER);
        when(repository.findAll()).thenReturn(List.of(account));

        CommandLineRunner initializer = new UsernameKeyInitializer()
                .populateCaseSensitiveUsernameKeys(repository);
        initializer.run();

        verify(repository).findAll();
        verifyNoMoreInteractions(repository);
    }
}
