# Week 6 test cases

This table extends the Week 5 catalogue cases with the circulation iteration.
The `Actual result` column distinguishes automated evidence from browser
acceptance. Service and MockMvc results use H2; the separate MySQL/browser run
remains pending until a team member performs it.

| ID | Requirement / rule | Purpose | Precondition and steps | Expected result | Actual result | Status |
|---|---|---|---|---|---|---|
| TC-16 | FR-09 | Member requests a book | Sign in as `member`, open an active book and submit a note | Request is saved as `PENDING` and appears in My activity | Passed through authenticated MockMvc/H2 in `LibraryWorkflowIntegrationTest`; manual browser/MySQL check pending. | Partial (automated) |
| TC-17 | FR-09 | Prevent repeat requests, including near-simultaneous submissions | Submit a second request for the same book before a decision | Second request is rejected with a clear message; same-member submissions are serialized before checking for a duplicate | Passed in `CirculationServiceTest`; duplicate request is rejected without a second pending request, and submission locks the member row before checking. Manual browser/MySQL concurrency check pending. | Partial (automated) |
| TC-18 | FR-11 / BR-01 | Approve an eligible Standard-member request | Librarian approves a pending request when a copy is available | One loan is created, due in 14 calendar days, and copy becomes `ON_LOAN` | Passed in service and authenticated workflow tests; approval creates one loan and updates the copy. Manual MySQL/browser check pending. | Partial (automated) |
| TC-19 | FR-12 / BR-02 | Reject overdue member | Give the member an overdue active loan, then approve a new request | Approval is rejected and no copy/loan state changes | Passed in `CirculationServiceTest`; overdue eligibility rejects approval. Manual browser check pending. | Partial (automated) |
| TC-20 | FR-11 / BR-01 | Enforce Standard membership loan limit | Give the Standard member five active loans, then approve another request | Approval is rejected and no extra loan is created | Passed in eligibility/service tests; Standard limit is five active loans. Manual browser check pending. | Partial (automated) |
| TC-21 | FR-10 / FR-11 / FR-12 | Handle unavailable copy | Make all copies unavailable, then approve a pending request | Approval is rejected and request remains pending | Passed in `CirculationServiceTest`; no loan is issued when no copy is available. Manual browser check pending. | Partial (automated) |
| TC-22 | FR-13 / FR-14 | Return on due date | Return an active loan on its due date | Return is recorded, fine is zero, and copy becomes `AVAILABLE` | Passed in `CirculationServiceTest`; on-time return has zero fine. The authenticated workflow also returned a loan and freed its copy. Manual browser check pending. | Partial (automated) |
| TC-23 | BR-03 | Calculate late fine | Return a copy three chargeable days after its due date | Return is recorded and fine is VND 15,000 | Passed in `FinePolicyTest`/`CirculationServiceTest`; the daily rate is VND 5,000. Manual MySQL/browser check pending. | Partial (automated) |
| TC-24 | FR-13 | Prevent duplicate return | Submit return for an already returned loan | A clear rejection is shown; recorded fine and copy status are unchanged | Passed in `CirculationServiceTest`; a second return is rejected without changing the recorded return. Manual browser check pending. | Partial (automated) |
| TC-25 | FR-03 / NFR-03 / NFR-04 | Check permissions and note validation | As `member`, try librarian URLs including `POST /books/save`; submit a note longer than 500 characters | Access is denied; invalid note is not saved | Member access to librarian book form/report/member/fine routes is denied in MockMvc; oversized request note is rejected in service tests. Manual browser check pending. | Partial (automated) |
| TC-26 | FR-10 / FR-11 | Do not issue a deactivated title | Submit a request, deactivate the title, then approve the pending request | Approval is rejected; no loan is created and the request remains pending | Passed in `CirculationServiceTest`; inactive-title approval is rejected. Manual browser check pending. | Partial (automated) |
| TC-27 | FR-10 | Librarian rejects a request | Reject a pending request with a decision note | Request becomes `REJECTED` and the note is shown in the request list | Passed in `CirculationServiceTest`; request is rejected with the recorded decision note. Manual browser check pending. | Partial (automated) |

## Automated service tests added

The Week 7 checkpoint run is recorded in [`week7-status.md`](week7-status.md):
`mvn test` passed 34 tests (0 failures, 0 errors, 0 skipped) at that checkpoint.
The later Week 8 full-suite result is in [`test-cases-week8.md`](test-cases-week8.md). This includes
`CirculationServiceTest`, which covers request submission and duplicate
prevention, note-length validation, successful approval, librarian rejection,
deactivated-title and overdue-member rejection, the five-loan limit,
unavailable-copy rejection, late-fine calculation, on-time return and
duplicate-return rejection, fine settlement, FIFO reservation allocation and
expired-hold dispatch. `BookCopyServiceTest` checks that an active loan
cannot be cleared and that an unloaned copy cannot be manually marked
`RESERVED`. These automated tests use service/repository mocks; they do not
replace the pending end-to-end checks above.

An earlier HTTP smoke check on isolated in-memory H2 verified registration,
sign-in, catalogue/detail pages and role-based access to member and librarian
pages. The new email sign-in, fine-settlement and scheduled-expiry additions
still require browser/database acceptance checks.

## Review notes

- The member activity page queries by the signed-in username so another
  member's requests and loans are not included in the result.
- Loan approval and return run in transactions. Pessimistic locks protect the
  selected copy and the loan being returned; a member row is also locked while
  borrowing eligibility is checked.
- Database concurrency behavior and browser flows still need manual checks.
