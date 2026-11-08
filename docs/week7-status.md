# Week 7 status

## Implemented functionality

- Member registration validates the username, email, display name and matching
  password fields. Public registration always creates a `MEMBER` account.
- Sign-in accepts username or email. Username matching remains case-sensitive;
  email matching is case-insensitive. Startup generates or refreshes the
  username lookup key for existing accounts.
- Reservations use a FIFO queue. Returning a copy offers it to the earliest
  eligible waiting member for 48 hours; expired holds are released and
  dispatched to the next eligible member. A scheduled check runs every minute.
- Librarians can search current and historical loans by member, title, ISBN,
  copy code and status, and record payment or waiver with an audit note.
- Catalogue and librarian report results are paginated, with server-side page
  sizes of 24 books and 50 loans.
- Fine settlements store the amount, type, note, librarian username and time.
  Remaining balance is calculated against prior settlements.
- Updated the use-case, class and ER diagrams, requirements traceability and
  additional manual test cases.

## Automated verification

At the Week 7 checkpoint, the repository-local Maven 3.9.9 binary was run
through a temporary `Z:` mapping with JDK 17. That suite passed **34 tests, 0
failures, 0 errors, 0 skipped**. The tests cover fine policy, case-sensitive
username lookup, email lookup, settlement balance, FIFO allocation, expiry
dispatch, registration through the MVC controller and reservation claim/cancel
ownership plus database-backed loan report/fine-history queries and catalogue
page boundaries. Later H2 benchmark runs measured p95 at 56 ms and 48 ms. The
6 November intermediate offline package compiled 50 application and 20 test
Java files, passed 72 tests and measured p95 at 49 ms. Later regression checks
brought the suite to 89 passing tests and the latest clean H2 p95 to 14 ms on
7 November; see
[`test-cases-week9.md`](test-cases-week9.md). These local results do not verify
the same target against MySQL or Supabase.

The later EN/VI interface update added locale integration and localized error
checks. On 7 November 2026, a clean Docker Maven `package` completed with 89 tests,
zero failures, zero errors and zero skips. Browser acceptance for the language
control is tracked separately in [`week9-acceptance.md`](week9-acceptance.md).

The tests use unit mocks or H2 repository tests. They do not confirm behavior
against the team's MySQL instance, Render, Supabase or a real browser.

## Remaining acceptance work

- Walk through registration, email sign-in, member/librarian authorization,
  request approval, reservation claim/cancel/expiry, report filters and fine
  settlement in a browser against MySQL.
- Verify the `username_key`, email and `fine_settlements` schema update on an
  existing database and confirm prior accounts still sign in.
- Check production variables, Supabase connectivity and persistence after a
  Render restart.
- MySQL/PostgreSQL performance measurement at 10,000 titles, Chrome/Edge
  acceptance and final
  release review remain open; no final-course report is included here.
