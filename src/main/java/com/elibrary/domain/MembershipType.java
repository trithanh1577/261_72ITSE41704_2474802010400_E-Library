package com.elibrary.domain;

public enum MembershipType {
    STANDARD(5, 14),
    STUDENT(3, 21),
    PREMIUM(8, 21);

    private final int maximumActiveLoans;
    private final int loanPeriodDays;

    MembershipType(int maximumActiveLoans, int loanPeriodDays) {
        this.maximumActiveLoans = maximumActiveLoans;
        this.loanPeriodDays = loanPeriodDays;
    }

    public int getMaximumActiveLoans() { return maximumActiveLoans; }
    public int getLoanPeriodDays() { return loanPeriodDays; }
}
