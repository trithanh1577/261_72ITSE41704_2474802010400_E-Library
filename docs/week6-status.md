# Week 6 status

This file preserves the Week 6 checkpoint snapshot. Later implementation and
verification are recorded in [`week7-status.md`](week7-status.md). The limits
listed below describe Week 6 and are superseded where Week 7 now implements
those items.

## Implemented in the local working branch

- A library member can submit a borrowing request for an active title and view
  their own requests and loans.
- A librarian can review requests, approve an eligible request, or reject it
  with a decision note.
- Approval checks member status, the active-loan limit for the member's type,
  overdue loans and copy availability. Loan length also follows membership:
  Standard 14 days, Student and Premium 21 days.
- A librarian can record a return. The service records VND 5,000 per
  chargeable late day, excludes dates configured in `LIBRARY_HOLIDAYS`, caps
  each loan's fine at VND 100,000 and routes a returned copy to the next
  reservation when there is a waiting member.
- Manual copy editing cannot create an `ON_LOAN` state or clear a copy with an
  active loan outside the circulation workflow.
- Added service tests, role-protected pages, Week 6 test cases and design
  updates.

## Verification state

- Ran `mvn test` with the local Maven binary: 19 tests passed, with 0 failures,
  0 errors and 0 skipped tests. This includes the Week 6 circulation service
  tests and copy-status protection test.
- `git diff --check` completed without whitespace errors.
- Local HTTP smoke check passed on an isolated in-memory H2 database: login,
  registration, catalogue and book detail returned successfully; after login,
  Librarian could open Members and Reservations, while Member could open My
  Activity and received 403 for librarian-only pages. This did not exercise
  reservation FIFO/expiry transitions.
- The earlier local H2 file-profile restart check passed: a newly created book
  remained searchable after a graceful stop and restart. MySQL persistence and
  the full browser workflow remain pending.
- On this Windows workspace, test compilation needs a temporary `subst` drive
  because the repository path contains Vietnamese characters; the workaround
  is included in `RUN_GUIDE.md`.
- The reservation, membership-policy and member-management additions compile
  successfully, and the existing suite passes. Dedicated automated coverage
  for reservation queue transitions is still absent; the FIFO, cancellation
  and expiry scenarios need manual verification.
- No Week 6 release tag has been created.

## Remaining limits

- Reservation FIFO, 48-hour ready/expiry transitions, member cancellation and
  fulfillment are implemented in the current working tree. Manual verification
  is still pending.
- The account model stores a username only, so email-based sign-in from FR-01
  is not implemented yet. Full MySQL browser checks for the member activity
  indicators remain pending.
- Fine payment or waiver, reports, pagination and browser compatibility
  evidence remain later work. Librarian member profile, membership and account
  enable/disable management has been added; it still needs manual verification.
- The team should review and understand the changes, then complete the manual
  MySQL/browser checks before calling the Week 6 checkpoint fully verified.
