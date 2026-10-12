package com.elibrary.service;

import com.elibrary.domain.Book;
import com.elibrary.domain.BookCopy;
import com.elibrary.domain.CopyStatus;
import com.elibrary.repository.BookCopyRepository;
import com.elibrary.repository.BookRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final BookCopyRepository copyRepository;

    public BookService(BookRepository bookRepository, BookCopyRepository copyRepository) {
        this.bookRepository = bookRepository;
        this.copyRepository = copyRepository;
    }

    @Transactional(readOnly = true)
    public List<Book> search(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return bookRepository.findAllByActiveTrueOrderByTitleAsc();
        }
        return bookRepository.searchActive(keyword.trim());
    }

    @Transactional(readOnly = true)
    public Book getRequired(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Book was not found."));
    }

    @Transactional(readOnly = true)
    public List<BookCopy> getCopies(Long bookId) {
        return copyRepository.findByBookIdOrderByCopyCode(bookId);
    }

    @Transactional(readOnly = true)
    public long countAvailableCopies(Long bookId) {
        return copyRepository.countByBookIdAndStatus(bookId, CopyStatus.AVAILABLE);
    }

    @Transactional
    public Book save(Book book) {
        String isbn = book.getIsbn().trim();
        book.setIsbn(isbn);
        book.setTitle(book.getTitle().trim());
        book.setAuthor(book.getAuthor().trim());
        book.setCategory(book.getCategory().trim());
        bookRepository.findByIsbn(isbn).ifPresent(existing -> {
            if (book.getId() == null || !existing.getId().equals(book.getId())) {
                throw new IllegalArgumentException("ISBN already belongs to another book.");
            }
        });
        return bookRepository.save(book);
    }

    @Transactional
    public void deactivate(Long id) {
        Book book = getRequired(id);
        book.setActive(false);
        bookRepository.save(book);
    }
}
