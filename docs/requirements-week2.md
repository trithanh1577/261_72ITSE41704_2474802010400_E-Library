# Week 2 requirements baseline

This is the working requirements baseline for the Week 5 slice. The approved
proposal contains the full FR-01 to FR-18 circulation scope; this checkpoint
implements the catalogue and access part, while the remaining FRs are kept for
later weeks.

| ID | Requirement | Priority | Source | Verified by |
|---|---|---|---|---|
| FR-01 | The system shall authenticate users by username or email and password. | Must | Week 2 requirements analysis; proposal | TC-01, TC-02 |
| FR-02 | The system shall restrict administrative functions to users with the Librarian role. | Must | Course Guidelines §4.1; proposal | TC-04 |
| FR-03 | The system shall allow a librarian to create, view, update and deactivate book-title records. | Must | Topic 6; proposal | TC-05, TC-06 |
| FR-04 | The system shall allow a librarian to add and update physical copies belonging to a book title. | Must | Topic 6 key entity Copy; proposal | TC-07, TC-08 |
| FR-05 | The system shall allow a librarian to create, update, view and deactivate member accounts. | Must (later slice) | Topic 6 member-management module | Future member tests |
| FR-06 | The system shall allow users to search by title, author, ISBN or category. | Must | Topic 6 catalogue with search | TC-09, TC-10 |
| FR-07 | The system shall show the current number of available copies for each title. | Must | Topic 6 catalogue with search | TC-11 |
| FR-08 | The system shall allow a librarian to issue an available copy to an eligible member. | Must (Week 6) | Topic 6 loan/return module | Future loan tests |
| FR-09 | The system shall reject a loan when the member or copy violates a circulation rule. | Must (Week 6) | Topic 6 non-CRUD rules | Future loan tests |
| FR-10 | The system shall allow a librarian to record the return of an active loan. | Must (Week 6) | Topic 6 loan/return module | Future return tests |
| FR-11 | The system shall calculate overdue fines using late days and the configured daily rate. | Must (Week 6) | Topic 6 fine calculation | Future fine tests |
| FR-12 | The system shall allow a member to reserve a title when no copy is currently available. | Must (Week 7) | Topic 6 reservation queue | Future reservation tests |
| FR-13 | The system shall prevent duplicate active reservations for the same member and title. | Must (Week 7) | Topic 6 reservation queue | Future reservation tests |
| FR-14 | The system shall allow a member to cancel an active reservation that has not been fulfilled. | Must (Week 7) | Topic 6 reservation queue | Future reservation tests |
| FR-15 | The system shall allocate a returned copy to the earliest eligible active reservation. | Must (Week 7) | Topic 6 reservation queue | Future reservation tests |
| FR-16 | The system shall allow a member to view active loans, due dates, overdue status, reservations and fines. | Must (Week 7) | Topic 6 circulation reports | Future member tests |
| FR-17 | The system shall allow a librarian to view current and historical loans by member, book or status. | Must (later slice) | Topic 6 circulation reports | Future librarian tests |
| FR-18 | The system shall allow a librarian to mark a recorded fine as paid or waived with a note. | Should (later slice) | Topic 6 fine management | Future fine tests |

## Non-functional requirements

| ID | Requirement | Verification planned |
|---|---|---|
| NFR-01 | At least 95% of catalogue searches shall finish within 2 seconds with 10,000 book titles. | Seed/performance test in Week 8. |
| NFR-02 | Passwords shall use a one-way adaptive hash and shall not be written to logs. | Inspect stored values and logs. |
| NFR-03 | Every protected server-side operation shall check the authenticated user's role. | Try member and librarian accounts. |
| NFR-04 | Every user-editable field shall have server-side validation and a clear invalid-input message. | Negative tests for create/update forms. |
| NFR-05 | Catalogue/circulation writes shall be transactional where one operation updates multiple records. | Service/integration tests from Week 6 onward. |
| NFR-06 | Catalogue and circulation data shall remain available after restart. | Restart and retrieve seed/created records. |
| NFR-07 | The main workflows shall work in current Chrome and Edge. | Browser checklist in Week 9. |
| NFR-08 | Presentation, business logic and data access shall be separated; controllers shall not contain circulation rules. | Package review and design evidence. |
