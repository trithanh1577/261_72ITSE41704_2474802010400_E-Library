package com.elibrary.domain;

import java.time.LocalDate;

/** Immutable circulation terms derived when a member receives a loan. */
public record LoanTerms(int maximumActiveLoans, int loanPeriodDays) {

    public LoanTerms {
        if (maximumActiveLoans < 1 || loanPeriodDays < 1) {
            throw new IllegalArgumentException("Loan limits and periods must be positive.");
        }
    }

    public static LoanTerms forMembership(MembershipType membershipType) {
        if (membershipType == null) {
            throw new IllegalArgumentException("A membership type is required to issue a loan.");
        }
        return new LoanTerms(membershipType.getMaximumActiveLoans(), membershipType.getLoanPeriodDays());
    }

    public LocalDate dueDateFor(LocalDate issuedOn) {
        if (issuedOn == null) {
            throw new IllegalArgumentException("The loan issue date is required.");
        }
        return issuedOn.plusDays(loanPeriodDays);
    }
}
