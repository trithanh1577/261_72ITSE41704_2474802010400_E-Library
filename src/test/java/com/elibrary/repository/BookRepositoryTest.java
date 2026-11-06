package com.elibrary.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.elibrary.domain.Book;
import java.util.stream.IntStream;
import java.util.stream.Collectors;
import org.springframework.data.domain.PageRequest;
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

        assertThat(bookRepository.searchActive("MARTIN", PageRequest.of(0, 20))).hasSize(1);
        assertThat(bookRepository.searchActive("978026", PageRequest.of(0, 20))).hasSize(1);
        assertThat(bookRepository.searchActive("fantasy", PageRequest.of(0, 20))).hasSize(1);
    }

    @Test
    void catalogueQueriesReturnOnlyTheRequestedPage() {
        bookRepository.saveAll(IntStream.range(0, 25)
                .mapToObj(index -> new Book("Book " + index, "ISBN-" + index,
                        "Test Author", "Testing", ""))
                .collect(Collectors.toList()));

        var firstPage = bookRepository.findAllByActiveTrueOrderByTitleAsc(PageRequest.of(0, 24));
        var secondPage = bookRepository.findAllByActiveTrueOrderByTitleAsc(PageRequest.of(1, 24));

        assertThat(firstPage.getContent()).hasSize(24);
        assertThat(firstPage.getTotalElements()).isEqualTo(25);
        assertThat(secondPage.getContent()).hasSize(1);
    }
}
