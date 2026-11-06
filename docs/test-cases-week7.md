# Week 7 test cases

These additional cases cover account identifiers, reservation transitions,
fine records and librarian loan search. Manual execution against MySQL or the
deployed service is pending until performed by a team member.

| ID | Requirement | Purpose | Expected result | Actual result | Status |
|---|---|---|---|---|---|
| TC-28 | FR-01 | Sign in with exact-case username | Correct-case username authenticates; a differently cased username does not match | Automated service and integration checks passed; browser check pending | Partial |
| TC-29 | FR-01 | Sign in with email | Email lookup succeeds regardless of email letter case | Automated integration check passed with mixed-case email; browser check pending | Partial |
| TC-30 | FR-06 | Register a member | New account receives `MEMBER` role and stored password is hashed | Automated integration check passed; browser check pending | Partial |
| TC-31 | FR-15 / FR-16 / BR-05 | Join reservation queue | Unavailable active title is queued once; duplicate active reservation is rejected | Authenticated workflow creates the reservation; service test rejects a duplicate; browser run pending | Partial |
| TC-32 | FR-15 | Cancel an active reservation | Waiting/ready reservation is cancelled and any held copy is released | Automated service check passed; browser check pending | Partial |
| TC-33 | FR-17 / BR-04 | Reservation FIFO and expiry | Returned copy goes to earliest eligible member; after 48 hours it goes to next eligible member | FIFO, 48-hour expiry and next-member assignment covered by automated service checks; browser check pending | Partial |
| TC-34 | FR-19 | Search circulation history | Librarian can filter by member/book/copy and loan status | Member search verified in authenticated integration workflow; browser filters pending | Partial |
| TC-35 | FR-19 | Record a fine payment | Outstanding amount is reduced and actor/time/note are visible in history | Payment and audit fields verified in authenticated integration workflow; browser check pending | Partial |
| TC-36 | FR-19 | Waive a fine | Outstanding amount is settled and a waiver record with note is stored | Automated service check passed; browser check pending | Partial |
| TC-37 | NFR-03 | Protect report and settlement actions | Member cannot open the report or submit a fine settlement | MockMvc verified 403 for both routes; manual browser sign-in remains to be run | Partial |
| TC-38 | NFR-01 | Navigate catalogue pages | Search with more than 24 results, move next/previous and keep the search term | Automated repository check covers pages, page size and total; browser navigation pending | Partial |

The automated service tests for these rules are listed in
[`test-cases-week6.md`](test-cases-week6.md). A pass in a unit test is not a
claim that these browser/database cases have passed.
