# Week 6 use cases

These cases extend the Week 5 catalogue slice. The current implementation
covers a borrowing request, librarian decision, loan, return and a member's
own activity page. Reservation handling is still planned for Week 7.

## UC-04 Submit a borrowing request

- **Actor:** Library Member
- **Precondition:** The member is signed in and the title is active.
- **Main flow:** The member opens a book detail page, optionally enters a note
  and submits a request. The system stores it as `PENDING` and shows it in My
  activity.
- **Alternative flow:** A repeated pending request for the same title is
  rejected. A note longer than 500 characters is not saved.
- **Related requirements:** FR-08, FR-09.

## UC-05 Review a request and issue a loan

- **Actor:** Librarian
- **Precondition:** The librarian is signed in and a pending request exists.
- **Main flow:** The librarian approves the request. The system checks that
  the member is enabled, has fewer than five active loans, has no overdue
  active loan and that a copy is available. It creates a loan due in 14 days,
  marks the copy `ON_LOAN` and marks the request approved.
- **Alternative flow:** If an eligibility check fails or no copy is available,
  no loan is created and the request remains pending. The librarian may
  instead reject the request with an optional decision note. A title
  deactivated after the request was submitted cannot be issued.
- **Related requirements:** FR-08, FR-09.

## UC-06 Record a return

- **Actor:** Librarian
- **Precondition:** An active loan exists.
- **Main flow:** The librarian records the returned copy. The system records
  the return timestamp, calculates VND 5,000 for each late calendar day and
  changes the copy status to `AVAILABLE`.
- **Alternative flow:** A second return attempt is rejected without changing
  the saved return or fine.
- **Related requirements:** FR-10, FR-11.

## UC-07 View own requests and loans

- **Actor:** Library Member
- **Precondition:** The member is signed in.
- **Main flow:** The member opens My activity and sees only their own requests
  and loans, including due/return dates and recorded fines.
- **Related requirements:** FR-16 (reservation details remain Week 7 work).

## Verification note

Service-level automated tests cover the main eligibility and fine rules. The
browser and configured-MySQL flows in `test-cases-week6.md` have not yet been
run and are still marked pending.
