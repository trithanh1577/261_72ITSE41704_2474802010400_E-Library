# Week 6 use cases

These cases extend the Week 5 catalogue slice with circulation, reservation
and member account workflows.

## UC-04 Submit a borrowing request

- **Actor:** Library Member
- **Precondition:** The member is signed in and the title is active.
- **Main flow:** The member opens a book detail page, optionally enters a note
  and submits a request. The system stores it as `PENDING` and shows it in My
  activity.
- **Alternative flow:** A repeated pending request for the same title is
  rejected. A note longer than 500 characters is not saved.
- **Related requirements:** FR-09.

## UC-05 Review a request and issue a loan

- **Actor:** Librarian
- **Precondition:** The librarian is signed in and a pending request exists.
- **Main flow:** The librarian approves the request. The system checks that
  the member is enabled, is below their membership loan limit, has no overdue
  active loan and that a copy is available. It creates a loan using the
  membership loan period, marks the copy `ON_LOAN` and approves the request.
- **Alternative flow:** If an eligibility check fails or no copy is available,
  no loan is created and the request remains pending. The librarian may
  instead reject the request with an optional decision note. A title
  deactivated after the request was submitted cannot be issued.
- **Related requirements:** FR-09, FR-10, FR-11, FR-12.

## UC-06 Record a return

- **Actor:** Librarian
- **Precondition:** An active loan exists.
- **Main flow:** The librarian records the returned copy. The system records
  the return timestamp, calculates VND 5,000 per chargeable late day up to
  VND 100,000 and makes the copy available or assigns it to the next
  reservation.
- **Alternative flow:** A second return attempt is rejected without changing
  the saved return or fine.
- **Related requirements:** FR-13, FR-14.

## UC-07 View own requests and loans

- **Actor:** Library Member
- **Precondition:** The member is signed in.
- **Main flow:** The member opens My activity and sees only their own requests,
  loans and reservations, including due/return dates and recorded fines.
- **Related requirements:** FR-18.

## Verification note

Service-level automated tests cover the main eligibility and fine rules. The
browser and configured-MySQL flows in `test-cases-week6.md` have not yet been
run and are still marked pending.

## UC-08 Reserve an unavailable title

- **Actor:** Library Member
- **Precondition:** The member is signed in, eligible to borrow and no physical
  copy is available.
- **Main flow:** The member joins the title's FIFO queue. When a copy is
  returned, the earliest waiting reservation receives it and has 48 hours to
  claim it. Claiming creates a loan under the member's loan limit and period.
- **Alternative flow:** A member cannot create a duplicate active reservation.
  The member can cancel a waiting or ready reservation. If the 48-hour window
  expires, the copy is offered to the next waiting member.
- **Related requirements:** FR-15, FR-16, FR-17, FR-18.

## UC-09 Manage member accounts and membership

- **Actor:** Librarian
- **Precondition:** The librarian is signed in.
- **Main flow:** The librarian edits a member's display name, membership type
  or enabled status. This page cannot promote a member to librarian.
- **Membership rules:** Standard members may hold five active loans for 14
  days, Student members three for 21 days, and Premium members eight for 21
  days.
- **Related requirements:** FR-06.

## Circulation rule notes

- Late fines are VND 5,000 per chargeable day and capped at VND 100,000 per
  loan. Dates in `LIBRARY_HOLIDAYS` are excluded from chargeable days.
- `LIBRARY_HOLIDAYS` is a comma-separated list of ISO dates (`yyyy-MM-dd`).
- Reservation expiry is processed by circulation actions and a scheduled task
  that checks every minute.

## UC-10 Claim or cancel a reservation

- **Actor:** Library Member
- **Precondition:** The member has an active `WAITING` or `READY` reservation.
- **Main flow:** The member cancels their own waiting/ready reservation or
  claims a ready copy before its 48-hour deadline. Claiming creates a loan
  using their membership limit and loan period.
- **Alternative flow:** A member cannot cancel or claim another member's
  reservation. A non-ready, expired or already completed reservation is
  rejected.
- **Related requirements:** FR-15, FR-16, FR-17, FR-18.
