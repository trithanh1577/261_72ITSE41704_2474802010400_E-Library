# Week 2 requirements baseline

This is the Week 2 requirements baseline, updated through the Week 9
localization and verification work. The implementation status distinguishes
automated evidence from manual acceptance checks.

| ID | Requirement | Priority | Source | Verified by |
|---|---|---|---|---|
| FR-01 | The system shall authenticate users by username or email and password, then show navigation allowed for the user's role. | Must | Week 2 requirements analysis; revised proposal | TC-01, TC-02, TC-28, TC-29, TC-60–TC-62, TC-66; username matching preserves case, email matching ignores case, and lookup keys are refreshed for existing accounts |
| FR-02 | The system shall allow an authenticated user to sign out and end the current session. | Must | Revised proposal | TC-03; logout and a subsequent protected-page request verified through MockMvc/H2 |
| FR-03 | The system shall restrict librarian-only functions to users with the Librarian role. | Must | Course Guidelines §4.1; revised proposal | TC-04, TC-37, TC-42–TC-45; protected routes verified with Spring Security and MockMvc |
| FR-04 | The system shall allow a librarian to create, view, update and deactivate book-title records. | Must | Topic 6; revised proposal | TC-05, TC-06, TC-53 |
| FR-05 | The system shall allow a librarian to add and update physical copies belonging to a book title. | Must | Topic 6 key entity Copy; revised proposal | TC-07, TC-08, TC-53, TC-64 |
| FR-06 | Public registration shall create Member accounts only; a librarian shall be able to view and update a member's display name, membership type and account status. | Must | Topic 6 member-management module; team scope refinement | TC-30, TC-43, TC-45, TC-54, TC-66, TC-67; registration and profile-update integration tests passed; browser checks remain pending |
| FR-07 | The system shall allow users to search active books by title, author, ISBN or category. | Must | Topic 6 catalogue with search; revised proposal | TC-09, TC-10, TC-38, TC-53 |
| FR-08 | The system shall show book details and the current availability of physical copies. | Must | Topic 6 catalogue with search; revised proposal | TC-11, TC-53 |
| FR-09 | The system shall allow an eligible member to submit a borrowing request for an available title and prevent a duplicate pending request. | Must | Topic 6 loan/return module; revised proposal | TC-16, TC-17, TC-65; authenticated request flow and concurrent duplicate protection tested with H2; browser/MySQL check pending |
| FR-10 | The system shall allow a librarian to approve or reject a pending borrowing request. | Must | Topic 6 loan/return module; revised proposal | TC-26, TC-27, TC-46, TC-72; authenticated request and approval workflow passed; browser/MySQL check pending |
| FR-11 | The system shall allow a librarian to create a loan for an eligible member and an available physical copy. | Must | Topic 6 loan/return module; revised proposal | TC-18, TC-20, TC-21, TC-46, TC-50; automated checks passed; browser/MySQL check pending |
| FR-12 | The system shall reject a new loan when the member or copy violates a borrowing rule. | Must | Topic 6 non-CRUD rules; revised proposal | TC-19, TC-21, TC-49; service and eligibility tests passed; browser result pending |
| FR-13 | The system shall allow a librarian to record the return of an active loan. | Must | Topic 6 loan/return module; revised proposal | TC-22, TC-24, TC-46; authenticated return workflow passed; MySQL/browser check pending |
| FR-14 | The system shall calculate overdue fines from chargeable late days, the configured daily rate, holidays and the per-loan cap. | Must | Topic 6 fine calculation; revised proposal | TC-22, TC-23, TC-46; fine rule and overdue-return workflow tested automatically; MySQL/browser check pending |
| FR-15 | The system shall allow a member to reserve an unavailable title and cancel their own unfulfilled reservation. | Must | Topic 6 reservation queue; revised proposal | TC-31, TC-32, TC-47, TC-55, TC-71; authenticated reservation and cancellation flows passed; browser check pending |
| FR-16 | The system shall prevent duplicate active reservations for the same member and title. | Must | Topic 6 reservation queue; revised proposal | TC-31; service tests reject duplicates; browser check pending |
| FR-17 | The system shall process active reservations in FIFO order and release an unclaimed READY copy after 48 hours to the next eligible member. | Must | Topic 6 reservation queue; revised proposal | TC-33, TC-47, TC-52, TC-63, TC-64, TC-72; queue, claim and expiry logic tested automatically; browser check pending |
| FR-18 | The system shall allow a member to view only their own current loans, history, reservations and fines. | Must | Topic 6 circulation reports; revised proposal | TC-46, TC-47, TC-55; member activity verified through authenticated integration flows; browser check pending |
| FR-19 | The system shall allow a librarian to filter current and historical loans and record fine payment or waiver with an audit note. | Must | Topic 6 circulation reports and fine management; team scope refinement | TC-34–TC-37, TC-42, TC-44, TC-48, TC-51; authenticated report and settlement flows passed; browser filters/payment check pending |
| FR-20 | The system shall provide a librarian report ranking book titles by recorded loan count. | Must | Topic 6 circulation and popularity reports; team scope refinement | TC-58, TC-59; ranking verified through MockMvc/H2; browser acceptance pending |

