package com.elibrary.repository;

import com.elibrary.domain.FineSettlement;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FineSettlementRepository extends JpaRepository<FineSettlement, Long> {

    @Query("select coalesce(sum(s.amount), 0) from FineSettlement s where s.loan.id = :loanId")
    BigDecimal sumAmountByLoanId(@Param("loanId") Long loanId);

    List<FineSettlement> findByLoanIdOrderByRecordedAtAsc(Long loanId);
}
