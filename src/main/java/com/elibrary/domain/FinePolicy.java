package com.elibrary.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

public final class FinePolicy {

    public static final BigDecimal DAILY_RATE = new BigDecimal("5000.00");
    public static final BigDecimal MAXIMUM_PER_LOAN = new BigDecimal("100000.00");

    private final Set<LocalDate> holidays;

    public FinePolicy(Set<LocalDate> holidays) {
        this.holidays = Set.copyOf(holidays);
    }

    public BigDecimal calculate(LocalDate dueDate, LocalDate returnedOn) {
        if (!returnedOn.isAfter(dueDate)) return BigDecimal.ZERO;
        long chargeableDays = 0;
        for (LocalDate date = dueDate.plusDays(1); !date.isAfter(returnedOn); date = date.plusDays(1)) {
            if (!holidays.contains(date)) chargeableDays++;
        }
        return DAILY_RATE.multiply(BigDecimal.valueOf(chargeableDays)).min(MAXIMUM_PER_LOAN);
    }
}
