# E-Library and Resource Circulation System

## Project information

- **Course:** 72ITSE41704 - Application Programming Project
- **Project:** E-Library and Resource Circulation System
- **Repository:** 261_72ITSE41704_2474802010400_E-Library
- **Status:** In development

## Problem description

The E-Library and Resource Circulation System is designed to help a small
library manage books and borrowing activities more efficiently.

The system allows librarians to manage books, members, loans, returns,
reservations, and overdue fines. Library members can search for books,
check availability, submit borrowing or reservation requests, and view
their current borrowing status.

## Target users

### Librarian

The librarian can:

- Manage books and book copies
- Manage library members
- Create and process loans
- Confirm book returns
- Manage reservations
- Calculate and manage overdue fines
- Monitor book availability

### Library member

A library member can:

- Search for books
- View book information
- Check book availability
- Submit borrowing requests
- Reserve unavailable books
- View current and previous loans
- View overdue status and fines

## Planned technology stack

| Technology | Purpose |
|---|---|
| Java | Main programming language for the system logic |
| Spring Boot | Backend development and application configuration |
| Spring Data JPA | Database access and persistence |
| MySQL | Storage for books, users, loans, reservations and fines |
| Thymeleaf | Integration between the Spring Boot backend and web pages |
| HTML/CSS | Web interface structure and styling |
| Bootstrap | Responsive and consistent user-interface components |
| Maven | Dependency management and project build |
| Git and GitHub | Version control and collaborative development |

## Planned architecture

The application will follow a layered architecture:

- **Presentation layer:** Controllers and web pages
- **Business layer:** Services and business rules
- **Data-access layer:** Repositories and database operations
- **Persistence layer:** MySQL database

Business rules will be implemented in the service layer, not directly
inside controllers or user-interface code.

## Core features

1. User authentication and role-based authorization
2. Book and book-copy management
3. Library-member management
4. Book search and availability checking
5. Loan creation and return processing
6. Book reservations
7. Overdue fine calculation
8. Borrowing-history management
9. Input validation and exception handling
10. Testing of important business rules

## Planned business rules

The project plans to implement rules such as:

- A book copy cannot be borrowed when it is already on loan.
- A member cannot borrow more than the permitted number of books.
- A member with unresolved overdue loans may be prevented from borrowing.
- An overdue fine is calculated according to the number of late days.
- An unavailable book can be reserved by a member.
- Reservations are processed in a defined order.

## Team members

| Student ID | Full name | Planned role |
|---|---|---|
| 2474802010400 | Nguyễn Thành Trí | Team Leader, Backend and Database Developer |
| 2474802016786 | Phạm Thị Khánh Ngân | Frontend and Feature Developer |

## Repository structure

```text
.
├── docs/                       # UML, ERD, wireframes and reports
├── src/
│   ├── main/
│   │   ├── java/              # Java source code
│   │   └── resources/         # Templates, static files and configuration
│   └── test/
│       └── java/              # Automated tests
├── .gitignore
├── pom.xml                     # Maven configuration
└── README.md
