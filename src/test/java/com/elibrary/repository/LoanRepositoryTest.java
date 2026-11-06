package com.elibrary.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.elibrary.domain.Book;
import com.elibrary.domain.BookCopy;
import com.elibrary.domain.FineSettlement;
import com.elibrary.domain.FineSettlementType;
import com.elibrary.domain.Loan;
import com.elibrary.domain.Role;
import com.elibrary.domain.UserAccount;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;

@DataJpaTest
class LoanRepositoryTest {

    @Autowired private LoanRepository loanRepository;
    @Autowired private BookRepository bookRepository;
    @Autowired private BookCopyRepository copyRepository;
    @Autowired private UserAccountRepository userRepository;
    @Autowired private FineSettlementRepository settlementRepository;
    @Autowired private EntityManager entityManager;

    @Test
    void reportFiltersByMemberAndStatusAndLoadsFineHistory() {
        UserAccount member = userRepository.save(new UserAccount(
                "report-reader", null, "hash", "Ada Reader", Role.MEMBER));
        Book book = bookRepository.save(new Book("Report Testing", "9781234567890", "A. Author", "Testing", ""));
        BookCopy copy = copyRepository.save(new BookCopy("RPT-001", book));
        Loan loan = loanRepository.save(new Loan(member, copy, null,
                LocalDateTime.now().minusDays(20), LocalDate.now().minusDays(5)));
        loan.markReturned(LocalDateTime.now().minusDays(2), new BigDecimal("15000.00"));
        loanRepository.save(loan);
        settlementRepository.save(new FineSettlement(loan, FineSettlementType.PAID,
                new BigDecimal("5000.00"), "Part payment", "librarian", LocalDateTime.now()));
        entityManager.flush();
        entityManager.clear();

        var returnedLoans = loanRepository.searchForReport("Ada", "RETURNED", LocalDate.now(), PageRequest.of(0, 50));
        var activeLoans = loanRepository.searchForReport("Ada", "ACTIVE", LocalDate.now(), PageRequest.of(0, 50));

        assertThat(returnedLoans.getContent()).hasSize(1);
        assertThat(returnedLoans.getContent().get(0).getCopy().getBook().getTitle()).isEqualTo("Report Testing");
        assertThat(returnedLoans.getContent().get(0).getOutstandingFineAmount()).isEqualByComparingTo("10000.00");
        assertThat(activeLoans.getContent()).isEmpty();
        assertThat(settlementRepository.sumAmountByLoanId(returnedLoans.getContent().get(0).getId()))
                .isEqualByComparingTo("5000.00");
    }
}
