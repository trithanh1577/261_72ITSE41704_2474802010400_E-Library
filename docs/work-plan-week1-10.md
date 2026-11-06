# Ten-week work plan and Week 5 boundary

| Week | Main tasks | Owner(s) | Output |
|---|---|---|---|
| 1 | Read course documents, confirm topic/team, create repository and README. | Trí: repository/backend setup; Ngân: README/UI folders; both: registration. | Topic registered; repository ready. |
| 2 | Confirm users, scope, FR/NFR, use cases, business rules and screens. | Trí: librarian rules/data; Ngân: member flows/screens; both: review. | Requirements baseline. |
| 3 | Write detailed use cases, task breakdown, proposal and timeline. | Trí: backend/rules; Ngân: UI/member sections; both: proofread. | Proposal PDF/DOCX. |
| 4 | Design architecture, UML, ERD, navigation and wireframes. | Trí: architecture/ERD/UML; Ngân: wireframes/navigation. | Design artefacts in `/docs/design`. |
| 5 | Build the first end-to-end slice: login, catalogue, copies, search and persistence. | Trí: entities/repositories/services/security; Ngân: catalogue/search interface and test execution. | Working skeleton; local v0.1 checkpoint. |
| 6 | Implement borrowing requests, librarian review, loans, returns, validation and unit tests. | Trí: services/rules/tests; Ngân: request/member UI and test execution. | Circulation slice, use cases, test results and manual test checklist. |
| 7 | Implement reservations, member status/history and refactor core services. | Trí: service/refactor/reservation integration; Ngân: reservation/status pages. | Core prototype; v0.2 checkpoint; rubric self-check (retrospective draft, team review pending). |
| 8 | Improve exception handling, transactions, security, performance checks, popularity reporting and responsive UI. | Trí: reliability/security/performance; Ngân: responsive UI/error states. | Hardened build, circulation-popularity report and updated design. |
| 9 | Freeze features, run end-to-end/user tests, prepare run guide and report draft. | Trí: integration/run guide; Ngân: UI/user tests/screenshots/report; both: fixes. | v1.0 checkpoint and report draft. |
| 10 | Clean repository and complete final report/submission. | Trí: code/repository review; Ngân: document formatting; both: final check. | Final report and repository. |

The Week 5 checkpoint is on `feature/week-5-catalogue`. Week 6–8 implementation is in the local branch `feature/week-7-reservations`. Local tags `v0.1` and `v0.2` point to the Week 5 and Week 7 checkpoint commits; they have not been pushed. The Week 8 changes and Week 9 verification additions are still uncommitted. A clean-source package on this branch passed; the fetched `origin/main` snapshot at `d2f884c` also built successfully with 19 tests passing when run through the temporary `Z:` mapping. MySQL/browser/cloud acceptance and teammate review remain before a Week 9 `v1.0` release. A Git-history review on 6 November shows commits recorded for `trithanh1577` in 3 distinct ISO weeks and for `Ngan3112` in 2; this does not meet the course self-check asking for each member's commits across at least 5 weeks. Preserve the real authorship and do not create retrospective commits. The final report is a separate course deliverable and is not included in this technical work plan.
