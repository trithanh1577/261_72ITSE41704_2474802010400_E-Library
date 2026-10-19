package com.elibrary.repository;

import com.elibrary.domain.Loan;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;

public interface LoanRepository extends JpaRepository<Loan, Long> {

    long countByMemberIdAndReturnedAtIsNull(Long memberId);

    boolean existsByMemberIdAndReturnedAtIsNullAndDueDateBefore(Long memberId, LocalDate date);

    boolean existsByCopyIdAndReturnedAtIsNull(Long copyId);

    @EntityGraph(attributePaths = {"member", "copy", "copy.book"})
    List<Loan> findAllByOrderByIssuedAtDesc();

    @EntityGraph(attributePaths = {"copy", "copy.book"})
    List<Loan> findByMemberUsernameOrderByIssuedAtDesc(String username);

    @Override
    @EntityGraph(attributePaths = {"member", "copy", "copy.book"})
    Optional<Loan> findById(Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @EntityGraph(attributePaths = {"member", "copy", "copy.book"})
    @Query("select l from Loan l where l.id = :id")
    Optional<Loan> findLockedById(Long id);

    @Query("select count(l) from Loan l where l.member.id = :memberId and l.returnedAt is null and l.dueDate < :today")
    long countOverdueActiveLoans(@Param("memberId") Long memberId, @Param("today") LocalDate today);
}
