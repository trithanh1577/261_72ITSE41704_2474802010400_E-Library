# Week 9 verification record

| ID | Purpose | Precondition | Input / steps | Expected result | Actual result | Status |
|---|---|---|---|---|---|---|
| TC-63 | Ensure viewing reservations does not trigger expiry side effects. | A READY reservation is expired or a WAITING queue exists. | Read reservation lists, waiting counts and a member's active reservation status. | Reads preserve stored state; expiry is processed by the scheduled task or a state-changing operation. | Passed in `CirculationServiceTest` on 6 November 2026. | Pass (automated) |
| TC-64 | Keep reserved-copy status under queue control. | A copy is held by a READY reservation. | Open the edit form, correct its copy code, then attempt to change its status to AVAILABLE. | Code correction is saved, but the copy remains RESERVED. | Passed in `CoreUseCaseIntegrationTest` and `BookCopyServiceTest` on 6 November 2026. | Pass (automated) |
| TC-65 | Prevent concurrent duplicate pending borrowing requests. | One eligible member and an active title are available. | Submit two requests for the same member and title in separate threads. | Exactly one request succeeds and one pending request remains. | Passed in `CoreUseCaseIntegrationTest` with two H2-backed transactions; MySQL concurrency is not checked. | Partial (automated; MySQL pending) |
| TC-66 | Preserve valid short-password registration and identifier sign-in. | Username and email are unique; password is non-empty and three characters long. | Register, then sign in with the exact-case username and email. | Registration succeeds; the stored password is BCrypt-hashed; both identifiers authenticate. | Passed in `AuthFlowIntegrationTest` on 6 November 2026. | Pass (automated) |
| TC-67 | Reject a password that exceeds BCrypt's UTF-8 byte limit. | A new registration form is open. | Enter 25 copies of `ệ` as the password (75 UTF-8 bytes). | A validation message appears, no account is created and no truncated password is passed to BCrypt. | Passed in `UserRegistrationServiceTest` and `AuthFlowIntegrationTest` on 6 November 2026. | Pass (automated) |
| TC-68 | Preserve search state when switching languages. | The catalogue has a keyword and page query. | Switch between EN and VI and inspect both links. | Locale changes while existing query parameters are retained. | Passed in `LanguageSwitchIntegrationTest`; manual browser check pending. | Partial (automated; browser pending) |
| TC-69 | Persist locale selection and validation feedback. | Vietnamese is selected on registration. | Open another page using the locale cookie, then submit invalid registration data. | Vietnamese remains active for one year and validation feedback is translated. | Passed in `LanguageSwitchIntegrationTest`; manual browser check pending. | Partial (automated; browser pending) |
| TC-70 | Localize domain and service validation errors. | Vietnamese locale is active. | Trigger missing-book/request/loan/reservation, unavailable-copy and unrecognized domain errors. | Known errors are translated; unknown validation errors use the localized fallback. | Passed in `UiMessagesTest`; included in the 83-test suite on 6 November 2026. | Pass (automated) |
| TC-71 | Prevent reserving a title while a copy is available. | A member is signed in; the active title has an AVAILABLE copy. | Submit a reservation for that title. | No reservation is saved and the copy remains AVAILABLE for a borrowing request. | Passed in `CoreUseCaseIntegrationTest` and full Maven `verify` on H2/MockMvc, 6 November 2026. | Pass (automated) |
| TC-72 | Protect a READY copy from another member's borrowing request. | A librarian is signed in; another member has a pending request; the only copy is held by a READY reservation. | Approve the pending request. | No loan is created; request stays PENDING, reservation stays READY and copy stays RESERVED. | Passed in `CoreUseCaseIntegrationTest` and full Maven `verify` on H2/MockMvc, 6 November 2026. | Pass (automated) |
| TC-73 | Roll back a multi-record return when reservation dispatch fails. | A member has an active loan and its copy is ON_LOAN. | Flush the return changes inside the transaction, then inject a runtime failure during queue dispatch. | The transaction rolls back; the loan remains active with no fine recorded and the copy remains ON_LOAN. | Passed in `CoreUseCaseIntegrationTest` with H2; included in the 83-test run on 6 November 2026. | Pass (automated) |
| TC-74 | Deny a librarian's attempt to submit a member borrowing request. | A librarian is authenticated and a valid book request route is available. | POST to `/books/{bookId}/requests` with a valid CSRF token. | The server returns 403 and does not create a borrowing request. | Passed in `RequestErrorHandlingIntegrationTest`; included in the 88-test run on 7 November 2026. | Pass (automated) |
| TC-75 | Restrict personal circulation activity to members. | A librarian is authenticated. | Open `/circulation/mine`. | The server returns 403 and displays the access-denied page. | Passed in `RequestErrorHandlingIntegrationTest`; included in the clean 88-test build on 7 November 2026. | Pass (automated) |
| TC-76 | Keep the forbidden page working for a denied POST. | An authenticated user makes a POST rejected by role authorization or CSRF protection. | Submit a wrong-role POST with a fresh CSRF token or an authorized POST with an invalid token. | The server returns 403 and renders the access-denied page instead of returning 405. | Passed in `RequestErrorHandlingIntegrationTest` and local HTTP checks after rebuilding the image on 7 November 2026. | Pass (automated and local HTTP) |
| TC-77 | Show the forbidden page for an anonymous invalid-CSRF POST. | No user is signed in. | POST to `/login` without a CSRF token, then open `/error/403` directly. | The POST returns 403 with the access-denied page; the public error route does not redirect to sign-in. | Passed in `RequestErrorHandlingIntegrationTest` and local HTTP checks after rebuilding the image on 7 November 2026. | Pass (automated and local HTTP) |

## Automated verification

On 7 November 2026, a clean, no-cache Docker build ran Maven `package` with 89
tests, no failures, errors or skips, and packaged the executable JAR. Manual
browser and hosted-database checks are tracked in
[`week9-acceptance.md`](week9-acceptance.md).

A local HTTP check after rebuilding the Compose image confirmed `/login`,
role-protected GET pages, and POST handling. A Librarian's invalid book form
returned the validation page; wrong-role POST requests and an invalid-CSRF POST
returned 403 with the access-denied page. No records were changed. Browser
acceptance remains pending.

A separate HTTP smoke check started the packaged JAR with an isolated file-
backed H2 database. `/login` and a search for the seeded `Clean Code` title
returned HTTP 200; Vietnamese rendering and locale-cookie persistence also
worked. The same title remained searchable after restarting the JAR against the
same isolated H2 file. The temporary database and processes were removed
afterward. This is not a browser walkthrough or a MySQL/hosted-database
acceptance check. Local P-01 also passed on MySQL Compose: a title and copy
created from the librarian forms and a registered Member account survived
`docker compose down` and `up`; both roles signed in afterward. The disposable
records were removed. The broader manual browser checklist and hosted-database
acceptance remain separate.
