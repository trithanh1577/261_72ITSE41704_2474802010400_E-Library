package com.elibrary.service;

import com.elibrary.domain.Book;
import com.elibrary.domain.BookCopy;
import com.elibrary.domain.CopyStatus;
import com.elibrary.repository.BookCopyRepository;
import com.elibrary.repository.BookRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookCopyService {

    private final BookCopyRepository copyRepository;
    private final BookRepository bookRepository;

    public BookCopyService(BookCopyRepository copyRepository, BookRepository bookRepository) {
        this.copyRepository = copyRepository;
        this.bookRepository = bookRepository;
    }

    @Transactional
    public BookCopy addCopy(Long bookId, String copyCode) {
        String normalisedCode = copyCode.trim();
        if (copyRepository.existsByCopyCode(normalisedCode)) {
            throw new IllegalArgumentException("Copy code already exists.");
        }
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book was not found."));
        return copyRepository.save(new BookCopy(normalisedCode, book));
    }

    @Transactional(readOnly = true)
    public BookCopy getRequired(Long id) {
        return copyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Book copy was not found."));
    }

    @Transactional
    public BookCopy update(Long id, String copyCode, CopyStatus status) {
        BookCopy copy = getRequired(id);
        String normalisedCode = copyCode.trim();
        copyRepository.findByCopyCode(normalisedCode).ifPresent(existing -> {
            if (!existing.getId().equals(id)) {
                throw new IllegalArgumentException("Copy code already belongs to another copy.");
            }
        });
        copy.setCopyCode(normalisedCode);
        copy.setStatus(status);
        return copyRepository.save(copy);
    }
}
