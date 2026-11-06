# Local run and Git handoff

## 1. Prerequisites

- JDK 17 or later
- Maven 3.9 or later
- MySQL 8 or later for a direct local run, or Docker Desktop with Compose

Check the Java and Maven installations:

```powershell
java -version
mvn -version
```

## 2. Create the local database

Run this once in MySQL:

```sql
CREATE DATABASE elibrary CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

The defaults use `root` with an empty password. If that is not your local
setup, set the connection values in PowerShell before starting the app:

```powershell
$env:DB_URL = "jdbc:mysql://localhost:3306/elibrary?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true"
$env:DB_USERNAME = "root"
$env:DB_PASSWORD = "your-local-password"
```

Alternatively, run the application and MySQL together with Docker Compose:

```powershell
Copy-Item .env.example .env
docker compose up --build
```

Open `http://localhost:8080`. Stop the containers with `docker compose down`;
the named MySQL volume remains and keeps records for the next start. Use
`docker compose down --volumes` only when you intend to delete that local
database. The example passwords are for local coursework only. Docker Compose
could not be runtime-verified in the current environment because its Docker
daemon is unavailable; continue with the direct MySQL or H2 instructions below
when Docker Desktop is not running.

Do not put a real password in a committed file. The demo accounts are seeded
for local coursework only: `admin` / `admin123` (Librarian) and `member` /
`member123` (Member).

## 3. Test and run

From the repository folder:

```powershell
mvn test
mvn spring-boot:run
```

If Maven reports that `com.elibrary` packages cannot be found while compiling
tests from this Vietnamese-named Windows folder, try a temporary drive mapping:

```powershell
$repoPath = (Get-Location).Path
subst Z: $repoPath
Set-Location Z:\
mvn test
```

Open `http://localhost:8080`. The local app uses the configured MySQL database.
For a public hosted version, follow
[`docs/deployment-render-supabase.md`](docs/deployment-render-supabase.md).

### Run locally without MySQL

For a local demo when MySQL is unavailable, use the included file-based H2
profile instead of the in-memory H2 database used by tests:

```powershell
mvn spring-boot:run "-Dspring-boot.run.profiles=h2"
```

This stores application data in `data/elibrary.mv.db` in the repository. Stop
the app normally and start it again with the same profile to check persistence.
The `data/` folder is ignored by Git, so its local database is not pushed. Use
MySQL for the standard course setup and record which database was used during
manual checks; the H2 profile is only a local fallback.

## 4. Week 5–9 smoke checks

1. Sign in as `admin`, search for a title, open its details and add a copy.
2. Sign in as `member`; confirm catalogue access works but librarian book and
   copy operations are denied.
3. As the member, submit a borrowing request and open **My activity**.
4. As the librarian, approve the request; check the loan due date and copy
   status, then record the return.
5. Repeat with a late return and check the fine. Try to return it a second
   time; the saved return should not change.
6. Restart the app and confirm the records remain in MySQL.
7. Register two members, set one to Student and one to Standard under
   **Members**, and confirm the librarian page cannot be opened as a member.
8. Make a title unavailable, join its reservation queue as both members, then
   return a copy. Confirm the earlier reservation becomes ready and can be
   claimed. Check cancellation and the 48-hour expiry in a disposable local
   database.
9. Set `LIBRARY_HOLIDAYS` to a date between the due date and return date and
   confirm that date is excluded from the fine. Confirm the fine stops at
   VND 100,000.
10. Register a member with a unique email, then sign in with that email and
    with the exact-case username. Try a username with different letter case.
11. As the librarian, filter loan history by member/title/status and record a
    payment and a waiver with notes. Confirm the settlement history and balance.
12. Verify a returned copy follows reservation order and an expired 48-hour
    hold is released to the next eligible member. The background check runs
    every minute; use a disposable local database for expiry checks.
13. Try a non-numeric catalogue page and an unknown book ID; each should show
    a recovery page rather than a stack trace.
14. As the librarian, open **Circulation report** and compare the most-borrowed
    titles and counts with loans recorded in the test database.

Record the observed outcome and date in the Week 5–9 test tables. Automated
integration tests do not count as manual browser acceptance; leave those rows
pending until the team runs them.

For fine calculation, `LIBRARY_HOLIDAYS` is optional and accepts comma-
separated dates in `yyyy-MM-dd` format. Membership limits are Standard (5
active loans, 14 days), Student (3, 21 days), and Premium (8, 21 days).

## 5. Current Git handoff

The current local branch is `feature/week-7-reservations`; it contains Week 6–8
work plus later verification updates. The working tree is uncommitted and has
not been pushed or deployed. Local tags `v0.1` and `v0.2` point to the Week 5
and Week 7 commits; `v1.0` awaits final acceptance and team review.
Review the working tree and test results before a release commit. The team's
handoff says Ngan3112 performs GitHub pushes; each student should commit their
own work under their own identity to preserve course contribution evidence.
Use a reviewed pull request to merge a feature branch into `main`.

Do not include `.tools/`, `target/`, IDE state, database dumps or credentials.
A push does not itself deploy the service; check Render's watched branch and
deployment result separately.
