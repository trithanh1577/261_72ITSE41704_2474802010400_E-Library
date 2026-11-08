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
| A-13 | As a member, try librarian-only pages and actions; as a librarian, try member-only reservation actions. | The server denies requests for the wrong role, including direct URL/POST access. | Pending (browser walkthrough) — local HTTP role smoke on 7 Nov: both demo sign-ins returned 200; Librarian `/members` and `/circulation/requests` returned 200; Member `/circulation/mine` returned 200 and `/members` plus `/circulation/requests` returned 403. Browser actions and wrong-role POST checks remain pending. |
| A-14 | Submit invalid form values, a non-numeric page, an unknown record ID and a duplicate unique value. | The app shows an appropriate validation, not-found or conflict response without exposing SQL or a stack trace. | Pending |
| A-15 | Switch between `EN` and `VI` from the catalogue, sign-in and registration pages; repeat with a search query and navigate to another page. | Page text changes language, search parameters remain intact, and the selected language is remembered by the browser. | Pending (browser acceptance). Partial local HTTP check, 7 Nov 2026: `/login?lang=vi` returned 200 and included the Vietnamese “Đăng nhập” label. Query retention, cookie persistence across pages and the browser walkthrough remain unverified. |

## Persistence and release checks

| ID | Check | Expected result | Result / observation / evidence |
|---|---|---|---|
| P-01 | Create a disposable title, copy and member record in the configured MySQL database; stop and restart the application. | The records remain available after restart. | Pass — 7 Nov 2026, local MySQL Compose: created a title and copy through the librarian forms and registered a Member through the app; after `docker compose down` and `up`, the records remained visible and both accounts could sign in. Removed the disposable records afterward. This was an application-level restart check, not browser walkthrough evidence. |
| P-02 | If deploying, restart/redeploy the Render service and reopen the same records backed by Supabase. | Production records remain available; the deployed URL serves the reviewed build. | Pending — direct read-only requests from this environment timed out on 7 and 8 Nov 2026. This does not establish that the service is down; verify from a browser and the Render dashboard. |
| P-03 | Review environment configuration and repository changes before release. | No secrets or database files are committed; local build succeeds; Render builds the intended branch. | Partial — 8 Nov 2026: Docker-isolated `mvn clean test` and `mvn verify` passed 89/89 (no failures, errors or skips). The runtime image also started with the `prod` profile against a temporary PostgreSQL database; `/login`, `/register` and `/books` returned 200, and the bootstrap user had the Librarian role. No `.env` or database file is tracked. These are local checks; Render settings and Supabase persistence still need verification. |
| P-04 | Review the GitHub history against the course repository-health checklist. | Every member has commits under their own account, the team history spans at least five course weeks, and changes are merged through reviewed pull requests. | Partial — read-only GitHub check 8 Nov 2026: remote refs are `main=e0ee041`, `update-final=e3ebf36`, Week 5 `5918ee5`, Week 6 `e5d69d9` and Week 7 `01adcf3`. The 20 commits returned on `main` are attributed to both accounts: `trithanh1577` appears in four ISO weeks (W38, W42, W43, W45) and `Ngan3112` in two (W43, W45). The branch spans four ISO weeks; mapping them to course weeks is unknown, so five course weeks are not established. PRs #2, #3, #5 and #6 each have one approved review; PRs #1 and #4 have none. The remote has no tags. Local refs have `v0.1` and `v0.2`, but no `v1.0`. The local worktree is `feature/i18n-en-vi` at `e3ebf36` with uncommitted changes; these changes are not on GitHub. |

## Acceptance notes

Automated tests do not replace the browser walkthroughs above. In particular,
H2 tests do not complete the MySQL restart check, and a local run does not
complete the Render/Supabase check. Record the actual environment and evidence
in the tables when those checks are performed.
