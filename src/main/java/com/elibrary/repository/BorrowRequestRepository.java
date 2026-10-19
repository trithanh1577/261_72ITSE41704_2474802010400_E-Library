package com.elibrary.repository;

import com.elibrary.domain.BorrowRequest;
import com.elibrary.domain.BorrowRequestStatus;
import java.util.List;
import java.util.Optional;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BorrowRequestRepository extends JpaRepository<BorrowRequest, Long> {

    @EntityGraph(attributePaths = {"member", "book"})
    List<BorrowRequest> findAllByOrderByRequestedAtDesc();

    @EntityGraph(attributePaths = {"member", "book"})
    List<BorrowRequest> findByMemberUsernameOrderByRequestedAtDesc(String username);

    boolean existsByMemberIdAndBookIdAndStatus(Long memberId, Long bookId, BorrowRequestStatus status);

    @Override
    @EntityGraph(attributePaths = {"member", "book"})
    Optional<BorrowRequest> findById(Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @EntityGraph(attributePaths = {"member", "book"})
    @Query("select r from BorrowRequest r where r.id = :id")
    Optional<BorrowRequest> findByIdForUpdate(@Param("id") Long id);
}
