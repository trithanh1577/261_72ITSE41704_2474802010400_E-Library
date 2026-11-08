# Weekly progress summary

This is a retrospective summary of the repository and project artifacts. The
week headings organize work by course milestone; they do not mean this file was
written during each week or that every listed activity was independently
confirmed by both members. Use Git commits, tags, test output and recorded
acceptance evidence to verify dated work and contributions. Do not use this
summary as a substitute for the final report's individual contribution table.

## Week 1 - Start with the topic and scope

### Work done

- Read the course guidelines, self-study materials and suggested project
  titles.
- Chose Topic 6: E-Library and Resource Circulation System.
- Confirmed the two user roles: Librarian and Library Member.
- Agreed on the initial team responsibilities and created the project
  baseline.
- Listed the first catalogue functions: sign in, search books, view book
  details and manage physical copies.

### Decision at this point

The first version should focus on the catalogue before adding loans,
reservations and fines. Those features are kept in the proposal as later
work, rather than being mixed into the first checkpoint.

### Difficulty / note

The topic description included many modules, so it was necessary to separate
the initial catalogue scope from the later circulation scope.

## Week 2 - Turn the topic into requirements

### Work done

- Wrote the FR-01 to FR-18 requirement list.
- Added priorities, sources and planned test IDs.
- Added the measurable non-functional requirements.
- Marked borrowing, returns, fines, reservations and member history as later
  work instead of claiming them in the Week 5 implementation.

### Difficulty / note

Some items in the original topic were module names rather than testable
behaviours. They had to be rewritten as smaller requirements before they
could be tested.

See [`requirements-week2.md`](requirements-week2.md).

## Week 3 - Plan the work

### Work done

- Broke the proposal into a ten-week plan.
- Assigned backend/database and interface/test responsibilities between the
  two members.
- Defined the Week 5 boundary as the first working catalogue slice.
- Prepared the initial test-case list, with manual results left pending until
  the application could be run with MySQL.

### Difficulty / note

The plan had to keep the final proposal broad while making the Week 5 target
small enough to finish and explain.

## Week 4 - Design before adding more features

### Work done

- Prepared the layered architecture and package responsibilities.
- Drew the initial use-case, class and ER diagrams.
- Wrote the catalogue search sequence and low-fidelity screen notes.
- Chose to keep loan and reservation tables out of the first database design.

### Difficulty / note

Adding all future circulation entities too early would make the first version
harder to test. The design therefore shows the Week 5 catalogue boundary and
leaves the later entities for the weeks where they are implemented.

See the files in [`design/`](design/).

## Week 5 - First implementation checkpoint

### Work done

- Implemented login/logout and Librarian/Member access rules.
- Implemented active-book search, book create/edit/deactivate and physical
  copy management.
- Added MySQL persistence configuration, validation and local seed data.
- Added unit and repository tests for the current slice.

### Current result

The Maven test run passed 3 automated tests. Browser checks and MySQL restart
checks are still marked pending because they require a configured local
database and an actual application run.

### Remaining work

Borrowing requests, loans, returns, fines, reservations, member management,
pagination and performance measurement are not part of this checkpoint. They
remain in the later work plan.

The detailed handoff is in [`week5-status.md`](week5-status.md).

## Week 6 - Circulation iteration (manual checks pending)

- Started member borrowing requests and librarian review.
- Added loan issue/return flow with the proposal's 14-day loan period, five
  active-loan limit, overdue eligibility check and VND 5,000 daily fine rule.
- Added service tests and a separate Week 6 test-case table. After final review,
  `mvn test` passed 17 tests with no failures or errors; manual MySQL/browser
  cases remain pending. A review found that the book-save POST route needed an
  explicit Librarian rule; this was corrected in `SecurityConfig`. Manual copy
  editing was also restricted from setting the not-yet-implemented
  `RESERVED` state.
- Added Week 6 use cases and updated the requirement traceability notes.
- This work is on local branch `feature/week-6-circulation`; it has not been
  reviewed, committed or pushed yet.

## Week 7 - account and circulation extension

