# E-Library and Resource Circulation System

## Project Information

- **Course:** 72ITSE41704 - Application Programming Project
- **Class group:** 261_72ITSE41704_01
- **Project:** E-Library and Resource Circulation System
- **Repository:** 261_72ITSE41704_2474802010400_E-Library
- **Development environment:** Visual Studio Code
- **Status:** In development

## Project Overview

The E-Library and Resource Circulation System is designed to help a small library manage books and borrowing activities more efficiently.

Librarians can manage books, book copies, members, loans, returns, reservations, and overdue fines. Library members can search for books, check availability, submit borrowing or reservation requests, and view their borrowing status.

The system aims to reduce manual record-keeping errors, enforce borrowing rules, and provide accurate information about library resources.

## User Roles

### Librarian

- Manage books and book copies
- Manage library members
- Process borrowing requests
- Create loans and confirm returns
- Manage reservations
- Monitor overdue loans and fines

### Library Member

- Search for books
- View book details and availability
- Submit borrowing requests
- Reserve unavailable books
- View current loans, borrowing history, and fines

## Planned Use Cases

1. Sign in and sign out
2. Manage books and book copies
3. Manage library members
4. Search for books and check availability
5. Submit and process borrowing requests
6. Create and manage loans
7. Process book returns
8. Create and manage reservations
9. Calculate and manage overdue fines
10. View borrowing history and current status

## Planned Technology Stack

| Technology | Purpose |
|---|---|
| Java | Main programming language |
| Spring Boot | Web application development |
| Spring Data JPA | Database access and persistence |
| Spring Security | Authentication and role-based authorisation |
| Bean Validation | Input validation |
| MySQL | Relational database |
| Thymeleaf | Server-side web-page rendering |
| HTML/CSS and Bootstrap | Web user interface |
| Maven | Dependency management and project build |
| JUnit 5 | Automated testing |
| Git and GitHub | Version control and collaboration |
| Visual Studio Code | Development environment |

## Planned Architecture

The application will follow a layered architecture:

- **Presentation layer:** Controllers and Thymeleaf pages
- **Business layer:** Services and business rules
- **Data-access layer:** Spring Data JPA repositories
- **Persistence layer:** MySQL database

Business rules will be implemented in the service layer, not in controllers or user-interface code.

## Planned Domain Classes

The initial domain model includes:

- `UserAccount`
- `Librarian`
- `Member`
- `Book`
- `BookCopy`
- `Author`
- `Category`
- `Loan`
- `Reservation`
- `Fine`

## Planned Business Rules

- A book copy cannot be borrowed while it is already on loan.
- A member cannot exceed the permitted borrowing limit.
- A member with an unresolved overdue loan may be prevented from borrowing.
- Overdue fines are calculated according to the number of late days.
- An unavailable book can be reserved.
- Reservations are processed in a defined order.

## Quality Requirements

- Users will have different permissions according to their roles.
- Application data will be stored in MySQL and will remain available after restart.
- Every user input will be validated.
- Invalid input will display a clear message without crashing the application.
- Database-access and input-processing failures will be handled appropriately.
- The application will provide a consistent web graphical user interface.
- At least 10 test cases will be documented with input, expected result, actual result, and pass/fail status.
- Important business rules will be covered by automated unit tests.

## Team Members

| Student ID | Full Name | Planned Role |
|---|---|---|
| 2474802010400 | Nguyễn Thành Trí | Team Leader, Backend and Database Developer |
| 2474802016786 | Phạm Thị Khánh Ngân | Frontend and Feature Developer |

Each member will commit their own work using their own GitHub account.

## Repository Structure

```text
.
├── docs/
├── src/
│   ├── main/
│   │   ├── java/
│   │   └── resources/
│   └── test/
│       └── java/
├── tests/
├── .gitignore
├── pom.xml
└── README.md
```
