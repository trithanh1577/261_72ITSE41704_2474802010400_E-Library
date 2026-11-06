package com.elibrary.domain;

/** Result of applying the library's member and loan-limit rules. */
public record BorrowingEligibility(boolean permitted, String reason) {

    public static BorrowingEligibility evaluate(UserAccount member, boolean hasOverdueLoan,
                                                long activeLoanCount) {
        if (member == null || !member.isEnabled() || member.getRole() != Role.MEMBER) {
            return new BorrowingEligibility(false, "This account cannot borrow books.");
        }
        if (hasOverdueLoan) {
            return new BorrowingEligibility(false,
                    "Member has an overdue loan and cannot borrow another book.");
        }
        LoanTerms terms = LoanTerms.forMembership(member.getMembershipType());
        if (activeLoanCount >= terms.maximumActiveLoans()) {
            return new BorrowingEligibility(false,
                    "Member has reached the active-loan limit for their membership type ("
                            + terms.maximumActiveLoans() + ").");
        }
        return new BorrowingEligibility(true, null);
    }
}
