# Week 7 use cases

These cases add account access and the remaining core circulation actions to
the catalogue and Week 6 flows. Automated checks do not replace browser checks
against the configured database.

## UC-11 Review circulation history and settle a fine

- **Actor:** Librarian
- **Precondition:** Librarian is signed in; loans and any fine records exist.
- **Main flow:** Search by member, book, ISBN or copy and filter by active,
  overdue, returned or all loans. For an outstanding fine, record payment or
  waiver with a required note. The system stores the amount, action, librarian
  and timestamp and shows the remaining balance and settlement history.
- **Alternative flow:** Empty results show an empty state. A missing note,
  unknown loan or already settled fine is rejected without creating another
  settlement.
- **Related requirement:** FR-19.

## Reservation expiry processing

The application checks ready reservations on circulation requests and from a
scheduled background task. A ready copy is released after its 48-hour hold and
offered to the next eligible waiting member. This should be checked with a
fixed-clock service test and a database-backed manual workflow.
