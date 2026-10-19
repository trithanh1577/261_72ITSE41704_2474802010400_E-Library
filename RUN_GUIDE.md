# Local run and Git handoff

## 1. Prerequisites

- JDK 17 or later
- Maven 3.9 or later
- MySQL 8 or later

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

Open `http://localhost:8080`. The app uses the configured MySQL database; it
does not host a public website.

## 4. Week 5/6 smoke checks

1. Sign in as `admin`, search for a title, open its details and add a copy.
2. Sign in as `member`; confirm catalogue access works but librarian book and
   copy operations are denied.
3. As the member, submit a borrowing request and open **My activity**.
4. As the librarian, approve the request; check the loan due date and copy
   status, then record the return.
5. Repeat with a late return and check the fine. Try to return it a second
   time; the saved return should not change.
6. Restart the app and confirm the records remain in MySQL.

Record the observed outcome and date in the Week 5/6 test tables. The manual
checks are not marked passed until they have been run on the team's setup.

## 5. Git handoff for Ngan3112

Week 6 is on the local branch `feature/week-6-circulation`, based on the Week 5
branch. Review the changes and test results before committing. Use Ngan's own
GitHub credentials and configured Git identity for the commit and push:

```powershell
git switch feature/week-6-circulation
git status
git diff --check
git add -A
git status
git commit -m "Implement Week 6 circulation workflow"
git push -u origin feature/week-6-circulation
```

The push uploads a branch; it does not publish or host the web application.
Do not include `.tools/`, `target/`, IDE state, database dumps or credentials.
