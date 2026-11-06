package com.elibrary.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;
import org.junit.jupiter.api.Test;

class FinePolicyTest {

    private final FinePolicy policy = new FinePolicy(Set.of(LocalDate.parse("2026-02-16")));

    @Test
    void excludesConfiguredHolidayAndCapsFinePerLoan() {
        assertThat(policy.calculate(LocalDate.parse("2026-02-14"), LocalDate.parse("2026-02-17")))
                .isEqualByComparingTo("10000.00");
        assertThat(policy.calculate(LocalDate.parse("2025-01-01"), LocalDate.parse("2025-02-15")))
                .isEqualByComparingTo(FinePolicy.MAXIMUM_PER_LOAN);
    }

    @Test
    void returnOnOrBeforeDueDateHasNoFine() {
        assertThat(policy.calculate(LocalDate.parse("2026-02-17"), LocalDate.parse("2026-02-17")))
                .isEqualByComparingTo(BigDecimal.ZERO);
    }
}
