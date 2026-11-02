package com.elibrary.service;

import com.elibrary.domain.Book;
import com.elibrary.domain.BookCopy;
import com.elibrary.domain.BorrowRequest;
import com.elibrary.domain.BorrowRequestStatus;
import com.elibrary.domain.CopyStatus;
import com.elibrary.domain.Loan;
import com.elibrary.domain.Reservation;
import com.elibrary.domain.ReservationStatus;
import com.elibrary.domain.Role;
import com.elibrary.domain.MembershipType;
import com.elibrary.domain.UserAccount;
import com.elibrary.repository.BookCopyRepository;
import com.elibrary.repository.BookRepository;
import com.elibrary.repository.BorrowRequestRepository;
import com.elibrary.repository.LoanRepository;
import com.elibrary.repository.ReservationRepository;
import com.elibrary.repository.UserAccountRepository;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CirculationService {

    static final BigDecimal FINE_PER_LATE_DAY = new BigDecimal("5000.00");
    static final BigDecimal MAX_FINE_PER_LOAN = new BigDecimal("100000.00");
    static final int RESERVATION_HOLD_HOURS = 48;

    private final BorrowRequestRepository requestRepository;
    private final LoanRepository loanRepository;
    private final UserAccountRepository userRepository;
    private final BookRepository bookRepository;
    private final BookCopyRepository copyRepository;
    private final ReservationRepository reservationRepository;
    private final Clock clock;
    private final Set<LocalDate> holidays;

    @Autowired
    public CirculationService(BorrowRequestRepository requestRepository,
                              LoanRepository loanRepository,
                              UserAccountRepository userRepository,
                              BookRepository bookRepository,
                              BookCopyRepository copyRepository,
                              ReservationRepository reservationRepository,
                              @Value("${elibrary.holidays:}") String configuredHolidays) {
        this(requestRepository, loanRepository, userRepository, bookRepository, copyRepository,
                reservationRepository, Clock.systemDefaultZone(), parseHolidays(configuredHolidays));
    }

    CirculationService(BorrowRequestRepository requestRepository,
                       LoanRepository loanRepository,
                       UserAccountRepository userRepository,
                       BookRepository bookRepository,
                       BookCopyRepository copyRepository,
                       ReservationRepository reservationRepository,
                       Clock clock,
                       Set<LocalDate> holidays) {
        this.requestRepository = requestRepository;
        this.loanRepository = loanRepository;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
        this.copyRepository = copyRepository;
        this.reservationRepository = reservationRepository;
        this.clock = clock;
        this.holidays = holidays;
    }

    CirculationService(BorrowRequestRepository requestRepository,
                       LoanRepository loanRepository,
                       UserAccountRepository userRepository,
                       BookRepository bookRepository,
                       BookCopyRepository copyRepository,
                       Clock clock) {
        this(requestRepository, loanRepository, userRepository, bookRepository, copyRepository,
                null, clock, Set.of());
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
        refreshReservationQueues();
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
                LocalDate.now(clock).plusDays(member.getMembershipType().getLoanPeriodDays())));
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
        long lateDays = countChargeableLateDays(loan.getDueDate(), returnDate);
        BigDecimal fine = FINE_PER_LATE_DAY.multiply(BigDecimal.valueOf(lateDays))
                .min(MAX_FINE_PER_LOAN);
        loan.markReturned(returnedAt, fine);
        loan.getCopy().setStatus(CopyStatus.AVAILABLE);
        dispatchForBook(loan.getCopy().getBook().getId());
        return loan;
    }

    @Transactional
    public Reservation submitReservation(String username, Long bookId) {
        refreshReservationQueues();
        UserAccount member = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Member account was not found."));
        validateEligibility(member);
        Book book = bookRepository.findById(bookId)
                .filter(Book::isActive)
                .orElseThrow(() -> new IllegalArgumentException("Active book was not found."));
        if (copyRepository.countByBookIdAndStatus(bookId, CopyStatus.AVAILABLE) > 0) {
            throw new IllegalArgumentException("A copy is available. Submit a borrowing request instead.");
        }
        if (reservationRepository.existsByMemberIdAndBookIdAndStatusIn(member.getId(), bookId,
                List.of(ReservationStatus.WAITING, ReservationStatus.READY))) {
            throw new IllegalArgumentException("You already have an active reservation for this book.");
        }
        Reservation reservation = reservationRepository.save(new Reservation(member, book, LocalDateTime.now(clock)));
        dispatchForBook(bookId);
        return reservation;
    }

    @Transactional
    public Loan claimReservation(Long reservationId, String username) {
        refreshReservationQueues();
        Reservation reservation = reservationRepository.findByIdForUpdate(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation was not found."));
        if (!reservation.getMember().getUsername().equals(username)) {
            throw new IllegalArgumentException("This reservation belongs to another member.");
        }
        if (reservation.getStatus() != ReservationStatus.READY
                || reservation.getReadyUntil() == null
                || !reservation.getReadyUntil().isAfter(LocalDateTime.now(clock))) {
            throw new IllegalArgumentException("This reservation is not ready to be collected.");
        }
        UserAccount member = userRepository.findByIdForUpdate(reservation.getMember().getId())
                .orElseThrow(() -> new IllegalArgumentException("Member account was not found."));
        validateEligibility(member);
        BookCopy copy = reservation.getCopy();
        if (copy == null || copy.getStatus() != CopyStatus.RESERVED) {
            throw new IllegalArgumentException("The reserved copy is no longer available.");
        }
        copy.setStatus(CopyStatus.ON_LOAN);
        LocalDateTime now = LocalDateTime.now(clock);
        Loan loan = loanRepository.save(new Loan(member, copy, null, now,
                LocalDate.now(clock).plusDays(member.getMembershipType().getLoanPeriodDays())));
        reservation.fulfill();
        return loan;
    }

    @Transactional
    public void cancelReservation(Long reservationId, String username) {
        refreshReservationQueues();
        Reservation reservation = reservationRepository.findByIdForUpdate(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation was not found."));
        if (!reservation.getMember().getUsername().equals(username)) {
            throw new IllegalArgumentException("This reservation belongs to another member.");
        }
        if (reservation.getStatus() != ReservationStatus.WAITING
                && reservation.getStatus() != ReservationStatus.READY) {
            throw new IllegalArgumentException("Only waiting or ready reservations can be cancelled.");
        }
        Long bookId = reservation.getBook().getId();
        if (reservation.getStatus() == ReservationStatus.READY && reservation.getCopy() != null) {
            reservation.getCopy().setStatus(CopyStatus.AVAILABLE);
        }
        reservation.cancel();
        dispatchForBook(bookId);
    }

    @Transactional
    public List<Reservation> listReservations() {
        refreshReservationQueues();
        return reservationRepository.findAllByOrderByReservedAtAsc();
    }

    @Transactional
    public List<Reservation> listReservationsForMember(String username) {
        refreshReservationQueues();
        return reservationRepository.findByMemberUsernameOrderByReservedAtDesc(username);
    }

    @Transactional
    public long waitingReservationCount(Long bookId) {
        refreshReservationQueues();
        return reservationRepository.countByBookIdAndStatus(bookId, ReservationStatus.WAITING);
    }

    @Transactional
    public ReservationStatus activeReservationStatus(String username, Long bookId) {
        refreshReservationQueues();
        return reservationRepository.findByMemberUsernameOrderByReservedAtDesc(username).stream()
                .filter(r -> r.getBook().getId().equals(bookId))
                .map(Reservation::getStatus)
                .filter(s -> s == ReservationStatus.WAITING || s == ReservationStatus.READY)
                .findFirst().orElse(null);
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
        int maximumLoans = member.getMembershipType().getMaximumActiveLoans();
        if (loanRepository.countByMemberIdAndReturnedAtIsNull(member.getId()) >= maximumLoans) {
            throw new IllegalArgumentException("Member has reached the active-loan limit for their membership type ("
                    + maximumLoans + ").");
        }
    }

    private void refreshReservationQueues() {
        if (reservationRepository == null) return;
        LocalDateTime now = LocalDateTime.now(clock);
        List<Reservation> expired = reservationRepository.findExpiredReadyForUpdate(ReservationStatus.READY, now);
        Set<Long> affectedBooks = new LinkedHashSet<>();
        for (Reservation reservation : expired) {
            if (reservation.getCopy() != null) {
                reservation.getCopy().setStatus(CopyStatus.AVAILABLE);
            }
            reservation.expire();
            affectedBooks.add(reservation.getBook().getId());
        }
        affectedBooks.addAll(reservationRepository.findBookIdsWithReservations(ReservationStatus.WAITING));
        affectedBooks.forEach(this::dispatchForBook);
    }

    private void dispatchForBook(Long bookId) {
        if (reservationRepository == null) return;
        LocalDateTime readyUntil = LocalDateTime.now(clock).plusHours(RESERVATION_HOLD_HOURS);
        List<Reservation> waiting = reservationRepository.findWaitingForUpdate(bookId, ReservationStatus.WAITING);
        for (Reservation reservation : waiting) {
            try {
                validateEligibility(reservation.getMember());
            } catch (IllegalArgumentException exception) {
                reservation.skip();
                continue;
            }
            var availableCopy = copyRepository.findFirstByBookIdAndStatusOrderByCopyCodeAsc(bookId, CopyStatus.AVAILABLE);
            if (availableCopy.isEmpty()) {
                break;
            }
            BookCopy copy = availableCopy.get();
            copy.setStatus(CopyStatus.RESERVED);
            reservation.markReady(copy, readyUntil);
        }
    }

    private long countChargeableLateDays(LocalDate dueDate, LocalDate returnDate) {
        if (!returnDate.isAfter(dueDate)) return 0;
        long chargeable = 0;
        for (LocalDate date = dueDate.plusDays(1); !date.isAfter(returnDate); date = date.plusDays(1)) {
            if (!holidays.contains(date)) chargeable++;
        }
        return chargeable;
    }

    private static Set<LocalDate> parseHolidays(String configuredDates) {
        Set<LocalDate> dates = new LinkedHashSet<>();
        if (configuredDates == null || configuredDates.isBlank()) return dates;
        for (String value : configuredDates.split(",")) {
            if (!value.isBlank()) dates.add(LocalDate.parse(value.trim()));
        }
        return dates;
    }
}
