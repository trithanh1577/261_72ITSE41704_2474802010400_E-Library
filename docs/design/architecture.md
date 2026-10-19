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
transitions were not implemented. Week 6 adds the loan/return authority for
`ON_LOAN`; reservation transitions remain Week 7 work. The copy edit form no
longer allows those workflow-owned statuses to be set manually.

## Week 6 circulation additions

`CirculationService` handles request submission, eligibility checks, approval,
rejection, issue and return. A successful approval updates the request, loan
and physical-copy status in one transaction. Repository locks serialize
approval of the same request, borrowing checks for the same member and the
selection of a physical copy. The return workflow locks its loan before
recording the return and fine. The librarian copy-edit flow cannot clear an
active loan or mark an unloaned copy `ON_LOAN`/`RESERVED`; those state changes
belong to the circulation/reservation workflows.

Reservations and fine settlement are still outside the Week 6 implementation.
