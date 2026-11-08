# Week 7 use cases

These cases add account access and the remaining core circulation actions to
the catalogue and Week 6 flows. Automated checks do not replace browser checks
against the configured database.

Public Member registration was added in this iteration. It extends the account
access flow described in `use-cases-week5.md`; it was not part of the `v0.1`
Week 5 checkpoint.

## UC-08 Reserve an unavailable title

- **Actor:** Library Member
- **Precondition:** The member is signed in, eligible to borrow and no physical
  copy is available.
- **Main flow:** The member joins the title's FIFO queue. When a copy is
  returned, the earliest waiting reservation receives it and has 48 hours to
  claim it. Claiming creates a loan under the member's loan limit and period.
- **Alternative flow:** A member cannot create a duplicate active reservation.
  The member can cancel a waiting or ready reservation. If the 48-hour window
  expires, the copy is offered to the next waiting member. If a waiting member
  is no longer eligible when a copy is assigned, that reservation becomes
  `SKIPPED` and the next eligible member is considered.
- **Related requirements:** FR-15, FR-16, FR-17 and FR-18.

## UC-09 Manage member accounts and membership

- **Actor:** Librarian
- **Precondition:** The librarian is signed in.
- **Main flow:** The librarian edits a member's display name, membership type
  or enabled status. This page cannot promote a member to Librarian.
- **Membership rules:** Standard members may hold five active loans for 14
  days, Student members three for 21 days, and Premium members eight for 21
  days.
- **Related requirement:** FR-06.

## UC-10 Claim or cancel a reservation

- **Actor:** Library Member
- **Precondition:** The member has an active `WAITING` or `READY` reservation.
- **Main flow:** The member cancels their own waiting/ready reservation or
  claims a ready copy before its 48-hour deadline. Claiming creates a loan
  using their membership limit and loan period.
- **Alternative flow:** A member cannot cancel or claim another member's
  reservation. A non-ready, expired or already completed reservation is
  rejected.
- **Related requirements:** FR-15, FR-16, FR-17 and FR-18.

## Circulation rule notes

- Late fines are VND 5,000 per chargeable day and capped at VND 100,000 per
  loan. Dates in `LIBRARY_HOLIDAYS` are excluded from chargeable days.
- `LIBRARY_HOLIDAYS` is a comma-separated list of ISO dates (`yyyy-MM-dd`).
- Reservation expiry is processed by circulation actions and a scheduled task
  that checks every minute.

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
