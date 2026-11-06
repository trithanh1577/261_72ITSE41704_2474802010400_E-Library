package com.elibrary.domain;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;

/** Applies FIFO copy assignment to the active reservations for one title. */
public final class ReservationQueue {

    private final List<Reservation> waitingReservations;

    public ReservationQueue(List<Reservation> waitingReservations) {
        this.waitingReservations = waitingReservations.stream()
                .sorted(Comparator.comparing(Reservation::getReservedAt)
                        .thenComparing(Reservation::getId, Comparator.nullsLast(Long::compareTo)))
                .toList();
    }

    public int dispatch(Supplier<Optional<BookCopy>> availableCopy,
                        Predicate<UserAccount> memberIsEligible,
                        LocalDateTime readyUntil) {
        int assigned = 0;
        for (Reservation reservation : waitingReservations) {
            if (!memberIsEligible.test(reservation.getMember())) {
                reservation.skip();
                continue;
            }
            Optional<BookCopy> copy = availableCopy.get();
            if (copy.isEmpty()) {
                break;
            }
            BookCopy allocatedCopy = copy.get();
            allocatedCopy.setStatus(CopyStatus.RESERVED);
            reservation.markReady(allocatedCopy, readyUntil);
            assigned++;
        }
        return assigned;
    }
}
