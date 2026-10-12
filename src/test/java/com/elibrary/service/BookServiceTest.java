package com.elibrary.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.elibrary.domain.Book;
import com.elibrary.repository.BookCopyRepository;
import com.elibrary.repository.BookRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock private BookRepository bookRepository;
    @Mock private BookCopyRepository copyRepository;
    @InjectMocks private BookService bookService;

    @Test
    void blankSearchReturnsActiveBooksInRepositoryOrder() {
        Book book = new Book("Clean Code", "9780132350884", "Robert C. Martin", "Software", "");
        when(bookRepository.findAllByActiveTrueOrderByTitleAsc()).thenReturn(List.of(book));

        assertThat(bookService.search("  ")).containsExactly(book);
        verify(bookRepository).findAllByActiveTrueOrderByTitleAsc();
    }

    @Test
    void keywordSearchTrimsWhitespaceBeforeQuerying() {
        Book book = new Book("Effective Java", "9780134685991", "Joshua Bloch", "Programming", "");
        when(bookRepository.searchActive("java")).thenReturn(List.of(book));

        assertThat(bookService.search(" java ")).containsExactly(book);
        verify(bookRepository).searchActive("java");
    }
}
