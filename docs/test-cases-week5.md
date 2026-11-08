# Week 5 test-case plan and results

The full course test log grows with each iteration. These cases cover the Week 5 catalogue slice. Week 6 circulation cases are in [`test-cases-week6.md`](test-cases-week6.md). `Actual result` records the strongest evidence currently available. MockMvc/H2 and source-review results are identified explicitly; they do not count as manual browser checks against MySQL or the hosted database.

| ID | Requirement | Precondition | Input / steps | Expected result | Actual result | Status |
|---|---|---|---|---|---|---|
| TC-01 | FR-01 | Seed data exists | Sign in as `admin` with `admin123` | Catalogue opens with Librarian badge | Librarian sign-in succeeded in authenticated MockMvc workflows; badge appearance was not asserted. Manual browser check pending. | Partial (automated) |
| TC-02 | FR-01 | Seed data exists | Sign in as `member` with `member123` | Catalogue opens with Member badge | Member sign-in and protected-page behavior passed in MockMvc; badge appearance was not asserted. Manual browser check pending. | Partial (automated) |
| TC-03 | FR-02 | Authenticated user | Select Sign out | Session ends and login page appears | Passed through MockMvc/H2: logout redirected to `/login?logout`; a subsequent protected-page request redirected to sign-in. Manual browser check pending. | Partial (automated) |
| TC-04 | FR-03 | Sign in as `member` | Open `/books/new` directly | Access is denied; no form is shown | Passed through MockMvc with the real Spring Security filters; browser check pending. | Partial (automated) |
| TC-05 | FR-04 | Librarian signed in | Submit a valid new book | New book appears in catalogue and database | Created `Restart check 20261019` with the local H2 profile; detail page opened successfully. The create/search/detail path also passed in MockMvc/H2. | Passed (local H2; browser/MySQL pending) |
| TC-06 | FR-04 | Librarian signed in | Submit blank title | Save is rejected with a title message | Passed through MockMvc/H2: `Title is required` was rendered and no row was saved. | Passed (automated H2) |
| TC-07 | FR-05 | Existing book | Add unique copy code `TEST-001` | Copy is shown as AVAILABLE | Passed in the authenticated MockMvc/H2 create-title-and-copy flow; browser check pending. | Partial (automated) |
| TC-08 | FR-05 | Existing book | Add duplicate copy code | Save is rejected and no second copy is created | Passed through MockMvc/H2: duplicate-code message rendered and copy count remained one. | Passed (automated H2) |
| TC-09 | FR-07 | Seed catalogue exists | Search `Martin` | Clean Code is returned | Title/author/ISBN/category search passed in repository tests; authenticated title search passed in MockMvc/H2. The exact seed-term browser check remains pending. | Partial (automated) |
| TC-10 | FR-07 | Seed catalogue exists | Search a non-existing term | Empty state is shown without a crash | Blank-search behavior is covered by service tests; exact no-match empty-state rendering remains pending. | Partial (automated) |
| TC-11 | FR-08 | Book has copies | Open Clean Code details | Copy rows and available count are correct | Passed through MockMvc/H2: detail page showed the created copy and available count of one. | Partial (automated) |
| TC-12 | NFR-06 | Seeded H2 catalogue exists | Stop and restart the packaged JAR using the same file database, then search for `Clean Code` | The same title remains searchable after restart | On 7 November, the packaged JAR returned HTTP 200 for the title search before and after restart against one isolated H2 file. The temporary database was removed afterward. MySQL/Supabase restart checks remain pending. | Passed (H2 fallback) |
| TC-13 | NFR-02 | Database is accessible | Inspect the user password column | Password is not stored in clear text | Passed in `AuthFlowIntegrationTest`: stored password has a BCrypt prefix, differs from the submitted password and verifies through the encoder. | Passed (automated H2) |
| TC-14 | NFR-04 | Librarian signed in | Submit over-length title | Server-side validation rejects it | Passed through MockMvc/H2: a 181-character title returned the field validation message and was not saved. | Passed (automated H2) |
| TC-15 | NFR-08 | Source available | Inspect package responsibilities | Controllers do not contain repository queries | Source review confirmed controllers delegate to services and do not inject repositories; see [`design/architecture.md`](design/architecture.md). | Passed (source review) |

## Unit tests included

- `BookServiceTest`: blank-search behaviour and keyword trimming.
- `BookRepositoryTest`: case-insensitive search through title/author/ISBN/category.

## Automated verification recorded

On 12 October 2026, the local Maven command compiled 20 Java source files and ran the repository/service test suite:

```text
Tests run: 3, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

The remaining browser/MySQL checks are still pending and must be filled with observations from the team's configured environment. Automated results above must not be presented as browser or production-database acceptance.
