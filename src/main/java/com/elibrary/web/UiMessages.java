package com.elibrary.web;

import java.util.Map;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

/** Resolves user-facing service messages using the locale selected for the request. */
@Component
public class UiMessages {

    private static final Map<String, String> MESSAGE_CODES = Map.ofEntries(
            Map.entry("ISBN already belongs to another book.", "uiError.duplicateIsbn"),
            Map.entry("Copy code already exists.", "uiError.duplicateCopyCode"),
            Map.entry("Book was not found.", "uiError.bookNotFound"),
            Map.entry("Copy code already belongs to another copy.", "uiError.duplicateCopyCode"),
            Map.entry("A copy with an active loan can only be returned through the loan workflow.", "uiError.copyOnLoan"),
            Map.entry("A reserved copy can only be released through the reservation workflow.", "uiError.copyReserved"),
            Map.entry("Loan and reservation workflows control ON_LOAN and RESERVED copy statuses.", "uiError.workflowCopyStatus"),
            Map.entry("Display name is required and must be at most 120 characters.", "uiError.displayName"),
            Map.entry("Select a valid membership type.", "uiError.membershipType"),
            Map.entry("Member account was not found.", "uiError.memberNotFound"),
            Map.entry("Only member accounts can be edited here.", "uiError.onlyMembersEditable"),
            Map.entry("Username, email, display name and password are required.", "uiError.registrationRequired"),
            Map.entry("Password must not exceed BCrypt's 72-byte UTF-8 input limit.", "validation.password.bcryptLimit"),
            Map.entry("Enter a valid email address.", "validation.email.invalid"),
            Map.entry("Only an enabled library member can submit a borrowing request.", "uiError.memberCannotRequest"),
            Map.entry("Active book was not found.", "uiError.activeBookNotFound"),
            Map.entry("You already have a pending request for this book.", "uiError.duplicateBorrowRequest"),
            Map.entry("Request note must be at most 500 characters.", "uiError.requestNoteTooLong"),
            Map.entry("Borrowing request was not found.", "uiError.borrowRequestNotFound"),
            Map.entry("Only pending requests can be approved.", "uiError.requestNotPending"),
            Map.entry("A deactivated book cannot be issued.", "uiError.bookDeactivated"),
            Map.entry("No available copy remains for this book.", "uiError.noAvailableCopy"),
            Map.entry("Only pending requests can be rejected.", "uiError.requestNotPending"),
            Map.entry("Decision note must be at most 500 characters.", "uiError.decisionNoteTooLong"),
            Map.entry("Loan was not found.", "uiError.loanNotFound"),
            Map.entry("This loan has already been returned.", "uiError.loanAlreadyReturned"),
            Map.entry("A copy is available. Submit a borrowing request instead.", "uiError.copyAvailableRequest"),
            Map.entry("You already have an active reservation for this book.", "uiError.duplicateReservation"),
            Map.entry("This reservation belongs to another member.", "uiError.reservationOwner"),
            Map.entry("Reservation was not found.", "uiError.reservationNotFound"),
            Map.entry("This reservation is not ready to be collected.", "uiError.reservationNotReady"),
            Map.entry("The reserved copy is no longer available.", "uiError.reservedCopyUnavailable"),
            Map.entry("Only waiting or ready reservations can be cancelled.", "uiError.reservationCannotCancel"),
            Map.entry("Choose whether the fine was paid or waived.", "uiError.fineActionRequired"),
            Map.entry("A note is required and must be at most 500 characters.", "uiError.fineNoteRequired"),
            Map.entry("This account cannot borrow books.", "uiError.memberCannotBorrow"),
            Map.entry("Member has an overdue loan and cannot borrow another book.", "uiError.overdueLoan"),
            Map.entry("This loan has no outstanding fine.", "uiError.noOutstandingFine"));

    private final MessageSource messageSource;

    public UiMessages(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public String get(String code, Object... arguments) {
        return messageSource.getMessage(code, arguments, LocaleContextHolder.getLocale());
    }

    public String fromService(String message) {
        if (message == null || message.isBlank()) {
            return get("error.invalidRequest");
        }
        String code = MESSAGE_CODES.get(message);
        if (code != null) {
            return get(code);
        }
        String limitPrefix = "Member has reached the active-loan limit for their membership type (";
        if (message.startsWith(limitPrefix) && message.endsWith(").")) {
            String limit = message.substring(limitPrefix.length(), message.length() - 2);
            return get("uiError.loanLimitReached", limit);
        }
        return get("error.invalidRequest");
    }
}
