# Week 6 use cases

These cases record the circulation slice introduced at the Week 6 checkpoint.
Reservation and member-account expansion arrived in Week 7 and is documented
in [`use-cases-week7.md`](use-cases-week7.md).

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

## Scope boundary

This checkpoint did not yet include reservation queues, member profile
management, fine settlement or circulation reporting. Those later workflows
are documented under Week 7 and Week 8.
