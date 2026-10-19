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

## Week 6 — circulation iteration (in progress)

- Added the first borrowing request flow: a member submits a request and a librarian can approve or reject it.
- Approval checks that the member is enabled, has fewer than five active loans, has no overdue active loan, and that a physical copy is still available.
- Approval creates a 14-day loan and marks one copy `ON_LOAN`; returning a loan records the return date, calculates VND 5,000 per late day, and makes the copy available again.
- Added page-level separation for librarian requests/loans and the signed-in member's own request/loan history.
- Added automated service tests for approval/rejection, eligibility, inventory availability, note validation, return fine calculation and copy-state protection. After recompiling all sources, `mvn test` passed 17 tests, with 0 failures and 0 errors; `git diff --check` also passed.
- Difficult point: automated tests use mocks for circulation rules, so they do not replace a real MySQL/browser walkthrough. MySQL persistence, role-based page access and restart checks remain to be confirmed locally.
- Current limitation: reservation queue allocation and expiry remain Week 7 work; fine payment/waiver and member account management remain later scope.
