package com.elibrary.domain;

import java.math.BigDecimal;

/** Assessed fine and prior settlements for one loan. */
public final class FineBalance {

    private final BigDecimal assessedAmount;
    private final BigDecimal settledAmount;

    public FineBalance(BigDecimal assessedAmount, BigDecimal settledAmount) {
        this.assessedAmount = nonNegative(assessedAmount, "Assessed fine");
        this.settledAmount = nonNegative(settledAmount, "Settled fine");
    }

    public BigDecimal outstandingAmount() {
        return assessedAmount.subtract(settledAmount).max(BigDecimal.ZERO);
    }

    public BigDecimal amountToSettle() {
        BigDecimal outstanding = outstandingAmount();
        if (outstanding.signum() <= 0) {
            throw new IllegalArgumentException("This loan has no outstanding fine.");
        }
        return outstanding;
    }

    private static BigDecimal nonNegative(BigDecimal amount, String label) {
        if (amount == null || amount.signum() < 0) {
            throw new IllegalArgumentException(label + " must be zero or greater.");
        }
        return amount;
    }
}
