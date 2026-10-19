# Week 5 test-case plan and results

The full course test log grows with each iteration. These cases cover the Week 5 catalogue slice. Week 6 circulation cases are in [`test-cases-week6.md`](test-cases-week6.md). The `Actual result` column must be updated after running the application on the team's configured machine.

| ID | Requirement | Precondition | Input / steps | Expected result | Actual result | Status |
|---|---|---|---|---|---|---|
| TC-01 | FR-01 | Seed data exists | Sign in as `admin` with `admin123` | Catalogue opens with Librarian badge | Pending local run | Pending |
| TC-02 | FR-01 | Seed data exists | Sign in as `member` with `member123` | Catalogue opens with Member badge | Pending local run | Pending |
| TC-03 | UC-01 | Authenticated user | Select Sign out | Session ends and login page appears | Pending local run | Pending |
| TC-04 | FR-02 | Sign in as `member` | Open `/books/new` directly | Access is denied; no form is shown | Pending local run | Pending |
| TC-05 | FR-03 | Librarian signed in | Submit a valid new book | New book appears in catalogue and database | Pending local run | Pending |
| TC-06 | FR-03 | Librarian signed in | Submit blank title | Save is rejected with a title message | Pending local run | Pending |
| TC-07 | FR-04 | Existing book | Add unique copy code `TEST-001` | Copy is shown as AVAILABLE | Pending local run | Pending |
| TC-08 | FR-04 | Existing book | Add duplicate copy code | Save is rejected and no second copy is created | Pending local run | Pending |
| TC-09 | FR-06 | Seed catalogue exists | Search `Martin` | Clean Code is returned | Pending local run | Pending |
| TC-10 | FR-06 | Seed catalogue exists | Search a non-existing term | Empty state is shown without a crash | Pending local run | Pending |
| TC-11 | FR-07 | Book has copies | Open Clean Code details | Copy rows and available count are correct | Pending local run | Pending |
| TC-12 | NFR-06 | A book was created | Restart application | Created book remains available | Pending local run | Pending |
| TC-13 | NFR-02 | Database is accessible | Inspect the user password column | Password is not stored in clear text | Pending local run | Pending |
| TC-14 | NFR-04 | Librarian signed in | Submit over-length title | Server-side validation rejects it | Pending local run | Pending |
| TC-15 | NFR-08 | Source available | Inspect package responsibilities | Controllers do not contain repository queries | Pending local run | Pending |

## Unit tests included

- `BookServiceTest`: blank-search behaviour and keyword trimming.
- `BookRepositoryTest`: case-insensitive search through title/author/ISBN/category.

## Automated verification recorded

On 12 October 2026, the local Maven command compiled 20 Java source files and ran the repository/service test suite:

```text
Tests run: 3, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

The 15 functional/manual cases above remain pending because they require a running application, browser and configured MySQL database. They must be filled with actual observations rather than copied from the expected column.
