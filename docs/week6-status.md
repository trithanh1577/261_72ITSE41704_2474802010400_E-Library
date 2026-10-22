# Week 6 status

## Implemented in the local working branch

- A library member can submit a borrowing request for an active title and view
  their own requests and loans.
- A librarian can review requests, approve an eligible request, or reject it
  with a decision note.
- Approval checks member status, the five-active-loan limit, overdue loans and
  copy availability. It creates a 14-calendar-day loan and marks the copy
  `ON_LOAN`.
- A librarian can record a return. The service records VND 5,000 per late
  calendar day and makes the copy available again.
- Manual copy editing cannot create an `ON_LOAN` state or clear a copy with an
  active loan outside the circulation workflow.
- Added service tests, role-protected pages, Week 6 test cases and design
  updates.

## Verification state

- Ran `mvn test` with the local Maven binary after recompiling all sources: 17 tests passed, with 0 failures,
  0 errors and 0 skipped tests. This includes the Week 6 circulation service
  tests and copy-status protection test.
- `git diff --check` completed without whitespace errors.
- Local H2 file-profile restart check passed: a newly created book remained
  searchable after a graceful stop and restart. MySQL persistence, full browser
  walkthrough and role smoke checks remain pending.
- On this Windows workspace, test compilation needs a temporary `subst` drive
  because the repository path contains Vietnamese characters; the workaround
  is included in `RUN_GUIDE.md`.
- No Week 6 release tag has been created.

## Remaining limits

- Reservation FIFO, ready/expiry transitions and reservation fulfillment are
  planned for Week 7.
- The account model stores a username only, so email-based sign-in from FR-01
  is not implemented yet. The member activity page also does not show
  reservation details or a separate overdue indicator yet.
- Fine payment or waiver, member account management, reports, pagination and
  browser compatibility evidence remain later work.
- The team should review and understand the changes, then complete the manual
  MySQL/browser checks before calling the Week 6 checkpoint fully verified.
