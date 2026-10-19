package com.elibrary.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.elibrary.domain.Book;
import com.elibrary.domain.BookCopy;
import com.elibrary.domain.BorrowRequest;
import com.elibrary.domain.BorrowRequestStatus;
import com.elibrary.domain.CopyStatus;
import com.elibrary.domain.Loan;
import com.elibrary.domain.Role;
import com.elibrary.domain.UserAccount;
import com.elibrary.repository.BookCopyRepository;
import com.elibrary.repository.BookRepository;
import com.elibrary.repository.BorrowRequestRepository;
import com.elibrary.repository.LoanRepository;
import com.elibrary.repository.UserAccountRepository;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class CirculationServiceTest {

    private static final Clock FIXED_CLOCK = Clock.fixed(
            Instant.parse("2026-02-17T10:00:00Z"), ZoneId.of("UTC"));

    @Mock private BorrowRequestRepository requestRepository;
    @Mock private LoanRepository loanRepository;
    @Mock private UserAccountRepository userRepository;
    @Mock private BookRepository bookRepository;
    @Mock private BookCopyRepository copyRepository;

    private CirculationService circulationService;

    @BeforeEach
    void setUp() {
        circulationService = new CirculationService(requestRepository, loanRepository,
                userRepository, bookRepository, copyRepository, FIXED_CLOCK);
    }

    @Test
    void approvingPendingRequestCreatesLoanAndUpdatesCopyAndRequest() {
        UserAccount member = member(11L);
        Book book = book(21L);
        BookCopy copy = new BookCopy("CC-001", book);
        BorrowRequest request = new BorrowRequest(member, book,
                LocalDate.now(FIXED_CLOCK).minusDays(1).atStartOfDay(), null);
        when(requestRepository.findByIdForUpdate(31L)).thenReturn(Optional.of(request));
        when(userRepository.findByIdForUpdate(11L)).thenReturn(Optional.of(member));
        when(loanRepository.existsByMemberIdAndReturnedAtIsNullAndDueDateBefore(11L, LocalDate.now(FIXED_CLOCK)))
                .thenReturn(false);
        when(loanRepository.countByMemberIdAndReturnedAtIsNull(11L)).thenReturn(0L);
        when(copyRepository.findFirstByBookIdAndStatusOrderByCopyCodeAsc(21L, CopyStatus.AVAILABLE))
                .thenReturn(Optional.of(copy));
        when(loanRepository.save(any(Loan.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Loan loan = circulationService.approveRequest(31L);

        assertThat(loan.getMember()).isSameAs(member);
        assertThat(loan.getCopy()).isSameAs(copy);
        assertThat(loan.getIssuedAt()).isEqualTo(LocalDateTime.now(FIXED_CLOCK));
        assertThat(loan.getDueDate()).isEqualTo(LocalDate.now(FIXED_CLOCK).plusDays(14));
        assertThat(copy.getStatus()).isEqualTo(CopyStatus.ON_LOAN);
        assertThat(request.getStatus()).isEqualTo(BorrowRequestStatus.APPROVED);
    }

    @Test
    void memberCanSubmitOnePendingRequestForAnActiveBook() {
        UserAccount member = member(11L);
        Book book = book(21L);
        when(userRepository.findByUsername("member")).thenReturn(Optional.of(member));
        when(bookRepository.findById(21L)).thenReturn(Optional.of(book));
        when(requestRepository.existsByMemberIdAndBookIdAndStatus(11L, 21L, BorrowRequestStatus.PENDING))
                .thenReturn(false);
        when(requestRepository.save(any(BorrowRequest.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BorrowRequest request = circulationService.submitRequest("member", 21L, "Please hold this title.");

        assertThat(request.getStatus()).isEqualTo(BorrowRequestStatus.PENDING);
        assertThat(request.getMemberNote()).isEqualTo("Please hold this title.");
    }

    @Test
    void duplicatePendingRequestIsRejected() {
        UserAccount member = member(11L);
        when(userRepository.findByUsername("member")).thenReturn(Optional.of(member));
        when(bookRepository.findById(21L)).thenReturn(Optional.of(book(21L)));
        when(requestRepository.existsByMemberIdAndBookIdAndStatus(11L, 21L, BorrowRequestStatus.PENDING))
                .thenReturn(true);

        assertThatThrownBy(() -> circulationService.submitRequest("member", 21L, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("pending request");
        verify(requestRepository, never()).save(any(BorrowRequest.class));
    }

    @Test
    void memberNoteLongerThanLimitIsRejected() {
        when(userRepository.findByUsername("member")).thenReturn(Optional.of(member(11L)));
        when(bookRepository.findById(21L)).thenReturn(Optional.of(book(21L)));
        when(requestRepository.existsByMemberIdAndBookIdAndStatus(11L, 21L, BorrowRequestStatus.PENDING))
                .thenReturn(false);

        assertThatThrownBy(() -> circulationService.submitRequest("member", 21L, "n".repeat(501)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("500 characters");

        verify(requestRepository, never()).save(any(BorrowRequest.class));
    }

    @Test
    void librarianCanRejectPendingRequestWithDecisionNote() {
        BorrowRequest request = new BorrowRequest(member(11L), book(21L),
                LocalDate.now(FIXED_CLOCK).atStartOfDay(), null);
        when(requestRepository.findByIdForUpdate(31L)).thenReturn(Optional.of(request));

        circulationService.rejectRequest(31L, "Please check availability next week.");

        assertThat(request.getStatus()).isEqualTo(BorrowRequestStatus.REJECTED);
        assertThat(request.getDecisionNote()).isEqualTo("Please check availability next week.");
    }

    @Test
    void approvalIsRejectedWhenMemberHasOverdueLoan() {
        UserAccount member = member(11L);
        BorrowRequest request = new BorrowRequest(member, book(21L), LocalDate.now(FIXED_CLOCK).atStartOfDay(), null);
        when(requestRepository.findByIdForUpdate(31L)).thenReturn(Optional.of(request));
        when(userRepository.findByIdForUpdate(11L)).thenReturn(Optional.of(member));
        when(loanRepository.existsByMemberIdAndReturnedAtIsNullAndDueDateBefore(11L, LocalDate.now(FIXED_CLOCK)))
                .thenReturn(true);

        assertThatThrownBy(() -> circulationService.approveRequest(31L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("overdue");
        verify(copyRepository, never()).findFirstByBookIdAndStatusOrderByCopyCodeAsc(21L, CopyStatus.AVAILABLE);
        assertThat(request.getStatus()).isEqualTo(BorrowRequestStatus.PENDING);
    }

    @Test
    void approvalIsRejectedWhenBookWasDeactivatedAfterRequest() {
        UserAccount member = member(11L);
        Book inactiveBook = book(21L);
        inactiveBook.setActive(false);
        BorrowRequest request = new BorrowRequest(member, inactiveBook,
                LocalDate.now(FIXED_CLOCK).atStartOfDay(), null);
        when(requestRepository.findByIdForUpdate(31L)).thenReturn(Optional.of(request));

        assertThatThrownBy(() -> circulationService.approveRequest(31L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("deactivated book");

        verify(userRepository, never()).findByIdForUpdate(11L);
        assertThat(request.getStatus()).isEqualTo(BorrowRequestStatus.PENDING);
    }

    @Test
    void approvalIsRejectedWhenMemberReachedFiveActiveLoans() {
        UserAccount member = member(11L);
        BorrowRequest request = new BorrowRequest(member, book(21L), LocalDate.now(FIXED_CLOCK).atStartOfDay(), null);
        when(requestRepository.findByIdForUpdate(31L)).thenReturn(Optional.of(request));
        when(userRepository.findByIdForUpdate(11L)).thenReturn(Optional.of(member));
        when(loanRepository.existsByMemberIdAndReturnedAtIsNullAndDueDateBefore(11L, LocalDate.now(FIXED_CLOCK)))
                .thenReturn(false);
        when(loanRepository.countByMemberIdAndReturnedAtIsNull(11L)).thenReturn(5L);

        assertThatThrownBy(() -> circulationService.approveRequest(31L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("limit of five");
        verify(copyRepository, never()).findFirstByBookIdAndStatusOrderByCopyCodeAsc(21L, CopyStatus.AVAILABLE);
    }

    @Test
    void approvalIsRejectedWhenNoAvailableCopyExists() {
        UserAccount member = member(11L);
        BorrowRequest request = new BorrowRequest(member, book(21L), LocalDate.now(FIXED_CLOCK).atStartOfDay(), null);
        when(requestRepository.findByIdForUpdate(31L)).thenReturn(Optional.of(request));
        when(userRepository.findByIdForUpdate(11L)).thenReturn(Optional.of(member));
        when(loanRepository.existsByMemberIdAndReturnedAtIsNullAndDueDateBefore(11L, LocalDate.now(FIXED_CLOCK)))
                .thenReturn(false);
        when(loanRepository.countByMemberIdAndReturnedAtIsNull(11L)).thenReturn(0L);
        when(copyRepository.findFirstByBookIdAndStatusOrderByCopyCodeAsc(21L, CopyStatus.AVAILABLE))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> circulationService.approveRequest(31L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("No available copy");
        verify(loanRepository, never()).save(any(Loan.class));
    }

    @Test
    void returnCalculatesFiveThousandVndForEachLateDay() {
        BookCopy copy = new BookCopy("CC-001", book(21L));
        Loan loan = new Loan(member(11L), copy, null,
                LocalDate.now(FIXED_CLOCK).minusDays(17).atStartOfDay(),
                LocalDate.now(FIXED_CLOCK).minusDays(3));
        when(loanRepository.findLockedById(41L)).thenReturn(Optional.of(loan));

        Loan returned = circulationService.returnLoan(41L);

        assertThat(returned.getFineAmount()).isEqualByComparingTo(new BigDecimal("15000.00"));
        assertThat(returned.getReturnedAt()).isEqualTo(LocalDateTime.now(FIXED_CLOCK));
        assertThat(copy.getStatus()).isEqualTo(CopyStatus.AVAILABLE);
    }

    @Test
    void returnOnDueDateHasNoFine() {
        BookCopy copy = new BookCopy("CC-001", book(21L));
        Loan loan = new Loan(member(11L), copy, null,
                LocalDate.now(FIXED_CLOCK).minusDays(14).atStartOfDay(), LocalDate.now(FIXED_CLOCK));
        when(loanRepository.findLockedById(41L)).thenReturn(Optional.of(loan));

        circulationService.returnLoan(41L);

        assertThat(loan.getFineAmount()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void secondReturnIsRejected() {
        BookCopy copy = new BookCopy("CC-001", book(21L));
        Loan loan = new Loan(member(11L), copy, null,
                LocalDate.now(FIXED_CLOCK).minusDays(14).atStartOfDay(), LocalDate.now(FIXED_CLOCK));
        loan.markReturned(LocalDateTime.now(FIXED_CLOCK), BigDecimal.ZERO);
        when(loanRepository.findLockedById(41L)).thenReturn(Optional.of(loan));

        assertThatThrownBy(() -> circulationService.returnLoan(41L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already been returned");
    }

    private UserAccount member(Long id) {
        UserAccount member = new UserAccount("member", "hash", "Test Member", Role.MEMBER);
        ReflectionTestUtils.setField(member, "id", id);
        return member;
    }

    private Book book(Long id) {
        Book book = new Book("Clean Code", "9780132350884", "Robert C. Martin", "Software", "");
        ReflectionTestUtils.setField(book, "id", id);
        return book;
    }
}
