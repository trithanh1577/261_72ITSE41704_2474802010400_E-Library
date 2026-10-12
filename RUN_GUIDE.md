# Week 5 run guide

## 1. Install local tools

Install JDK 17+, Maven 3.9+ and MySQL 8+. Confirm:

```powershell
java -version
mvn -version
```

## 2. Create the database

```sql
CREATE DATABASE elibrary CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

## 3. Configure the application

The default connection is `root` with an empty password on `localhost:3306`. For another setup:

```powershell
$env:DB_URL = "jdbc:mysql://localhost:3306/elibrary?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true"
$env:DB_USERNAME = "root"
$env:DB_PASSWORD = "your-password"
```

## 4. Test and run

```powershell
mvn test
mvn spring-boot:run
```

Browse to `http://localhost:8080`. The initial seed accounts are documented in `README.md`. Change them before any non-local use.

## 5. Smoke test

1. Sign in as `admin`.
2. Search for `Java` and open a book.
3. Add a new book and one physical copy.
4. Edit the copy status and confirm the detail page changes.
5. Sign out and sign in as `member`.
6. Confirm that the member can search but cannot open librarian book/copy forms.
7. Restart the application and confirm the new book remains in MySQL.

## 6. Push the project to GitHub when it is ready

The project folder currently does not have a Git repository or a remote
address. Do this part only after checking the files and deciding that the
Week 5 checkpoint is ready to share.

The course guideline requires the repository name to follow
`261_72ITSE41704_<TeamLeaderID>_<ShortTopic>`. The revised proposal uses:

```text
261_72ITSE41704_2474802010400_E-Library
```

### Create the GitHub repository

1. Sign in to GitHub and create a new empty repository.
2. Use the name `261_72ITSE41704_2474802010400_E-Library`.
3. Do not add a second README, `.gitignore` or licence during creation.
4. Add the lecturer as a collaborator, or make the repository public as
   permitted by the course guideline.
5. Copy the repository HTTPS address:

```text
https://github.com/trithanh1577/261_72ITSE41704_2474802010400_E-Library.git
```

### Create the initial main branch

For the Week 1 repository setup, create the first small commit with the
README and ignore rules:

```powershell
git --version
git init
git branch -M main
git add .gitignore README.md
git status
git commit -m "Initialise repository with README and gitignore"
git remote add origin https://github.com/trithanh1577/261_72ITSE41704_2474802010400_E-Library.git
git push -u origin main
```

Before `git commit`, check `git status` and make sure that passwords, local
database files, `target/`, `.tools/` and IDE settings are not included.
The existing `.gitignore` excludes the generated build folders and common
local files. Never commit a real MySQL password.

### Submit the Week 5 implementation through a pull request

Do not put the whole Week 5 implementation directly on `main`. Create a
short-lived feature branch, commit the actual work, push the branch and open a
pull request:

```powershell
git switch -c feature/week-5-catalogue
git add -p
git status
git commit -m "Implement Week 5 catalogue checkpoint"
git push -u origin feature/week-5-catalogue
```

Open a pull request from `feature/week-5-catalogue` into `main`, review it
with the other member, and merge it only after the project builds. Then tag
the merged Week 5 checkpoint:

```powershell
git switch main
git pull
git tag -a v0.1 -m "Week 5: first end-to-end slice"
git push origin v0.1
```

For later weeks, repeat the same branch and pull-request process. Create
`v0.2` at Week 7 and `v1.0` at Week 9. Use the real week and work completed in
each message. Do not create fake old commits to make the history look longer.

Each member must commit their own work using their own GitHub account. Do not
have one person push all contributions under one account.
