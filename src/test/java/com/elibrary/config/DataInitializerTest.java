package com.elibrary.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.elibrary.domain.Book;
import com.elibrary.domain.BookCopy;
import com.elibrary.domain.UserAccount;
import com.elibrary.repository.BookCopyRepository;
import com.elibrary.repository.BookRepository;
import com.elibrary.repository.UserAccountRepository;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

class DataInitializerTest {

    @Test
    void seedsFiftyUniqueTitlesAndCopiesWithUniqueBibliographicIdentifiers() throws Exception {
        UserAccountRepository users = mock(UserAccountRepository.class);
        BookRepository books = mock(BookRepository.class);
        BookCopyRepository copies = mock(BookCopyRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        List<Book> savedBooks = new ArrayList<>();
        List<BookCopy> savedCopies = new ArrayList<>();

        when(users.count()).thenReturn(0L);
        when(books.count()).thenReturn(0L);
        when(passwordEncoder.encode("admin123")).thenReturn("encoded-admin");
        when(passwordEncoder.encode("member123")).thenReturn("encoded-member");
        when(books.save(org.mockito.ArgumentMatchers.any(Book.class))).thenAnswer(invocation -> {
            Book book = invocation.getArgument(0);
            savedBooks.add(book);
            return book;
        });
        when(copies.save(org.mockito.ArgumentMatchers.any(BookCopy.class))).thenAnswer(invocation -> {
            BookCopy copy = invocation.getArgument(0);
            savedCopies.add(copy);
            return copy;
        });

        CommandLineRunner runner = new DataInitializer().seedData(users, books, copies, passwordEncoder);
        runner.run();

        assertThat(savedBooks).hasSize(50);
        assertThat(savedCopies).hasSize(51);
        assertThat(savedBooks).extracting(Book::getIsbn).doesNotHaveDuplicates();
        assertThat(savedBooks).extracting(Book::getTitle).doesNotHaveDuplicates();
        assertThat(savedBooks).allSatisfy(book -> assertThat(hasValidIsbn13Checksum(book.getIsbn())).isTrue());
        assertThat(savedCopies).extracting(BookCopy::getCopyCode).doesNotHaveDuplicates();
        assertThat(savedBooks).allSatisfy(book -> assertThat(book.getDescription()).isNotBlank());
        verify(users, times(2)).save(any(UserAccount.class));
    }

    private boolean hasValidIsbn13Checksum(String isbn) {
        if (isbn.length() != 13 || !isbn.chars().allMatch(Character::isDigit)) {
            return false;
        }
        int weightedSum = 0;
        for (int index = 0; index < isbn.length() - 1; index++) {
            weightedSum += Character.digit(isbn.charAt(index), 10) * (index % 2 == 0 ? 1 : 3);
        }
        int checkDigit = (10 - weightedSum % 10) % 10;
        return isbn.length() == 13 && checkDigit == Character.digit(isbn.charAt(12), 10);
    }
}
