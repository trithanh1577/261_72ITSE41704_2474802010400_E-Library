# Screen wireframes and flow notes

These low-fidelity wireframes began in Week 4 and were updated to match the
implemented screens. They describe the current Thymeleaf page structure; they
are not screenshots or browser-acceptance evidence.

## Sign in

```text
+----------------------------------------+
| E-Library                              |
| Username or email [                  ] |
| Password          [                  ] |
|                    [ Sign in ]         |
| New to E-Library? Create a member account|
+----------------------------------------+
```

The login page does not display demo credentials. Successful registration and
sign-out messages appear above the form; invalid credentials show a short
error message.

## Member registration

```text
+----------------------------------------+
| Create a member account                 |
| Display name       [                  ] |
| Username           [                  ] |
| Email              [                  ] |
| Password           [                  ] |
| Confirm password   [                  ] |
|                    [ Create account ]   |
| Back to sign in                         |
+----------------------------------------+
```

The form creates a Member account only. Field errors and duplicate-identifier
messages are shown on the form.

## Catalogue

```text
+--------------------------------------------------+
| E-Library   [role]                     Sign out  |
| CATALOGUE                                        |
| Find a book                                      |
| [ title / author / ISBN / category ] [Search]   |
| [title, author, category, available copies]      |
| [title, author, category, available copies]      |
|                    [Previous] [Next]             |
+--------------------------------------------------+
```

The search term and page are retained while moving through results. An empty
state is shown when no active titles match.

## Book details and copies

```text
+--------------------------------------------------+
| Title / author / ISBN / category                 |
| Description                                      |
| Available copies: N                              |
| [Request to borrow]   [Join reservation queue]   |
| PHYSICAL COPIES                   [Add copy]     |
| Copy code                 Status        [Edit]   |
+--------------------------------------------------+
```

Member actions depend on availability and their account state. Copy-management
controls are shown to Librarians.

## My activity

```text
+--------------------------------------------------+
| MEMBER — My activity                             |
| Reservations: title | placed | status | collect by|
|               [Borrow reserved copy] [Cancel]    |
| Borrowing requests: title | requested | status   |
| Loans: title | copy | due | status | returned | fine|
+--------------------------------------------------+
```

Only the signed-in member's records are shown. A ready reservation can be
claimed before its hold expires; waiting or ready reservations can be cancelled.

## Librarian member management

```text
+--------------------------------------------------+
| LIBRARIAN — Member accounts                      |
| Username | Display name [      ]                 |
|          | Membership [Standard / Student / ...] |
|          | [x] Account enabled       [Save]      |
+--------------------------------------------------+
```

The page edits member display name, membership type and enabled status. It does
not provide a control for changing an account's role.

## Circulation and reports

```text
+--------------------------------------------------+
| LIBRARIAN — Circulation                          |
| [member / title / ISBN / copy] [status] [Search] |
| Member | Title | Copy | Due | Status | Fine      |
|                                  [Return] [Settle]|
| Most borrowed titles: title | author | loan count|
+--------------------------------------------------+
```

The request and reservation pages provide their respective review and queue
actions. The loan report supports member/title/status search, return recording,
fine payment or waiver with a note, and a five-title popularity summary.

Validation messages, empty states, access-denied pages and recovery pages are
part of the implemented interface. Chrome/Edge walkthroughs remain pending;
these text wireframes do not claim browser acceptance.
