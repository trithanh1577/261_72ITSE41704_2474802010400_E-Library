package com.elibrary.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.elibrary.domain.Book;
import com.elibrary.domain.BookCopy;
import com.elibrary.domain.CopyStatus;
import com.elibrary.repository.BookCopyRepository;
import com.elibrary.repository.BookRepository;
import com.elibrary.repository.LoanRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class BookCopyServiceTest {

    @Mock private BookCopyRepository copyRepository;
    @Mock private BookRepository bookRepository;
    @Mock private LoanRepository loanRepository;
    @InjectMocks private BookCopyService copyService;

    @Test
    void librarianCannotMakeAnActivelyBorrowedCopyAvailableByEditingIt() {
        BookCopy copy = new BookCopy("CC-001", new Book("Clean Code", "9780132350884",
                "Robert C. Martin", "Software", ""));
        ReflectionTestUtils.setField(copy, "id", 10L);
        when(copyRepository.findById(10L)).thenReturn(Optional.of(copy));
        when(copyRepository.findByCopyCode("CC-001")).thenReturn(Optional.of(copy));
        when(loanRepository.existsByCopyIdAndReturnedAtIsNull(10L)).thenReturn(true);

        assertThatThrownBy(() -> copyService.update(10L, "CC-001", CopyStatus.AVAILABLE))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("active loan");

        verify(copyRepository, never()).save(copy);
    }

    @Test
    void librarianCannotSetReservedStatusBeforeReservationWorkflowExists() {
        BookCopy copy = new BookCopy("CC-001", new Book("Clean Code", "9780132350884",
                "Robert C. Martin", "Software", ""));
        ReflectionTestUtils.setField(copy, "id", 10L);
        when(copyRepository.findById(10L)).thenReturn(Optional.of(copy));
        when(copyRepository.findByCopyCode("CC-001")).thenReturn(Optional.of(copy));
        when(loanRepository.existsByCopyIdAndReturnedAtIsNull(10L)).thenReturn(false);

        assertThatThrownBy(() -> copyService.update(10L, "CC-001", CopyStatus.RESERVED))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("reservation workflows");

        verify(copyRepository, never()).save(copy);
    }
}
