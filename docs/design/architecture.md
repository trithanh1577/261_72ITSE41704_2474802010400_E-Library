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

## Design limitations at this checkpoint

The application does not yet have member profile entities, loans or reservation state transitions. The copy status select is intentionally simple for Week 5; later loan/return services must become the authority for `ON_LOAN` and `RESERVED` transitions instead of allowing arbitrary staff edits.