- Added member registration with email and sign-in by exact-case username or case-insensitive email.
- Added periodic reservation expiry processing, FIFO queue service tests, and audited fine payment/waiver records.
- Added librarian loan-history filters and fine-settlement records under FR-19; reconciled the use-case set to 11 distinct user goals.
- At the Week 7 checkpoint, work was on local branch `feature/week-7-reservations` and 34 functional tests passed. MySQL/browser/cloud acceptance checks remained pending; the performance benchmark was added in Week 8.

## Week 8 - performance and reliability check

- Added a repeatable catalogue benchmark using 10,000 active titles and 100 warmed paged searches.
- The 6 November clean-source full-suite H2 run measured p95 at 54 ms, below the 2-second target. The packaged JAR also served a seeded title before and after restart against the same temporary H2 file database. These checks do not substitute for MySQL/PostgreSQL or hosted measurements.
- Added structured error pages for invalid requests, missing records, duplicate data, database failures and unexpected failures; added real-login security integration checks for librarian-only routes.
- Added authenticated end-to-end tests for book/copy management and search, member-account management, reservation cancellation, request/approve/return, reservation/claim/report/fine-payment journeys and a librarian popularity report. Domain tests cover loan terms, eligibility, fine balance and reservation-queue allocation; service checks cover fine waiver and duplicate reservations. New MockMvc checks cover logout, librarian-only book forms, blank/over-length title validation and duplicate copy codes. A database-failure test confirms safe 503 handling. A seed test verifies 50 unique titles and 51 copies in the local demo catalogue. The full Maven suite passed 68 tests with no failures, errors or skips; the clean-source H2 search p95 was 52 ms for 10,000 titles. The packaged JAR returned HTTP 200 for `/login` and `/books`.
- Added FR-20 and UC-12 for the topic's circulation-popularity report; aligned BR-01–BR-05 identifiers across requirements and test cases. Refreshed architecture notes for fine settlements, pagination and reservation expiry scheduling.
- A database-collation review found that Base64 username lookup keys could compare case-insensitively on MySQL. Username keys now use lowercase SHA-256, and startup refreshes legacy keys. Three regression checks were added; the full Maven suite passed 71 tests on 6 November.
- Read-only reservation pages previously triggered expiry processing. Expiry now runs in the scheduled task or state-changing circulation actions; a regression check covers the read paths. The full offline package passed 72 tests, and the H2 search benchmark measured p95 at 49 ms.
- Review also found that librarian copy editing could release a `RESERVED` copy from its queue. The edit form now keeps workflow-controlled statuses out of manual choices, and the service rejects attempts to release a held copy while allowing copy-code correction. Unit and MVC integration checks cover the behavior; the full offline package passed 75 tests with p95 58 ms on 6 November.
- A final transaction review found a race in duplicate borrowing-request submission. The service now locks the member row before checking for a pending request, and a two-thread H2 integration test confirms only one pending request is created. At that review checkpoint, the offline package passed 76 tests and the H2 benchmark measured p95 42 ms. The MySQL concurrency check remains pending.
- A registration boundary review found BCrypt's input limit is measured in UTF-8 bytes, not characters. The form and service now reject passwords over 72 bytes with a field message; short non-empty passwords remain allowed. A 75-byte password was rejected by MockMvc and service tests. At that review checkpoint, the 6 November offline package passed 77 tests and the H2 benchmark measured p95 11 ms. Browser/MySQL verification remains pending.
- The packaged JAR was started twice against the same isolated H2 file. `/login` and `/books` returned HTTP 200 on both runs, and the seeded `Clean Code` title was still visible after restart. Both smoke-test processes were stopped after verification.
- Browser acceptance, existing MySQL schema compatibility, hosted persistence, and release review remain open. The final-course report remains a separate deliverable.

## Week 9 - localization and release checks

