# Week 5 use cases

This file records the first catalogue checkpoint and how its account-access
case was extended later. At the `v0.1` checkpoint on 12 October 2026, `UC-01`
covered sign-in and sign-out for existing seeded accounts. Public Member
registration was added in a later iteration; it was not part of the Week 5
implementation.

## UC-01 Access a library account

- **Actors:** Prospective Member, Library Member, Librarian
- **Precondition:** A prospective member is not signed in; a returning user
  has an enabled account.
- **Main flow:** A prospective member can register with a display name,
  username, email and password; the system creates a `MEMBER` account. An
  existing user signs in with a username or email and password. A signed-in
  user can sign out to end the session.
- **Alternative flow:** Invalid or duplicate registration data is rejected
  with field feedback; public registration cannot create a Librarian account.
  Invalid credentials or a disabled account do not create an authenticated
  session.
- **Postcondition:** The user has an authenticated session, or has ended the
  session by signing out.
- **Related requirements:** FR-01, FR-02 and FR-06. Registration was added
  after the Week 5 `v0.1` checkpoint.

## UC-02 Manage books and copies

- **Actor:** Librarian
- **Precondition:** The librarian is authenticated.
- **Main flow:** The librarian opens Add book, enters title/ISBN/author/category,
  saves the record, opens the detail page and adds a physical copy with a
  unique copy code.
- **Alternative flow:** Missing or over-length input is rejected with a field
  message. A duplicate ISBN or copy code is rejected without a partial save.
- **Related requirements:** FR-03, FR-04 and FR-05.

## UC-03 Search books and check availability

- **Actors:** Librarian, Library Member
- **Precondition:** None for catalogue search; authentication is required to
  submit a borrowing request or reservation.
- **Main flow:** The user enters a title, author, ISBN or category; the system
  returns active matching titles; the user opens a title and sees its copy
  status and available-copy count.
- **Alternative flow:** A no-result search shows an empty state without an
  application error. Deactivated titles are not returned in the normal search.
- **Related requirements:** FR-07 and FR-08.

## Scope boundary

At the Week 5 checkpoint, borrowing requests, loan creation, returns, fines,
reservations and member history were outside the implemented slice. Those
workflows were added in later iterations and are recorded in the Week 6–8
use-case files and the current use-case diagram.
