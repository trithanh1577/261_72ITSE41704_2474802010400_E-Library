# Week 9 acceptance checklist

This checklist is for the team's final application walkthrough. Automated tests
and the local H2 smoke check are recorded separately; they do not count as
browser acceptance against MySQL or the deployed database. Leave each result
as **Pending** until a team member performs the check and records what happened.

## Test environment record

Complete this section before the walkthrough:

| Item | Value |
|---|---|
| Date and time | Pending |
| Tester(s) | Pending |
| Application commit or branch | Pending |
| Database | MySQL / Supabase PostgreSQL (record version or project, never credentials) |
| Browser and version | Chrome / Edge (record version) |
| Environment | Local / hosted Render URL |

Do not put passwords, database URLs, API keys or member personal data in this
file or in screenshots committed to the repository.

## Browser workflows

Run with disposable test accounts and data. Record **Pass**, **Fail** or
**Blocked**, plus a short observation and evidence filename. Do not replace a
failed or blocked result with an automated test result.

| ID | Workflow | Expected result | Result / observation / evidence |
|---|---|---|---|
| A-01 | Register a member with a unique username and email; sign in with the exact-case username, then sign out. | Registration creates a Member; sign-in opens the catalogue; sign-out ends access to protected pages. | Pending |
| A-02 | Try signing in with the username changed to a different letter case. | The differently cased username is rejected. | Pending |
| A-03 | Sign in by email using different email letter case. | The account is found and the member can open My activity. | Pending |
| A-04 | As a member, open a book, search by title/author/ISBN/category and inspect availability. | Active matching titles and copy availability are shown; no-result search has a clear empty state. | Pending |
| A-05 | As a librarian, create a title and physical copy, edit them, then deactivate the title. | Saved values appear; duplicate ISBN/copy code and invalid fields are rejected; deactivated title leaves the active catalogue. | Pending |
| A-06 | As a member, submit a borrowing request; as librarian, approve it; return the loan. | Request, loan and copy statuses update; the copy is available again unless allocated to a reservation. | Pending |
| A-07 | Exercise membership limits, an overdue account and a disabled member. | Ineligible borrowing is rejected with a useful message; eligible members receive the configured due date. | Pending |
| A-08 | Return a book late, record payment or waive the outstanding fine, and attempt another settlement after the balance reaches zero. | Fine follows the configured rate/cap/holiday rules; payment or waiver, actor, note and amount are retained; zero balance cannot be settled again. | Pending |
| A-09 | With no copy available, have two members reserve the same title; return a copy and claim it as the first member. | FIFO assignment is shown, the ready copy is held for 48 hours, and claiming creates a loan. | Pending |
| A-10 | Cancel a waiting and a ready reservation; attempt to claim/cancel another member's reservation. | Cancellation releases a held copy for the next eligible member; another member's reservation cannot be changed. | Pending |
| A-11 | Let a ready reservation expire in a disposable database. | The copy is released and offered to the next eligible waiting member. Record whether the scheduled check or a state-changing circulation action processed expiry; simply opening a page should not change reservation state. | Pending |
| A-12 | As librarian, filter loan history and compare the popularity report with recorded loans. | Filters return matching loans; title ranking and counts reflect loans, and the empty state is clear when there is no history. | Pending |
| A-13 | As a member, try librarian-only pages and actions; as a librarian, try member-only reservation actions. | The server denies requests for the wrong role, including direct URL/POST access. | Pending |
| A-14 | Submit invalid form values, a non-numeric page, an unknown record ID and a duplicate unique value. | The app shows an appropriate validation, not-found or conflict response without exposing SQL or a stack trace. | Pending |

## Persistence and release checks

| ID | Check | Expected result | Result / observation / evidence |
|---|---|---|---|
| P-01 | Create a disposable title, copy and member record in the configured MySQL database; stop and restart the application. | The records remain available after restart. | Pending |
| P-02 | If deploying, restart/redeploy the Render service and reopen the same records backed by Supabase. | Production records remain available; the deployed URL serves the reviewed build. | Pending |
| P-03 | Review environment configuration and repository changes before release. | No secrets or database files are committed; Render tracks the intended branch; the release commit and tag identify the accepted build. | Pending |

## Existing evidence and boundaries

As of 6 November 2026, the repository records 77 passing automated tests and
an H2 file-database restart smoke check. Those results do not complete any
pending row above. The deployed service, MySQL/Supabase persistence, browser
role matrix, and teammate review remain unverified until the team performs and
records those checks.

The local Compose YAML and variable interpolation pass `docker compose config`,
but the Docker daemon is unavailable in the verification environment, so the
container image build and MySQL-volume restart check remain pending.

Keep the final course report as its own deliverable. This checklist is only an
engineering acceptance record and does not claim that the course submission or
report is complete.
