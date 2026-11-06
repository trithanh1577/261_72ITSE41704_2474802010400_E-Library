# Project journal

This journal records the work rhythm and decisions required by the self-study materials. Entries are intentionally short and should be updated in the same commit as the related work.

## Week 1 — repository and scope

- Confirmed Topic 6: E-Library and Resource Circulation System.
- Confirmed two roles: Librarian and Library Member.
- Created the repository baseline and expanded the README with a local run guide.
- Chose Java 17, Spring Boot, Spring Data JPA, Spring Security, Thymeleaf, MySQL, Maven and JUnit 5.
- Agreed that each member commits their own work.

## Week 2 — requirements

- Converted the initial feature list into FR-01 to FR-18 testable statements.
- Added priorities and requirement sources to `docs/requirements-week2.md`.
- Identified the non-CRUD rules for later slices: membership limits, loan period, fines and reservation queue.
- Difficult point: some original requirements were topic headings rather than atomic testable behaviour, so they were split and assigned test IDs.

## Week 3 — proposal and plan

- Kept the ten-week plan aligned with the proposal and assigned work by member and output.
- Week 5 output is the first end-to-end catalogue slice, not a collection of unfinished layers.
- Added a test-case plan but left execution results pending until a Java/Maven environment is available.

## Week 4 — design

- Added architecture, use-case, class, sequence and ERD artefacts under `docs/design/`.
- Chose services for application rules and repositories for persistence queries.
- Difficult point: the later loan/reservation entities are deliberately not forced into the Week 5 schema; adding them early would make the checkpoint harder to explain and test.

## Week 5 — implementation checkpoint

- Implemented authentication, role restrictions, catalogue search, book CRUD/deactivation, physical-copy management and MySQL persistence configuration.
- Added BCrypt password hashing, server-side validation and a copy version field as an initial concurrency safeguard.
- Added sample data for a repeatable local demonstration.
- The initial workspace did not have Java or Maven. After installing JDK 17 and a local Maven binary, `mvn test` compiled 20 source files and passed 3 automated tests with 0 failures and 0 errors. Browser checks and MySQL restart persistence still require a configured local database.
- Known limitations: no borrowing/return flow, reservation queue, fines, member CRUD, pagination, audit log or final concurrency policy yet.

## Week 6 — circulation iteration

- Added the first borrowing request flow: a member submits a request and a librarian can approve or reject it.
- Approval checks that the member is enabled, has fewer than five active loans, has no overdue active loan, and that a physical copy is still available.
- Approval creates a 14-day loan and marks one copy `ON_LOAN`; returning a loan records the return date, calculates VND 5,000 per late day, and makes the copy available again.
- Added page-level separation for librarian requests/loans and the signed-in member's own request/loan history.
- Added automated service tests for approval/rejection, eligibility, inventory availability, note validation, return fine calculation and copy-state protection. After recompiling all sources, `mvn test` passed 17 tests, with 0 failures and 0 errors; `git diff --check` also passed.
- Difficult point: automated tests use mocks for circulation rules, so they do not replace a real MySQL/browser walkthrough. MySQL persistence, role-based page access and restart checks remain to be confirmed locally.
- At this checkpoint, reservation queue allocation and expiry, fine payment/waiver and member account management remained later work; these were picked up in the Week 7 iteration below.

## Week 7 — account access and circulation completion

- Added member email to registration and sign-in while preserving exact-case username lookup. Existing accounts get a derived username key at startup.
- Added a scheduled reservation-expiry check, librarian loan history filters, and audited payment/waiver records for outstanding fines.
- Added focused and H2 integration tests for registration/sign-in lookup, late-fee policy, FIFO dispatch, expiry dispatch, fine balance, report queries and catalogue pagination. The full Maven suite passed 34 tests with 0 failures or errors on 5 November 2026.
- Updated the use-case, class and ER diagrams and added Week 7 acceptance cases.
- Remaining work is manual validation with MySQL and the hosted Render/Supabase setup, browser acceptance and Week 9–10 release checks. The final report remains a separate deliverable.

## Week 8 — reliability and reporting

- Added the circulation popularity report and updated the requirements traceability, diagrams and test-case records.
- Added structured error handling, stronger role and input checks, and integration coverage for the main librarian/member journeys.
- The clean-source Maven package on 5 November 2026 compiled 50 main and 19 test Java files; 68 tests passed with no failures, errors or skips. The H2 search benchmark measured p95 at 52 ms for 100 warmed searches over 10,000 titles. A packaged-JAR smoke check returned HTTP 200 for `/login` and `/books`; H2 file data also remained after restarting the JAR.
- On 6 November, three regression tests were added for case-sensitive username lookup-key generation and migration; the full offline Maven test run passed 71 tests with no failures, errors or skips.
- The same day, the newly packaged JAR returned HTTP 200 for `/login` and `/books` against an isolated H2 file database; a seeded title remained visible after a stop and restart. The temporary app was stopped after the check.
- The final 6 November source-clean package compiled 50 application and 20 test files, passed all 71 tests and measured H2 search p95 at 54 ms.
- After review, reservation expiry was removed from read-only page queries so GET requests do not change reservation state. A regression test was added. The 6 November offline package then passed 72 tests and measured H2 search p95 at 49 ms; MySQL/browser/cloud acceptance is still pending.
- A second review found that editing a reserved copy could release it without updating its reservation. The form and service now keep that transition in the reservation workflow, and an MVC regression test covers code correction and a rejected release attempt. The full offline package passed 75 tests; H2 search p95 measured 58 ms.
- A final transaction review found that concurrent duplicate borrowing requests from one member could pass the check together. Submission now locks the member row before checking for a pending request; a two-thread H2 integration test confirms only one request is created. At that review checkpoint, the offline package passed all 76 tests and the H2 search p95 was 42 ms. Concurrent MySQL/browser verification remains part of the pending acceptance run.
- After packaging that change, the JAR started twice on the same isolated H2 file. Both runs returned HTTP 200 for `/login` and `/books`, and `Clean Code` remained visible after restart. The smoke-test processes were stopped after each run.
- Added an optional local Docker Compose setup for the app and MySQL 8, using a named database volume and a clearly local-only `.env.example`. `docker compose config` accepts the YAML and variable interpolation; the Docker daemon is unavailable here, so image build and Compose runtime remain unverified.
- A final registration review found that BCrypt limits input by UTF-8 bytes rather than characters. Registration now rejects values longer than 72 bytes with a visible validation message while still accepting short non-empty passwords. Automated checks cover a 75-byte input; the 6 November clean offline package passed 77 tests and the H2 search p95 was 11 ms. MySQL/browser acceptance remains pending.
- These checks used H2 and MockMvc. Docker/MySQL execution, Chrome/Edge walkthroughs, Supabase persistence after a hosted restart, teammate review and the Week 9–10 release steps remain unverified. The course report is separate from these technical artifacts.
