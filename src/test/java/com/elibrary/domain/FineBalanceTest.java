package com.elibrary.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class FineBalanceTest {

    @Test
    void computesRemainingFineAfterEarlierPayments() {
        FineBalance balance = new FineBalance(new BigDecimal("15000"), new BigDecimal("5000"));

        assertThat(balance.outstandingAmount()).isEqualByComparingTo("10000");
        assertThat(balance.amountToSettle()).isEqualByComparingTo("10000");
    }

    @Test
    void neverReportsNegativeBalanceAndRejectsFurtherSettlement() {
        FineBalance balance = new FineBalance(new BigDecimal("5000"), new BigDecimal("7000"));

        assertThat(balance.outstandingAmount()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThatThrownBy(balance::amountToSettle)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("no outstanding fine");
    }

    @Test
    void rejectsNegativeAmounts() {
        assertThatThrownBy(() -> new FineBalance(new BigDecimal("-1"), BigDecimal.ZERO))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
