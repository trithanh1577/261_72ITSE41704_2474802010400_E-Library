# Week 5 status and handoff

## Done

- Spring Boot project structure and Maven build file.
- Layered packages for configuration, domain, repository, service and web code.
- User accounts with Librarian/Member roles.
- Login/logout and URL-level role protection.
- Book catalogue search using title, author, ISBN and category.
- Librarian book create/edit/deactivate flow.
- Physical-copy add/edit/status flow.
- MySQL persistence configuration with environment-variable overrides.
- BCrypt password storage and server-side validation.
- Seed data and 15-case test plan.
- Week 1–5 design and planning evidence.
- Maven compile and automated tests verified locally on 12 October 2026: 3 tests passed, 0 failures, 0 errors.

## Not claimed as done

- Borrowing requests and approval.
- Loans, returns and overdue-fine calculation.
- Reservation FIFO/expiry processing.
- Member account CRUD and personal history.
- Final ERD with circulation tables.
- Performance measurement on 10,000 titles.
- Browser compatibility evidence.

## Difficulties and honest risks to report later

1. The first local workspace had no JDK or Maven. The tools were installed only for local verification; the setup history is kept because it was a genuine development obstacle.
2. MySQL setup is external to the repository; an incorrect account or database URL prevents startup. The README therefore documents environment variables instead of committing credentials.
3. Physical-copy status can currently be edited by a librarian. Once loans and reservations exist, a domain/service state transition must prevent an `ON_LOAN` copy from being changed incorrectly.
4. The search query is suitable for the first slice but has no pagination or measured 10,000-title performance result yet.
5. The proposal's later domain classes are not all implemented at Week 5. The final report must distinguish proposed scope from implemented scope.

Automated verification is complete for the repository/service tests. Manual browser checks, MySQL restart persistence and role smoke tests remain pending until the application is run with a configured local database.

These are real checkpoint limitations, not intentionally inserted failures. The test table records `Pending` until a team member runs the application and records actual results.
