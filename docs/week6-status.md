# Week 6 status

This is a snapshot of the Week 6 circulation checkpoint at commit `3d98875`
(`Implement Week 6 circulation workflow`, 19 October 2026). It records what
was present then; later additions are in [`week7-status.md`](week7-status.md).

## Implemented at the checkpoint

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

## Verification state at the checkpoint

- `mvn test` passed 17 tests, with 0 failures, 0 errors and 0 skipped tests.
- `git diff --check` completed without whitespace errors.
- MySQL, browser, role-smoke and restart-persistence checks were pending.
- On this Windows workspace, test compilation needed a temporary `subst`
  drive because the repository path contains Vietnamese characters; the
  workaround is documented in `RUN_GUIDE.md`.
- No Week 6 release tag had been created.

## Remaining work at the checkpoint

- Reservation FIFO, ready/expiry transitions and reservation fulfillment were
  planned for Week 7.
- The account model stored a username only, so email-based sign-in from FR-01
  was not implemented yet. Member activity did not show reservation details
  or a separate overdue indicator.
- Fine payment or waiver, member account management, reports, pagination and
  browser compatibility evidence remained later work.
- Team review and manual MySQL/browser checks were needed before calling the
  Week 6 checkpoint fully verified.

Later implementation and final verification are recorded in
[`week7-status.md`](week7-status.md), [`week9-acceptance.md`](week9-acceptance.md)
and the Week 5–9 test-case tables.
