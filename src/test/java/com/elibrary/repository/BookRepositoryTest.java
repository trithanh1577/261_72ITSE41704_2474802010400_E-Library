package com.elibrary.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.elibrary.domain.Book;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class BookRepositoryTest {

    @Autowired private BookRepository bookRepository;

    @Test
    void searchFindsTitleAuthorIsbnAndCategoryCaseInsensitively() {
        bookRepository.save(new Book("Clean Code", "9780132350884", "Robert C. Martin", "Software", ""));
        bookRepository.save(new Book("The Hobbit", "9780261102217", "J. R. R. Tolkien", "Fantasy", ""));

        assertThat(bookRepository.searchActive("MARTIN")).hasSize(1);
        assertThat(bookRepository.searchActive("978026")).hasSize(1);
        assertThat(bookRepository.searchActive("fantasy")).hasSize(1);
    }
}
