# Week 9 verification record

| ID | Requirement | Test | Expected result | Result |
|---|---|---|---|---|
| TC-63 | FR-17 / NFR-05 | Read reservation lists, waiting counts and a member's active reservation status. | Read-only operations query the stored state without invoking expiry processing or reallocating a copy. The scheduled task or a state-changing circulation action handles expiry. | Passed in `CirculationServiceTest` on 6 November 2026. |
| TC-64 | FR-05 / FR-17 | Open the edit form for a reservation-held copy, correct its copy code, and attempt to change its status to AVAILABLE. | The form keeps the status workflow-managed; code correction is saved, but the queue retains control of RESERVED status. | Passed in `CoreUseCaseIntegrationTest` and `BookCopyServiceTest` on 6 November 2026. |
| TC-65 | FR-09 / NFR-05 | Submit two borrowing requests for the same member and title at the same time in separate threads. | Exactly one request succeeds; one pending request remains. | Passed in `CoreUseCaseIntegrationTest` with two H2-backed application transactions on 6 November 2026. MySQL/browser concurrency acceptance remains pending. |
| TC-66 | FR-01 / FR-06 | Register a member with a short non-empty password, then sign in using the exact-case username and email. | Registration succeeds without an arbitrary minimum-length rule; the password is BCrypt-hashed and both identifiers authenticate the account. | Passed in `AuthFlowIntegrationTest` with a three-character fixture on 6 November 2026. |
| TC-67 | FR-06 / NFR-04 | Register a member using 25 copies of `ệ` as the password (75 UTF-8 bytes). | The form shows a clear validation message, creates no account and does not pass a truncated value to BCrypt. | Passed in `UserRegistrationServiceTest` and `AuthFlowIntegrationTest` on 6 November 2026. |

## Build and local runtime evidence

- The offline Maven test/package workflow compiled the current application
  and test sources. The full suite passed 77 tests with no
  failures, errors or skips on 6 November 2026.
- The latest clean H2 catalogue benchmark measured p95 at 11 ms for 100 warmed searches
  across 10,000 titles. This is a local H2 result, not a MySQL or Supabase
  performance measurement.
- The packaged JAR was started twice against the same isolated H2 file
  database. `/login` and `/books` returned HTTP 200 on both starts, and the
  seeded `Clean Code` title remained visible after restart. Both temporary
  application processes were stopped.
- MySQL/Supabase schema migration, browser acceptance, Docker image build and
  hosted persistence remain pending; see [`week9-acceptance.md`](week9-acceptance.md).
