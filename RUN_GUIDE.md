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

If PowerShell says `mvn` is not recognized, install Maven on Windows:

1. Download the binary ZIP from the [Apache Maven installation page](https://maven.apache.org/install.html).
2. Extract it to a stable folder outside the project, for example
   `C:\tools\apache-maven-3.9.x`.
3. Add that folder's `bin` directory (for example,
   `C:\tools\apache-maven-3.9.x\bin`) to the Windows user `Path` environment
   variable.
4. Close and reopen VS Code so its terminal reloads `Path`, then run
   `mvn -version` again.

The first Maven build may need an internet connection to download dependencies.
See the official [Windows prerequisites](https://maven.apache.org/guides/getting-started/windows-prerequisites.html)
if Java or Maven is still not recognized.

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
if (-not (Test-Path .env)) { Copy-Item .env.example .env }
docker compose up --build
```

Open `http://localhost:8080`. The application container appears as `e-library`
in Docker Desktop. Stop the containers with `docker compose down`; the named
MySQL volume remains and keeps records for the next start. Use
`docker compose down --volumes` only when you intend to delete that local
database. The example passwords are for local coursework only. Docker Compose
was verified with the application and MySQL services running; see the local
acceptance notes for the persistence restart check.

The root `.dockerignore` excludes local `data/`, `.tools/`, `.env` files,
generated output and IDE files from the image build context. It does not delete
the local H2 database or the Compose MySQL volume.

Do not put a real password in a committed file. The demo accounts are seeded
for local coursework only: `admin` / `admin123` (Librarian) and `member` /
`member123` (Member).

## 3. Test and run

From the repository folder:

```powershell
mvn test
mvn spring-boot:run
```

If Maven fails from a Windows project path containing Vietnamese characters or
spaces—for example, the launcher reports `Access is denied` or cannot find
`org.codehaus.plexus.classworlds.launcher.Launcher`, or the compiler cannot
resolve `com.elibrary` packages—try a temporary drive mapping. Check that `Z:`
is unused first:

```powershell
if (Get-PSDrive Z -ErrorAction SilentlyContinue) { throw 'Z: is already in use' }
$repoPath = (Get-Location).Path
subst Z: $repoPath
try {
    Set-Location Z:\
    mvn test
} finally {
    Set-Location $repoPath
    subst Z: /D
}
```

Run `mvn spring-boot:run` from `Z:\` in the same way if the launcher error
occurs when starting the app. The example above removes the mapping after the
Maven command exits.

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

## 4. Manual application checks

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
15. Use the `EN` / `VI` control from the catalogue, sign-in and registration
    pages. Confirm the visible labels change, the current search/filter query is
    kept, and the chosen language remains active after opening another page.

Record the date, browser, observed outcome and evidence in
[`docs/week9-acceptance.md`](docs/week9-acceptance.md). Use the Week 5–9 test
case tables for the corresponding case IDs. Automated integration tests do not
count as manual browser acceptance; leave a checklist item pending until the
team performs it in a browser.

For fine calculation, `LIBRARY_HOLIDAYS` is optional and accepts comma-
separated dates in `yyyy-MM-dd` format. Membership limits are Standard (5
active loans, 14 days), Student (3, 21 days), and Premium (8, 21 days).

## 5. Git workflow and deployment

1. Create a feature branch for a change and commit the finished work.
2. Push the branch and open a pull request into `main`.
3. Have the other member review it, then merge the pull request.
4. Set the Render service to deploy from `main` and enable automatic deploys.
   After merging, confirm that the deployment reports **Live**.
5. Open the website and check sign-in and the main workflow after deployment.
