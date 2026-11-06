# Week 5 use cases

The course guidance recommends modelling user goals, not individual buttons. The Week 5 slice implements the following goals end-to-end.

## UC-01 Register, sign in and sign out

- **Actors:** Librarian, Library Member, Prospective Member
- **Precondition:** An enabled account exists, or a prospective member supplies unused account identifiers.
- **Main flow:** A prospective member registers with display name, username, email and matching passwords; the system creates a `MEMBER` account with a BCrypt password hash. An enabled user signs in using username or email and password, is redirected to the catalogue, and can sign out.
- **Alternative flow:** Invalid registration values, duplicate username/email, mismatched passwords, invalid credentials or a disabled account are rejected. Public registration cannot create a Librarian.
- **Related requirements:** FR-01, FR-02, FR-06.

## UC-02 Manage books and copies

- **Actor:** Librarian
- **Precondition:** The librarian is authenticated.
- **Main flow:** The librarian opens Add book, enters title/ISBN/author/category, saves the record, opens the detail page and adds a physical copy with a unique copy code.
- **Alternative flow:** Missing or over-length input is rejected with a field message. A duplicate ISBN or copy code is rejected without a partial save.
- **Related requirements:** FR-03, FR-04, FR-05.

## UC-03 Search books and check availability

- **Actors:** Librarian, Library Member
- **Precondition:** None for catalogue search; authentication is required to submit a borrowing request or reservation.
- **Main flow:** The user enters a title, author, ISBN or category; the system returns active matching titles; the user opens a title and sees its copy status and available-copy count.
- **Alternative flow:** A no-result search shows an empty state without an application error. Deactivated titles are not returned in the normal search.
- **Related requirements:** FR-07, FR-08.

## Scope boundary

Borrowing requests, loan creation, returns, fines, reservations and member history are not claimed as implemented in Week 5. They are planned for Weeks 6–7 and must be added to the use-case diagrams when implemented.
