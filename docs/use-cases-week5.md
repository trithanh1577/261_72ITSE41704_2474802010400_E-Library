# Week 5 use cases

The course guidance recommends modelling user goals, not individual buttons. The Week 5 slice implements the following goals end-to-end.

## UC-01 Sign in and sign out

- **Actors:** Librarian, Library Member
- **Precondition:** An enabled account exists.
- **Main flow:** The user enters credentials; Spring Security verifies the password; the user is redirected to the catalogue; the user selects Sign out to end the session.
- **Alternative flow:** Invalid credentials show a clear error and do not create a session. A disabled account is denied access.
- **Related requirements:** FR-01, FR-02. Sign out is part of this use case but is not a separate FR in the approved baseline.

## UC-02 Manage books and copies

- **Actor:** Librarian
- **Precondition:** The librarian is authenticated.
- **Main flow:** The librarian opens Add book, enters title/ISBN/author/category, saves the record, opens the detail page and adds a physical copy with a unique copy code.
- **Alternative flow:** Missing or over-length input is rejected with a field message. A duplicate ISBN or copy code is rejected without a partial save.
- **Related requirements:** FR-03, FR-04.

## UC-03 Search books and check availability

- **Actors:** Librarian, Library Member
- **Precondition:** The user is authenticated.
- **Main flow:** The user enters a title, author, ISBN or category; the system returns active matching titles; the user opens a title and sees its copy status and available-copy count.
- **Alternative flow:** A no-result search shows an empty state without an application error. Deactivated titles are not returned in the normal search.
- **Related requirements:** FR-06, FR-07.

## Scope boundary

Borrowing requests, loan creation, returns, fines, reservations and member history are not claimed as implemented in Week 5. They are planned for Weeks 6–7 and must be added to the use-case diagrams when implemented.
