package com.elibrary.repository;

import com.elibrary.domain.Reservation;
import com.elibrary.domain.ReservationStatus;
import jakarta.persistence.LockModeType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @EntityGraph(attributePaths = {"member", "book", "copy"})
    List<Reservation> findAllByOrderByReservedAtAsc();

    @EntityGraph(attributePaths = {"member", "book", "copy"})
    List<Reservation> findByMemberUsernameOrderByReservedAtDesc(String username);

    long countByBookIdAndStatus(Long bookId, ReservationStatus status);

    @Query("select distinct r.book.id from Reservation r where r.status = :status")
    List<Long> findBookIdsWithReservations(@Param("status") ReservationStatus status);

    boolean existsByMemberIdAndBookIdAndStatusIn(Long memberId, Long bookId, List<ReservationStatus> statuses);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select r from Reservation r where r.book.id = :bookId and r.status = :status order by r.reservedAt asc, r.id asc")
    List<Reservation> findWaitingForUpdate(@Param("bookId") Long bookId,
                                           @Param("status") ReservationStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @EntityGraph(attributePaths = {"member", "book", "copy"})
    @Query("select r from Reservation r where r.id = :id")
    Optional<Reservation> findByIdForUpdate(@Param("id") Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @EntityGraph(attributePaths = {"member", "book", "copy"})
    @Query("select r from Reservation r where r.status = :status and r.readyUntil <= :now order by r.readyUntil asc")
    List<Reservation> findExpiredReadyForUpdate(@Param("status") ReservationStatus status,
                                                @Param("now") LocalDateTime now);

}
