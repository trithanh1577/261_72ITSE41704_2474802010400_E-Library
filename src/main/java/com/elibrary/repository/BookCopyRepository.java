package com.elibrary.repository;

import com.elibrary.domain.BookCopy;
import com.elibrary.domain.CopyStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import jakarta.persistence.LockModeType;

public interface BookCopyRepository extends JpaRepository<BookCopy, Long> {
    List<BookCopy> findByBookIdOrderByCopyCode(Long bookId);
    long countByBookIdAndStatus(Long bookId, CopyStatus status);
    boolean existsByCopyCode(String copyCode);
    Optional<BookCopy> findByCopyCode(String copyCode);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<BookCopy> findFirstByBookIdAndStatusOrderByCopyCodeAsc(Long bookId, CopyStatus status);

    @Override
    @EntityGraph(attributePaths = "book")
    Optional<BookCopy> findById(Long id);
}
