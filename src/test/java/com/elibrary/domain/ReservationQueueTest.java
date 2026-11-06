package com.elibrary.domain;

import static org.assertj.core.api.Assertions.assertThat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.Test;

class ReservationQueueTest {

    @Test
    void assignsTheAvailableCopyToTheOldestEligibleMember() {
        UserAccount firstMember = new UserAccount("first", "hash", "First", Role.MEMBER);
        UserAccount secondMember = new UserAccount("second", "hash", "Second", Role.MEMBER);
        Book book = new Book("Title", "ISBN-1", "Author", "Category", "");
        LocalDateTime now = LocalDateTime.of(2026, 3, 1, 12, 0);
        Reservation first = new Reservation(firstMember, book, now.minusHours(2));
        Reservation second = new Reservation(secondMember, book, now.minusHours(1));
        BookCopy copy = new BookCopy("COPY-1", book);
        AtomicBoolean copyAvailable = new AtomicBoolean(true);

        int assigned = new ReservationQueue(List.of(second, first)).dispatch(
                () -> copyAvailable.getAndSet(false) ? Optional.of(copy) : Optional.empty(),
                member -> true, now.plusHours(48));

        assertThat(assigned).isEqualTo(1);
        assertThat(first.getStatus()).isEqualTo(ReservationStatus.READY);
        assertThat(first.getCopy()).isSameAs(copy);
        assertThat(second.getStatus()).isEqualTo(ReservationStatus.WAITING);
    }

    @Test
    void skipsIneligibleMemberAndContinuesToTheNextWaitingMember() {
        UserAccount firstMember = new UserAccount("first", "hash", "First", Role.MEMBER);
        UserAccount secondMember = new UserAccount("second", "hash", "Second", Role.MEMBER);
        Book book = new Book("Title", "ISBN-2", "Author", "Category", "");
        LocalDateTime now = LocalDateTime.of(2026, 3, 1, 12, 0);
        Reservation first = new Reservation(firstMember, book, now.minusHours(2));
        Reservation second = new Reservation(secondMember, book, now.minusHours(1));
        BookCopy copy = new BookCopy("COPY-2", book);

        int assigned = new ReservationQueue(List.of(first, second)).dispatch(
                () -> Optional.of(copy), member -> member == secondMember, now.plusHours(48));

        assertThat(assigned).isEqualTo(1);
        assertThat(first.getStatus()).isEqualTo(ReservationStatus.SKIPPED);
        assertThat(second.getStatus()).isEqualTo(ReservationStatus.READY);
        assertThat(copy.getStatus()).isEqualTo(CopyStatus.RESERVED);
    }

    @Test
    void stopsWhenNoCopyIsAvailable() {
        UserAccount member = new UserAccount("reader", "hash", "Reader", Role.MEMBER);
        Reservation reservation = new Reservation(member,
                new Book("Title", "ISBN-3", "Author", "Category", ""),
                LocalDateTime.of(2026, 3, 1, 12, 0));

        int assigned = new ReservationQueue(List.of(reservation)).dispatch(
                Optional::<BookCopy>empty, account -> true, LocalDateTime.of(2026, 3, 3, 12, 0));

        assertThat(assigned).isZero();
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.WAITING);
    }
}
