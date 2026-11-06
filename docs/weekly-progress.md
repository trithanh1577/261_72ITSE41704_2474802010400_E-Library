# Weekly progress log

This note records how the project was developed from the first week through
the Week 6 checkpoint. It is a progress record, not a claim that the whole
ten-week proposal has already been completed.

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

The detailed handoff is in [`week5-status.md`](week5-status.md), and the
working notes are in [`journal.md`](journal.md).

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
- The current local branch is `feature/week-7-reservations`. The 34 functional tests passed. MySQL/browser/cloud acceptance checks remain pending; the performance benchmark was added in Week 8.

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
- A registration boundary review found BCrypt's input limit is measured in UTF-8 bytes, not characters. The form and service now reject passwords over 72 bytes with a field message; short non-empty passwords remain allowed. A 75-byte password was rejected by MockMvc and service tests. The 6 November clean offline package passed 77 tests, and the H2 benchmark measured p95 11 ms. Browser/MySQL verification remains pending.
- The packaged JAR was started twice against the same isolated H2 file. `/login` and `/books` returned HTTP 200 on both runs, and the seeded `Clean Code` title was still visible after restart. Both smoke-test processes were stopped after verification.
- Browser acceptance, existing MySQL schema compatibility, hosted persistence, and release review remain open. The final-course report remains a separate deliverable.