## Requirement identifier notes

FR-01 through FR-18 retain the identifiers from the revised proposal. FR-06
was refined so members create their own Member accounts through public
registration, while librarians review and update member profiles and account
status; public registration cannot create a Librarian. FR-19 adds librarian
loan-history filters and audited fine settlements, and FR-20 adds the
circulation-popularity report. New requirements extend the baseline instead of
renumbering the proposal requirements.

## Business rules

| ID | Rule | Verification |
|---|---|---|
| BR-01 | Membership type sets both active-loan limit and loan period: Standard 5/14 days, Student 3/21 days, Premium 8/21 days. | TC-18, TC-20, TC-50 |
| BR-02 | Only an enabled Member with no overdue active loan and an unused membership loan slot may receive a loan. | TC-19, TC-49 |
| BR-03 | A late return accrues VND 5,000 per chargeable day, skips configured holidays and is capped at VND 100,000 per loan. | TC-23, TC-51 |
| BR-04 | A member may reserve only when no copy is available. Reserved copies are offered in FIFO order to the earliest eligible member, held for 48 hours, and cannot be issued through another borrowing request while held. An ineligible waiting reservation becomes `SKIPPED`; the next eligible member is considered. | TC-31, TC-33, TC-47, TC-52, TC-64, TC-71, TC-72 |
| BR-05 | A member may have at most one active (WAITING or READY) reservation for the same title. | TC-31 |

## Non-functional requirements

| ID | Requirement | Verification / current result |
|---|---|---|
| NFR-01 | At least 95% of catalogue searches shall finish within 2 seconds with 10,000 book titles. | Passed on H2: the clean no-cache Docker Maven `package` on 7 November measured p95 at 14 ms for 100 warmed searches over 10,000 titles; MySQL/PostgreSQL measurement remains pending. |
| NFR-02 | Passwords shall use a one-way adaptive hash and shall not be written to logs. | `AuthFlowIntegrationTest` verifies the stored value has a BCrypt prefix, differs from the submitted password and matches through the encoder; source review found no logging of password fields. |
| NFR-03 | Every protected server-side operation shall check the authenticated user's role. | `SecurityConfig` defines role restrictions; MockMvc integration tests verify member denials for librarian functions, librarian denial of member borrowing requests (TC-74), Librarian denial of member activity (TC-75), 403 rendering for denied POST forwards (TC-76), and anonymous invalid-CSRF handling (TC-77). Full browser role-matrix acceptance remains pending. |
| NFR-04 | Every user-editable field shall have server-side validation and a clear invalid-input message. | Bean Validation and service checks on forms; TC-40/41 cover invalid request and missing-resource feedback; field-by-field negative browser checks remain pending. |
| NFR-05 | Catalogue/circulation writes shall be transactional where one operation updates multiple records. | Transactional service boundaries are source-reviewed. TC-73 injects a failure during return/queue dispatch and verifies that loan and copy changes both roll back on H2; MySQL rollback behavior remains unverified. |
| NFR-06 | Catalogue and circulation data shall remain available after restart. | A seeded title remained searchable after restarting the JAR with the same H2 file database (TC-12). P-01 also verified that a title, copy and registered Member persisted across a local MySQL Compose restart on 7 November 2026. Supabase production persistence remains pending (P-02). |
| NFR-07 (team addition) | The interface shall provide English and Vietnamese labels and validation messages and remember the selected language for one year. | This is an optional interface feature added by the team, not a requirement stated in the course brief. EN/VI links, locale persistence and form feedback are covered by TC-68/69; localized service-error handling is covered by TC-70. Browser checklist A-15 remains pending. |
| NFR-08 | Presentation, business logic and data access shall be separated; controllers shall not contain circulation rules. | Package review and layered architecture in `docs/design/architecture.md`; controllers delegate decisions to services. |
| NFR-09 | Persistence and input-parsing failures shall return a structured recovery response without exposing internal details. | TC-40 verifies invalid request parsing; TC-56 verifies database failure returns a safe service-unavailable page. |
