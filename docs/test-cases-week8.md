# Week 8 verification: performance, errors and user flows

| ID | Requirement | Test | Expected result | Automated result |
|---|---|---|---|---|
| TC-39 | NFR-01 | Seed 10,000 active titles, warm the catalogue search, then measure 100 paged searches for a common term. | The 95th percentile is below 2 seconds; each page contains at most 24 titles and reports the matching total. | Clean no-cache Docker Maven `package` on 7 November 2026: p95 14 ms. Earlier 6 November H2 runs measured 11 ms, 10 ms, 42 ms and 49 ms. This local H2 result does not establish MySQL or Supabase performance. |
| TC-40 | NFR-04 | Request the catalogue with a non-numeric page parameter. | A friendly validation page is returned with HTTP 400. | Passed in MockMvc. |
| TC-41 | NFR-08 | Request a book ID that does not exist. | A friendly not-found page is returned with HTTP 404 and no stack trace. | Passed in MockMvc. |
| TC-42 | FR-03 / FR-19 / NFR-03 | Sign in as a member and open the librarian loan report. | Access is denied with HTTP 403. | Passed in MockMvc using the actual login form and security filters. |
| TC-43 | FR-03 / FR-06 / NFR-03 | Sign in as a member and open member management. | Access is denied with HTTP 403. | Passed in MockMvc using the actual login form and security filters. |
| TC-44 | FR-03 / FR-19 / NFR-03 | Sign in as a member and submit a fine settlement. | Access is denied with HTTP 403 before the settlement can be processed. | Passed in MockMvc with a valid CSRF token. |
| TC-45 | FR-03 / FR-06 | Sign in as a librarian and open member management. | The member list is displayed. | Passed in MockMvc using the actual login form and security filters. |
| TC-46 | FR-09–FR-14 | Member requests a book, librarian approves it, the member views the loan, then the librarian records the return. | Request, loan and copy statuses move through the expected states and the copy becomes available again. | Passed end-to-end through MockMvc, Spring Security and H2. |
| TC-47 | FR-15–FR-17 | Member queues for an unavailable title; a returned copy is assigned; the member claims it. | Reservation moves WAITING → READY → FULFILLED and the copy moves to ON_LOAN. | Passed end-to-end through MockMvc, Spring Security and H2. |
| TC-48 | FR-19 | Librarian searches the overdue member's loan and records payment of its fine. | The report returns the loan; the payment is auditable and the outstanding balance reaches zero. | Passed end-to-end through MockMvc, Spring Security and H2. |
| TC-49 | FR-12 / BR-01 / BR-02 | Evaluate enabled role, overdue state and membership loan limit. | Only an enabled Member below their membership limit and without an overdue loan is eligible. | Passed in `BorrowingEligibilityTest` (3 cases). |
| TC-50 | FR-11 / BR-01 | Resolve loan limit and due date for Standard and Student memberships. | Standard receives 14 days and five active loans; Student receives 21 days and three active loans. | Passed in `LoanTermsTest` (3 cases). |
| TC-51 | FR-14 / FR-19 | Calculate a fine balance after partial settlement and prevent settling a zero balance. | Outstanding amount never falls below zero; invalid or negative balances are rejected. | Passed in `FineBalanceTest` and `CirculationServiceTest`. |
| TC-52 | FR-17 / BR-04 | Dispatch available copies through an ordered reservation queue. | Oldest eligible member receives the copy; ineligible members are skipped; processing stops when no copy remains. | Passed in `ReservationQueueTest` (3 cases). |
| TC-53 | FR-04 / FR-05 / FR-07 / FR-08 | Librarian creates a title and physical copy; a member searches and opens the title. | The saved title is searchable and its detail page shows the copy code and available count. | Passed in `CoreUseCaseIntegrationTest` through MockMvc, Spring Security and H2. |
| TC-54 | FR-06 / NFR-03 | Librarian updates a member's display name and membership; attempt the same operation on a Librarian account. | Member settings are saved; the librarian role cannot be changed through member management. | Passed in `CoreUseCaseIntegrationTest` through MockMvc and H2. |
| TC-55 | FR-15–FR-18 | Member cancels a ready reservation. | Reservation becomes CANCELLED, its held copy becomes AVAILABLE and the member sees the updated activity. | Passed in `CoreUseCaseIntegrationTest` through MockMvc, Spring Security and H2. |
| TC-56 | NFR-09 | Simulate a data-access failure while handling a request. | The application returns a service-unavailable recovery page without exposing database details. | Passed in `GlobalExceptionHandlerTest`; invalid-input handling is also covered by TC-40. |
| TC-57 | Data initialization | Start the non-production seed runner against an empty catalogue. | 50 unique book titles and 51 unique physical-copy codes are created, with two local demo accounts; no duplicate ISBN/title/copy code is seeded. | Verified by `DataInitializerTest`. |
| TC-58 | FR-20 | Sign in as a librarian and open the circulation report when titles have different loan counts. | The report ranks the title with more recorded loans first and shows each title's loan count. | Passed through MockMvc, Spring Security, the `LoanRepository` aggregate query and H2 in `CoreUseCaseIntegrationTest`. |
| TC-59 | FR-20 / NFR-07 | In Chrome or Edge, record multiple loans for two titles and open the librarian circulation report. | Titles are ranked by actual recorded loan count; an empty library shows the no-history message. | Pending team browser acceptance with MySQL. |
| TC-60 | FR-01 | Generate lookup keys for two usernames that differ only by letter case. | Keys remain different under a case-insensitive string comparison. | Passed in `UsernameKeyInitializerTest`; compares the generated lowercase SHA-256 keys. MySQL collation behavior still needs a configured MySQL check. |
| TC-61 | FR-01 / migration | Start with a legacy Base64 username key and run the startup initializer. | The stored lookup key is refreshed from the exact username. | Passed in `UsernameKeyInitializerTest` with a mocked repository; an existing MySQL/Supabase database migration check remains pending. |
| TC-62 | FR-01 / migration | Run the startup initializer when an account already has the current lookup key. | The existing key is preserved and the account is not rewritten. | Passed in `UsernameKeyInitializerTest` with a mocked repository. |
After confirming `target/` was Git-ignored, the generated folder was removed
and `mvn package` rebuilt the project from source on 5 November: 50 main Java
files and 19 test Java files compiled, all 68 tests passed, and the executable
JAR was built. On 6 November, the generated `target/classes` and
`target/test-classes` folders were removed and `mvn -o package` rebuilt from
source through the temporary `Z:` mapping. Maven compiled 50 application Java
files and 20 test files; all 72 tests passed with 0 failures, errors or skips,
and the executable JAR was built. The H2 benchmark measured p95 at 49 ms for
100 searches. `mvn clean package` could not resolve the uncached clean plugin
because network access to Maven Central was denied; deleting only the verified
class-output folders forced source compilation without changing source files.
The JAR was started with an in-memory H2 database; `/login` and `/books`
returned HTTP 200 and the seeded catalogue was visible. The smoke-test process
was stopped afterward. The Docker image was not built because the Docker daemon
was not running in the local environment.

On 6 November, the latest packaged JAR was started twice against the same
isolated H2 file database under the ignored `.tools/smoke-db` folder. `/login`
and `/books` returned HTTP 200 on both starts, and a seeded title remained
visible after restart. Both temporary application processes were stopped.
This verifies the H2 fallback only; it does not replace the MySQL or hosted
database restart check.

Run with `mvn -Dtest=BookSearchPerformanceTest test`. The fixture and result
are local to the test database and do not modify application data.
