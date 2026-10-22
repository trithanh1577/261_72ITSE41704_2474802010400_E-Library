# E-Library and Resource Circulation System

Application Programming Project, course 72ITSE41704, class group 261_72ITSE41704_01.

## Week 6 checkpoint (manual checks pending)

The Week 5 catalogue slice is implemented. Week 6 circulation work is in progress:

- sign in/sign out with separate Librarian and Member permissions;
- catalogue search by title, author, ISBN or category;
- librarian book create, edit and deactivate actions;
- physical book-copy create, status update and availability display;
- MySQL persistence through Spring Data JPA;
- PostgreSQL support for a hosted deployment using Supabase;
- validation on the main book and copy forms;
- seed data for a local demonstration;
- 17 automated unit and repository tests (all passing in the local verification run);
- member borrowing requests, librarian approval/rejection, 14-day loans, return recording and overdue fine calculation (Week 6 implementation; manual database/browser checks pending);
- architecture, requirements, use cases, ERD, wireframe notes and work journal in `docs/`.

Reservation queues, fine settlement, member account management and circulation reports remain later work. Week 6 changes need review and test execution before they are considered complete.

## Technology stack

| Technology | Purpose |
|---|---|
| Java 17 | Object-oriented application code |
| Spring Boot 3.3 | Web application framework |
| Spring Data JPA | Repository and persistence layer |
| Spring Security | Login and role-based authorisation |
| Thymeleaf | Server-side HTML rendering |
| Bean Validation | Server-side form validation |
| MySQL / PostgreSQL | Local and hosted relational database options |
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

If MySQL is not available for local viewing, the file-based H2 fallback can
be started with `mvn spring-boot:run "-Dspring-boot.run.profiles=h2"`. It keeps
records in the ignored `data/elibrary.mv.db` file across application restarts.
This fallback does not replace the standard MySQL setup used for course checks.

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

## Public deployment (Render + Supabase)

The repository includes a Dockerfile and Render Blueprint for a public web
service. The hosted app can use Supabase PostgreSQL; the catalogue is readable
without signing in, while management and circulation actions remain protected.
The cloud deployment has not been created yet and needs the team's Render and
Supabase accounts. Follow [`docs/deployment-render-supabase.md`](docs/deployment-render-supabase.md)
to connect the repository and enter credentials securely in Render. Do not put
database passwords or the bootstrap administrator password in Git or chat.

Run tests with:

```powershell
mvn test
```

Week 6 use cases, test cases and current verification results are listed in
[`docs/use-cases-week6.md`](docs/use-cases-week6.md),
[`docs/test-cases-week6.md`](docs/test-cases-week6.md) and
[`docs/week6-status.md`](docs/week6-status.md). Automated tests passed; manual
MySQL and browser checks remain pending.

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
docs/                             Week 1–6 evidence and design artefacts
```

The development order is recorded in [`docs/weekly-progress.md`](docs/weekly-progress.md),
from the Week 1 scope decision through the Week 6 circulation iteration.

## Current limitations

This is a checkpoint, not the final system. Automated tests for the current Week 6 implementation passed, but manual MySQL/browser checks and restart-persistence checks remain outstanding. Email-based sign-in, overdue indicators, reservation processing, fine settlement, member CRUD, pagination, audit trail and browser compatibility evidence are not complete. See [`docs/week6-status.md`](docs/week6-status.md) and [`docs/journal.md`](docs/journal.md).
