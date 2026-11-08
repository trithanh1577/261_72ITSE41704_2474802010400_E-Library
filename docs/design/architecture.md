# Architecture and responsibilities

## Selected architecture

The application uses a layered architecture. Dependencies point from the web layer toward the service layer and then toward the repository layer.

```text
Thymeleaf templates + Controllers
              |
              v
Services + domain entities + validation
              |
              v
Spring Data JPA repositories
              |
              v
MySQL (local) / PostgreSQL (hosted)
```

Controllers receive HTTP input and choose a view. Services enforce application decisions such as unique ISBN/copy code and active-book search. Repositories are the only layer that knows persistence queries. The templates do not calculate availability; the service obtains it from the repository.

## Week 5 responsibilities

| Class | Responsibility |
|---|---|
| `Book` | Holds title data and active/deactivated state. |
| `BookCopy` | Holds one physical copy, status and optimistic-lock version. |
| `BookService` | Searches active books, checks duplicate ISBNs and exposes availability. |
| `BookCopyService` | Creates/updates copies and checks duplicate copy codes. |
| `BookRepository` | Loads active books and executes the keyword query. |
| `BookCopyRepository` | Loads copies and counts available copies. |
| `BookController` | Maps web requests and returns catalogue/form views. |
| `BookCopyController` | Maps copy form requests. |

## Security decision

Passwords are stored as BCrypt hashes. Username login uses a lowercase SHA-256 lookup key derived from the exact username; this avoids case-insensitive database collations treating differently cased identifiers as equal. Startup refreshes keys written by the earlier encoding. Email login is case-insensitive. URL rules restrict book/copy administration to the `LIBRARIAN` role. Both roles can search and inspect availability. Demo credentials are only seed data for a local database.

## Earlier checkpoint limitations

At the Week 5 checkpoint, member profile entities, loans and reservation
transitions were not implemented. The copy edit form no longer allows
workflow-owned statuses to be set manually.

## Circulation, reservation and account workflows

`CirculationService` handles request submission, eligibility checks, approval,
rejection, issue and return. A successful approval updates the request, loan
and physical-copy status in one transaction. Repository locks serialize
approval of the same request, borrowing checks for the same member and the
selection of a physical copy. The return workflow locks its loan before
recording the return and fine. The librarian copy-edit flow cannot clear an
active loan or release a `RESERVED` copy; it also cannot mark an ordinary copy
`ON_LOAN`/`RESERVED`. Those status changes belong to the circulation and
reservation workflows. A librarian can still correct a copy code while its
status is workflow-managed.

`Reservation` records the member, title, optional assigned copy and hold
deadline. Returning a copy allocates it to the oldest eligible waiting member;
the member has 48 hours to claim it. Expired holds are released during
state-changing circulation or reservation operations, and a scheduled task
checks for expired holds every minute. Read-only page requests do not change
reservation state.

`MembershipType` supplies the active-loan limit and loan period. `CirculationService`
uses `LoanTerms` to apply those values during approval and reservation
fulfillment. `BorrowingEligibility` evaluates account status, overdue state and
membership limits as one business decision. `ReservationQueue` owns FIFO
allocation and skips members who are no longer eligible. `FinePolicy` calculates
fines at VND 5,000 per chargeable day, excludes configured `LIBRARY_HOLIDAYS`
and caps a loan's fine at VND 100,000. `MemberManagementService` allows
librarians to edit member display names, membership types and enabled status;
the service rejects non-member accounts.

`FineSettlement` stores each payment or waiver as an audit record with amount,
action, librarian, note and timestamp. `FineBalance` calculates the remaining
amount and rejects settlement when no balance remains. Librarian circulation
reports filter and paginate current and historical loans. Catalogue search and
reports also use server-side pagination. The circulation report includes a
five-title popularity summary, grouped by recorded loans and ordered from most
to least borrowed. The repository runs the aggregate query; searches and
reservations are not counted as loans.

An assessed fine is stored as `Loan.fineAmount`; the design does not create a
separate fine row because each loan receives one assessed amount on return.
Payments and waivers are separate `FineSettlement` rows so the audit trail is
preserved.

The topic's key concepts map to the implementation as follows: `Book` is a
Title, `BookCopy` is a physical Copy, and `UserAccount` represents either a
Member or Librarian through its role. `MembershipType`, `Loan` and
`Reservation` are explicit domain types; a Fine is the amount assessed on its
Loan and its later adjustments are `FineSettlement` records.

`GlobalExceptionHandler` converts invalid request parameters, missing records,
constraint conflicts and database failures into status-specific pages. The
pages show a recovery action without exposing stack traces or SQL details.

The design contains twelve meaningful domain types: seven persistent entities,
three Java classes and two Java records used for policy, value and aggregate
responsibilities (`FinePolicy`, `FineBalance`, `BorrowingEligibility`,
`LoanTerms` and `ReservationQueue`). They encode different persistence and
circulation responsibilities rather than counting controllers or repositories
toward the domain-class target.

## Verification boundaries

Automated repository tests use H2; the catalogue performance check seeds 10,000
titles and measures 100 warmed searches. This is a repeatable local indicator,
not a substitute for measuring the same requirement on the intended MySQL or
PostgreSQL deployment. Browser workflows, existing-database schema updates,
and cloud persistence still need acceptance checks in those environments.
