# Week 4 design: architecture and responsibilities

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
MySQL
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

Passwords are stored as BCrypt hashes. URL rules restrict book/copy administration to the `LIBRARIAN` role. Both roles can search and inspect availability. Demo credentials are only seed data for a local database.

## Design limitations at the Week 5 checkpoint

At the Week 5 checkpoint, member profile entities, loans and reservation
transitions were not implemented. The copy edit form no longer allows
workflow-owned statuses to be set manually.

## Week 6 circulation additions

`CirculationService` handles request submission, eligibility checks, approval,
rejection, issue and return. A successful approval updates the request, loan
and physical-copy status in one transaction. Repository locks serialize
approval of the same request, borrowing checks for the same member and the
selection of a physical copy. The return workflow locks its loan before
recording the return and fine. The librarian copy-edit flow cannot clear an
active loan or mark an unloaned copy `ON_LOAN`/`RESERVED`; those state changes
belong to the circulation/reservation workflows.

Fine settlement remains outside the current implementation.

## Current reservation and membership additions

`Reservation` records the member, title, optional assigned copy and hold
deadline. Returning a copy allocates it to the oldest eligible waiting member;
the member has 48 hours to claim it. Expired holds are released when a
circulation or reservation request reaches the service. A background scheduler
is not configured.

`MembershipType` supplies the active-loan limit and loan period. `CirculationService`
applies those values during approval and reservation fulfillment. It calculates
fines at VND 5,000 per chargeable day, excludes configured `LIBRARY_HOLIDAYS`
and caps a loan's fine at VND 100,000. `MemberManagementService` allows
librarians to edit member display names, membership types and enabled status;
the service rejects non-member accounts.
