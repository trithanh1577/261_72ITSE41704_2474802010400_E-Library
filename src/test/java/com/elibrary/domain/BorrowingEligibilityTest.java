package com.elibrary.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class BorrowingEligibilityTest {

    @Test
    void permitsEnabledMemberBelowTheirMembershipLimit() {
        BorrowingEligibility result = BorrowingEligibility.evaluate(member(), false, 4);

        assertThat(result.permitted()).isTrue();
        assertThat(result.reason()).isNull();
    }

    @Test
    void rejectsMemberAtTheirLimitOrWithAnOverdueLoan() {
        assertThat(BorrowingEligibility.evaluate(member(), false, 5).reason())
                .contains("active-loan limit");
        assertThat(BorrowingEligibility.evaluate(member(), true, 0).reason())
                .contains("overdue loan");
    }

    @Test
    void rejectsDisabledAccountsAndLibrarians() {
        UserAccount disabled = member();
        disabled.setEnabled(false);
        UserAccount librarian = new UserAccount("staff", "hash", "Staff", Role.LIBRARIAN);

        assertThat(BorrowingEligibility.evaluate(disabled, false, 0).permitted()).isFalse();
        assertThat(BorrowingEligibility.evaluate(librarian, false, 0).permitted()).isFalse();
        assertThat(BorrowingEligibility.evaluate(null, false, 0).permitted()).isFalse();
    }

    private UserAccount member() {
        return new UserAccount("reader", "hash", "Reader", Role.MEMBER);
    }
}
