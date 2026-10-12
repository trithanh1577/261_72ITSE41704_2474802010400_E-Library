# E-Library and Resource Circulation System

Application Programming Project, course 72ITSE41704, class group 261_72ITSE41704_01.

## Week 5 checkpoint

This repository contains the first end-to-end slice required by the Week 5 plan:

- sign in/sign out with separate Librarian and Member permissions;
- catalogue search by title, author, ISBN or category;
- librarian book create, edit and deactivate actions;
- physical book-copy create, status update and availability display;
- MySQL persistence through Spring Data JPA;
- validation on the main book and copy forms;
- seed data for a local demonstration;
- Week 5 unit and repository tests;
- architecture, requirements, use cases, ERD, wireframe notes and work journal in `docs/`.

Borrowing requests, loans, returns, reservations, fines and member management are intentionally not implemented in this checkpoint. They remain in the later slices of the proposal.

## Technology stack

| Technology | Purpose |
|---|---|
| Java 17 | Object-oriented application code |
| Spring Boot 3.3 | Web application framework |
| Spring Data JPA | Repository and persistence layer |
| Spring Security | Login and role-based authorisation |
| Thymeleaf | Server-side HTML rendering |
| Bean Validation | Server-side form validation |
| MySQL | Persistent relational database |
| Maven | Build and dependency management |
| JUnit 5 + Mockito | Automated tests |
| Bootstrap-compatible CSS | Consistent web interface |

## Run locally

### Prerequisites

- JDK 17 or later;
- Maven 3.9 or later;
- MySQL 8 or later.

Create the database once:

```sql
CREATE DATABASE elibrary CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

Set connection variables if the local MySQL account is not the default:

```powershell
$env:DB_URL = "jdbc:mysql://localhost:3306/elibrary?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true"
$env:DB_USERNAME = "root"
$env:DB_PASSWORD = "your-local-password"
```

Run the application:

```powershell
mvn spring-boot:run
```

Open `http://localhost:8080`.

The first run creates demo accounts and sample catalogue data when the tables are empty:

| Username | Password | Role |
|---|---|---|
| `admin` | `admin123` | Librarian |
| `member` | `member123` | Library Member |

These accounts are for local development only and must be changed or removed before a real deployment.

Run tests with:

```powershell
mvn test
```

## Architecture

```text
Thymeleaf pages / Controllers
          ↓
Services and domain rules
          ↓
Spring Data JPA repositories
          ↓
MySQL
```

Business decisions belong in services or domain classes rather than templates or controllers. See [`docs/design/architecture.md`](docs/design/architecture.md).

## Team

| Student ID | Name | Planned role |
|---|---|---|
| 2474802010400 | Nguyễn Thành Trí | Team leader, backend and database |
| 2474802016786 | Phạm Thị Khánh Ngân | Frontend and feature integration |

Each member should commit their own work under their own Git identity. The repository history is part of the course evidence.

## Repository map

```text
src/main/java/com/elibrary/       Java application
src/main/resources/templates/     Thymeleaf pages
src/main/resources/static/        CSS
src/test/java/com/elibrary/       Unit and repository tests
docs/                             Week 1–5 evidence and design artefacts
```

The development order is recorded in [`docs/weekly-progress.md`](docs/weekly-progress.md),
from the Week 1 scope decision through the Week 5 checkpoint.

## Honest Week 5 limitations

This is a checkpoint, not the final system. The current version has no loan transaction, reservation queue, fine calculation, member CRUD, pagination, audit trail or optimistic locking workflow. The copy `@Version` field is present as a first persistence safeguard, but conflict handling still needs a later iteration. These limitations and the initial setup problem caused by a missing local JDK/Maven are recorded in [`docs/week5-status.md`](docs/week5-status.md) and [`docs/journal.md`](docs/journal.md).
