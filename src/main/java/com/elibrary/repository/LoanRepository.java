package com.elibrary.repository;

import com.elibrary.domain.Loan;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @EntityGraph(attributePaths = {"member", "copy", "copy.book", "fineSettlements"})
    List<Loan> findAllByOrderByIssuedAtDesc();

    @EntityGraph(attributePaths = {"member", "copy", "copy.book", "fineSettlements"})
    @Query("""
            select distinct l from Loan l
            join l.member m join l.copy c join c.book b
            where (:query = '' or lower(m.username) like lower(concat('%', :query, '%'))
                or lower(m.displayName) like lower(concat('%', :query, '%'))
                or lower(c.copyCode) like lower(concat('%', :query, '%'))
                or lower(b.title) like lower(concat('%', :query, '%'))
                or lower(b.isbn) like lower(concat('%', :query, '%')))
              and (:status = 'ALL'
                or (:status = 'ACTIVE' and l.returnedAt is null)
                or (:status = 'RETURNED' and l.returnedAt is not null)
                or (:status = 'OVERDUE' and l.returnedAt is null and l.dueDate < :today))
            order by l.issuedAt desc
            """)
    Page<Loan> searchForReport(@Param("query") String query,
                               @Param("status") String status,
                               @Param("today") LocalDate today,
                               Pageable pageable);

    @Query("""
            select new com.elibrary.repository.BookPopularity(b.title, b.author, count(l.id))
            from Loan l join l.copy c join c.book b
            group by b.id, b.title, b.author
            order by count(l.id) desc, b.title asc
            """)
    List<BookPopularity> findMostBorrowedTitles(Pageable pageable);

    @EntityGraph(attributePaths = {"copy", "copy.book"})
    List<Loan> findByMemberUsernameOrderByIssuedAtDesc(String username);

    @Override
    @EntityGraph(attributePaths = {"member", "copy", "copy.book"})
    Optional<Loan> findById(Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @EntityGraph(attributePaths = {"member", "copy", "copy.book", "fineSettlements"})
    @Query("select l from Loan l where l.id = :id")
    Optional<Loan> findLockedById(Long id);

    @Query("select count(l) from Loan l where l.member.id = :memberId and l.returnedAt is null and l.dueDate < :today")
    long countOverdueActiveLoans(@Param("memberId") Long memberId, @Param("today") LocalDate today);
}
