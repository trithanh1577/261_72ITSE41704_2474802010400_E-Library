# Week 6 test cases

This table extends the Week 5 catalogue cases with the circulation iteration.
The cases below are end-to-end/manual checks and remain `Pending` until a team
member runs them with the configured application and database. Automated
service/repository test results are recorded separately below.

| ID | Requirement / rule | Purpose | Precondition and steps | Expected result | Actual result | Status |
|---|---|---|---|---|---|---|
| TC-16 | Borrow request | Member requests a book | Sign in as `member`, open an active book and submit a note | Request is saved as `PENDING` and appears in My activity | Pending local run | Pending |
| TC-17 | Duplicate pending request | Prevent repeat requests | Submit a second request for the same book before a decision | Second request is rejected with a clear message | Pending local run | Pending |
| TC-18 | Loan issue / BR-01 | Approve an eligible request | Librarian approves a pending request when a copy is available | One loan is created, due in 14 calendar days, and copy becomes `ON_LOAN` | Pending local run | Pending |
| TC-19 | BR-02 | Reject overdue member | Give the member an overdue active loan, then approve a new request | Approval is rejected and no copy/loan state changes | Pending local run | Pending |
| TC-20 | BR-03 | Enforce five-loan limit | Give the member five active loans, then approve another request | Approval is rejected and no extra loan is created | Pending local run | Pending |
| TC-21 | FR-08 | Handle unavailable copy | Make all copies unavailable, then approve a pending request | Approval is rejected and request remains pending | Pending local run | Pending |
| TC-22 | FR-10 / FR-11 | Return on due date | Return an active loan on its due date | Return is recorded, fine is zero, and copy becomes `AVAILABLE` | Pending local run | Pending |
| TC-23 | BR-04 | Calculate late fine | Return a copy three calendar days after its due date | Return is recorded and fine is VND 15,000 | Pending local run | Pending |
| TC-24 | Return validation | Prevent duplicate return | Submit return for an already returned loan | A clear rejection is shown; recorded fine and copy status are unchanged | Pending local run | Pending |
| TC-25 | NFR-03 / NFR-04 | Check permissions and note validation | As `member`, try librarian URLs including `POST /books/save`; submit a note longer than 500 characters | Access is denied; invalid note is not saved | Pending local run | Pending |
| TC-26 | FR-09 | Do not issue a deactivated title | Submit a request, deactivate the title, then approve the pending request | Approval is rejected; no loan is created and the request remains pending | Pending local run | Pending |
| TC-27 | Borrow request | Librarian rejects a request | Reject a pending request with a decision note | Request becomes `REJECTED` and the note is shown in the request list | Pending local run | Pending |

## Automated service tests added

`mvn test` passed 17 tests (0 failures, 0 errors, 0 skipped). This includes
`CirculationServiceTest`, which covers request submission and duplicate
prevention, note-length validation, successful approval, librarian rejection,
deactivated-title and overdue-member rejection, the five-loan limit,
unavailable-copy rejection, late-fine calculation, on-time return and
duplicate-return rejection. `BookCopyServiceTest` checks that an active loan
cannot be cleared and that an unloaned copy cannot be manually marked
`RESERVED`. These automated tests use service/repository mocks; they do not
replace the pending end-to-end checks above.

## Review notes

- The member activity page queries by the signed-in username so another
  member's requests and loans are not included in the result.
- Loan approval and return run in transactions. Pessimistic locks protect the
  selected copy and the loan being returned; a member row is also locked while
  borrowing eligibility is checked.
- Database concurrency behavior and browser flows still need manual checks.
