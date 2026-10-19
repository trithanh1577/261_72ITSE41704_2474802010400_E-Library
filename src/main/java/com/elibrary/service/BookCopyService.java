package com.elibrary.service;

import com.elibrary.domain.Book;
import com.elibrary.domain.BookCopy;
import com.elibrary.domain.CopyStatus;
import com.elibrary.repository.BookCopyRepository;
import com.elibrary.repository.BookRepository;
import com.elibrary.repository.LoanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookCopyService {

    private final BookCopyRepository copyRepository;
    private final BookRepository bookRepository;
    private final LoanRepository loanRepository;

    public BookCopyService(BookCopyRepository copyRepository, BookRepository bookRepository,
                           LoanRepository loanRepository) {
        this.copyRepository = copyRepository;
        this.bookRepository = bookRepository;
        this.loanRepository = loanRepository;
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
        boolean hasActiveLoan = loanRepository.existsByCopyIdAndReturnedAtIsNull(id);
        if (hasActiveLoan && status != CopyStatus.ON_LOAN) {
            throw new IllegalArgumentException("A copy with an active loan can only be returned through the loan workflow.");
        }
        if (!hasActiveLoan && (status == CopyStatus.ON_LOAN || status == CopyStatus.RESERVED)) {
            throw new IllegalArgumentException("Loan and reservation workflows control ON_LOAN and RESERVED copy statuses.");
        }
        copy.setCopyCode(normalisedCode);
        copy.setStatus(status);
        return copyRepository.save(copy);
    }
}
