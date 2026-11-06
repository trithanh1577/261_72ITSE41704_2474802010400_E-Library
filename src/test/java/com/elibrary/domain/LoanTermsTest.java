package com.elibrary.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class LoanTermsTest {

    @Test
    void standardMembershipProvidesFiveLoansAndFourteenDays() {
        LoanTerms terms = LoanTerms.forMembership(MembershipType.STANDARD);

        assertThat(terms.maximumActiveLoans()).isEqualTo(5);
        assertThat(terms.loanPeriodDays()).isEqualTo(14);
        assertThat(terms.dueDateFor(LocalDate.of(2026, 3, 1)))
                .isEqualTo(LocalDate.of(2026, 3, 15));
    }

    @Test
    void studentMembershipUsesItsOwnLoanLimitAndPeriod() {
        LoanTerms terms = LoanTerms.forMembership(MembershipType.STUDENT);

        assertThat(terms.maximumActiveLoans()).isEqualTo(3);
        assertThat(terms.dueDateFor(LocalDate.of(2026, 3, 1)))
                .isEqualTo(LocalDate.of(2026, 3, 22));
    }

    @Test
    void invalidTermsAreRejected() {
        assertThatThrownBy(() -> new LoanTerms(0, 14))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> LoanTerms.forMembership(null))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
