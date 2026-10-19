package com.elibrary.service;

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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CirculationService {

    static final int MAX_ACTIVE_LOANS = 5;
    static final int LOAN_PERIOD_DAYS = 14;
    static final BigDecimal FINE_PER_LATE_DAY = new BigDecimal("5000.00");

    private final BorrowRequestRepository requestRepository;
    private final LoanRepository loanRepository;
    private final UserAccountRepository userRepository;
    private final BookRepository bookRepository;
    private final BookCopyRepository copyRepository;
    private final Clock clock;

    public CirculationService(BorrowRequestRepository requestRepository,
                              LoanRepository loanRepository,
                              UserAccountRepository userRepository,
                              BookRepository bookRepository,
                              BookCopyRepository copyRepository) {
        this(requestRepository, loanRepository, userRepository, bookRepository, copyRepository, Clock.systemDefaultZone());
    }

    CirculationService(BorrowRequestRepository requestRepository,
                       LoanRepository loanRepository,
                       UserAccountRepository userRepository,
                       BookRepository bookRepository,
                       BookCopyRepository copyRepository,
                       Clock clock) {
        this.requestRepository = requestRepository;
        this.loanRepository = loanRepository;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
        this.copyRepository = copyRepository;
        this.clock = clock;
    }

    @Transactional
    public BorrowRequest submitRequest(String username, Long bookId, String note) {
        UserAccount member = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Member account was not found."));
        if (!member.isEnabled() || member.getRole() != Role.MEMBER) {
            throw new IllegalArgumentException("Only an enabled library member can submit a borrowing request.");
        }
        Book book = bookRepository.findById(bookId)
                .filter(Book::isActive)
                .orElseThrow(() -> new IllegalArgumentException("Active book was not found."));
        if (requestRepository.existsByMemberIdAndBookIdAndStatus(member.getId(), bookId, BorrowRequestStatus.PENDING)) {
            throw new IllegalArgumentException("You already have a pending request for this book.");
        }
        String safeNote = note == null || note.isBlank() ? null : note.trim();
        if (safeNote != null && safeNote.length() > 500) {
            throw new IllegalArgumentException("Request note must be at most 500 characters.");
        }
        return requestRepository.save(new BorrowRequest(member, book, LocalDateTime.now(clock), safeNote));
    }

    @Transactional
    public Loan approveRequest(Long requestId) {
        BorrowRequest request = requestRepository.findByIdForUpdate(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Borrowing request was not found."));
        if (request.getStatus() != BorrowRequestStatus.PENDING) {
            throw new IllegalArgumentException("Only pending requests can be approved.");
        }
        if (!request.getBook().isActive()) {
            throw new IllegalArgumentException("A deactivated book cannot be issued.");
        }

        UserAccount member = userRepository.findByIdForUpdate(request.getMember().getId())
                .orElseThrow(() -> new IllegalArgumentException("Member account was not found."));
        validateEligibility(member);
        BookCopy copy = copyRepository.findFirstByBookIdAndStatusOrderByCopyCodeAsc(
                        request.getBook().getId(), CopyStatus.AVAILABLE)
                .orElseThrow(() -> new IllegalArgumentException("No available copy remains for this book."));

        copy.setStatus(CopyStatus.ON_LOAN);
        LocalDateTime now = LocalDateTime.now(clock);
        Loan loan = loanRepository.save(new Loan(member, copy, request, now,
                LocalDate.now(clock).plusDays(LOAN_PERIOD_DAYS)));
        request.approve();
        return loan;
    }

    @Transactional
    public void rejectRequest(Long requestId, String reason) {
        BorrowRequest request = requestRepository.findByIdForUpdate(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Borrowing request was not found."));
        if (request.getStatus() != BorrowRequestStatus.PENDING) {
            throw new IllegalArgumentException("Only pending requests can be rejected.");
        }
        String note = reason == null || reason.isBlank() ? "Request rejected by librarian." : reason.trim();
        if (note.length() > 500) {
            throw new IllegalArgumentException("Decision note must be at most 500 characters.");
        }
        request.reject(note);
    }

    @Transactional
    public Loan returnLoan(Long loanId) {
        Loan loan = loanRepository.findLockedById(loanId)
                .orElseThrow(() -> new IllegalArgumentException("Loan was not found."));
        if (!loan.isActive()) {
            throw new IllegalArgumentException("This loan has already been returned.");
        }
        LocalDateTime returnedAt = LocalDateTime.now(clock);
        LocalDate returnDate = returnedAt.toLocalDate();
        long lateDays = returnDate.isAfter(loan.getDueDate())
                ? ChronoUnit.DAYS.between(loan.getDueDate(), returnDate) : 0;
        BigDecimal fine = FINE_PER_LATE_DAY.multiply(BigDecimal.valueOf(lateDays));
        loan.markReturned(returnedAt, fine);
        loan.getCopy().setStatus(CopyStatus.AVAILABLE);
        return loan;
    }

    @Transactional(readOnly = true)
    public List<BorrowRequest> listRequests() {
        return requestRepository.findAllByOrderByRequestedAtDesc();
    }

    @Transactional(readOnly = true)
    public List<BorrowRequest> listRequestsForMember(String username) {
        return requestRepository.findByMemberUsernameOrderByRequestedAtDesc(username);
    }

    @Transactional(readOnly = true)
    public List<Loan> listLoans() {
        return loanRepository.findAllByOrderByIssuedAtDesc();
    }

    @Transactional(readOnly = true)
    public List<Loan> listLoansForMember(String username) {
        return loanRepository.findByMemberUsernameOrderByIssuedAtDesc(username);
    }

    private void validateEligibility(UserAccount member) {
        if (!member.isEnabled() || member.getRole() != Role.MEMBER) {
            throw new IllegalArgumentException("This account cannot borrow books.");
        }
        if (loanRepository.existsByMemberIdAndReturnedAtIsNullAndDueDateBefore(member.getId(), LocalDate.now(clock))) {
            throw new IllegalArgumentException("Member has an overdue loan and cannot borrow another book.");
        }
        if (loanRepository.countByMemberIdAndReturnedAtIsNull(member.getId()) >= MAX_ACTIVE_LOANS) {
            throw new IllegalArgumentException("Member has reached the limit of five active loans.");
        }
    }
}