- Added the English/Vietnamese switch to the signed-in navigation and the sign-in and registration pages. Language links keep the current query parameters, and the selected language is saved in a one-year cookie.
- Added translations for service validation messages and a generic localized fallback for unmapped domain errors. A Title 6 cross-check added direct integration coverage for reservation availability and protected-copy rules. The full local Maven 3.9.9 offline `verify` passed 82 tests with no failures, errors or skips and packaged the application, including EN/VI integration checks and localized service-error mapping.
- A transaction failure-injection check (TC-73) flushes return updates before a reservation-dispatch failure and verifies on H2 that the loan and copy both roll back. The integration class passed 9/9 and the full Maven `verify` passed 83/83.
- TC-74 verifies that a Librarian cannot submit a member-only borrowing request. The full local Maven `verify` on 7 November 2026 passed 84 tests with no failures, errors or skips.
- At this Week 9 checkpoint, the Week 5–9 tables contained 76 documented cases. TC-75–TC-77 were added during the later Week 10 review, bringing the current total to 77. Browser acceptance, hosted persistence, Render verification and release review remain pending. The course report is still a separate deliverable.

## Week 10 - repository cleanup and final technical review

- Removed transient logs, extracted document copies, isolated smoke-test databases, the local Maven distribution and dependency cache, and generated `target/` output. Kept the project source, course documents, designs, test cases and progress records; preserved the ignored local demo database so its data is not lost.
- The first Week 10 `mvn verify` run passed 84 tests. The packaged JAR also returned HTTP 200 for `/login`, Vietnamese search and locale-cookie login before and after restarting against the same temporary H2 file. That smoke check did not replace browser, MySQL or hosted acceptance.
- The latest clean Docker Maven build and exact `mvn verify` both passed 86 tests with no failures, errors or skips. Compose now runs the app as `e-library`; `/login` returned HTTP 200 and MySQL was healthy. P-01 passed: a title, copy and Member account created through the app survived `docker compose down` and `up`; the disposable records were removed.
- A read-only HTTP smoke check on 7 November returned 200 for `/books`, `/login`, `/register` and Vietnamese catalogue search. The login page contains neither local demo password, and it shows the registration link and language switch; the registration page has no password `minlength` and its Vietnamese heading rendered. Unauthenticated requests to member/librarian pages returned 302 to `/login`. No forms were submitted; this does not replace the browser acceptance checklist.
- An authenticated local HTTP role check returned 200 for Librarian pages `/members` and `/circulation/requests`, and Member page `/circulation/mine`; the Member received 403 for `/members` and `/circulation/requests`. No application records were changed; direct browser acceptance remains pending.
- Added an explicit Member-only rule for `/circulation/mine` and regression case TC-75; a clean no-cache Docker Maven `package` passed 87/87 tests. Rebuilt the `e-library` container while keeping the MySQL volume; `/login` returned 200 and authenticated GET role checks returned the expected 200/403 results.
- Initial POST probes reused a CSRF token from before authentication; Spring rotates that token after login, and its POST access-denied forward targeted a GET-only handler. Changed `/error/403` to accept forwarded methods and added TC-76. The clean no-cache Docker Maven `package` now passes 88/88 tests. On the rebuilt local app, valid Librarian form submission reached validation (200); wrong-role POSTs and invalid-CSRF POSTs returned 403 with the access-denied page. No application records changed; browser and deployed acceptance remain pending.
- Added TC-77 for an anonymous invalid-CSRF POST and allowed the public 403 error route so the access-denied page renders without redirecting to sign-in. The latest clean no-cache Docker Maven `package` passes 89/89 tests. On local Compose, `/login` and `/error/403` return 200, while POST `/login` without a CSRF token returns 403 with the access-denied page. No application records changed; browser and deployed acceptance remain pending.
- Updated the acceptance checklist with the local MySQL restart evidence. Browser workflows, Supabase restart, Render, teammate review and release-history checks remain pending. The final course report remains a separate submission.
- On 8 November 2026, ran `mvn clean test` and `mvn verify` in a Docker Maven environment against the current worktree. Both completed with 89 tests, no failures, errors or skips. This confirms the local build path.
- Built and started the Render runtime image on 8 November with the `prod` profile against a disposable PostgreSQL container. The /login, /register and /books routes returned 200, and the configured bootstrap account was created with the Librarian role. The temporary database and containers were removed; the check does not establish Supabase or Render deployment status.
- The latest Hibernate run logged a warning that the paged loan report fetches fine-settlement rows and applies pagination in memory. The current integration check returns the expected page, but a large loan history could use more memory. This is outside the catalogue-search performance target and remains a later optimization.
