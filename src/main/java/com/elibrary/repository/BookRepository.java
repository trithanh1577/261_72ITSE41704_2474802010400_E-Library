package com.elibrary.repository;

import com.elibrary.domain.Book;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookRepository extends JpaRepository<Book, Long> {

    List<Book> findAllByActiveTrueOrderByTitleAsc();

    Optional<Book> findByIsbn(String isbn);

    @Query("""
            select b from Book b
            where b.active = true and (
                lower(b.title) like lower(concat('%', :keyword, '%')) or
                lower(b.author) like lower(concat('%', :keyword, '%')) or
                lower(b.isbn) like lower(concat('%', :keyword, '%')) or
                lower(b.category) like lower(concat('%', :keyword, '%'))
            )
            order by b.title asc
            """)
    List<Book> searchActive(@Param("keyword") String keyword);
}
