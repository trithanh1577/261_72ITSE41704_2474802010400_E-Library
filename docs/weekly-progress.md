# Weekly progress log

This note records how the project was developed from the first week through
the Week 6 checkpoint. It is a progress record, not a claim that the whole
ten-week proposal has already been completed.

## Week 1 - Start with the topic and scope

### Work done

- Read the course guidelines, self-study materials and suggested project
  titles.
- Chose Topic 6: E-Library and Resource Circulation System.
- Confirmed the two user roles: Librarian and Library Member.
- Agreed on the initial team responsibilities and created the project
  baseline.
- Listed the first catalogue functions: sign in, search books, view book
  details and manage physical copies.

### Decision at this point

The first version should focus on the catalogue before adding loans,
reservations and fines. Those features are kept in the proposal as later
work, rather than being mixed into the first checkpoint.

### Difficulty / note

The topic description included many modules, so it was necessary to separate
the initial catalogue scope from the later circulation scope.

## Week 2 - Turn the topic into requirements

### Work done

- Wrote the FR-01 to FR-18 requirement list.
- Added priorities, sources and planned test IDs.
- Added the measurable non-functional requirements.
- Marked borrowing, returns, fines, reservations and member history as later
  work instead of claiming them in the Week 5 implementation.

### Difficulty / note

Some items in the original topic were module names rather than testable
behaviours. They had to be rewritten as smaller requirements before they
could be tested.

See [`requirements-week2.md`](requirements-week2.md).

## Week 3 - Plan the work

### Work done

- Broke the proposal into a ten-week plan.
- Assigned backend/database and interface/test responsibilities between the
  two members.
- Defined the Week 5 boundary as the first working catalogue slice.
- Prepared the initial test-case list, with manual results left pending until
  the application could be run with MySQL.

### Difficulty / note

The plan had to keep the final proposal broad while making the Week 5 target
small enough to finish and explain.

## Week 4 - Design before adding more features

### Work done

- Prepared the layered architecture and package responsibilities.
- Drew the initial use-case, class and ER diagrams.
- Wrote the catalogue search sequence and low-fidelity screen notes.
- Chose to keep loan and reservation tables out of the first database design.

### Difficulty / note

Adding all future circulation entities too early would make the first version
harder to test. The design therefore shows the Week 5 catalogue boundary and
leaves the later entities for the weeks where they are implemented.

See the files in [`design/`](design/).

## Week 5 - First implementation checkpoint

### Work done

- Implemented login/logout and Librarian/Member access rules.
- Implemented active-book search, book create/edit/deactivate and physical
  copy management.
- Added MySQL persistence configuration, validation and local seed data.
- Added unit and repository tests for the current slice.

### Current result

The Maven test run passed 3 automated tests. Browser checks and MySQL restart
checks are still marked pending because they require a configured local
database and an actual application run.

### Remaining work

Borrowing requests, loans, returns, fines, reservations, member management,
pagination and performance measurement are not part of this checkpoint. They
remain in the later work plan.

The detailed handoff is in [`week5-status.md`](week5-status.md), and the
working notes are in [`journal.md`](journal.md).

## Week 6 - Circulation iteration (manual checks pending)

- Started member borrowing requests and librarian review.
- Added loan issue/return flow with the proposal's 14-day loan period, five
  active-loan limit, overdue eligibility check and VND 5,000 daily fine rule.
- Added service tests and a separate Week 6 test-case table. After final review,
  `mvn test` passed 17 tests with no failures or errors; manual MySQL/browser
  cases remain pending. A review found that the book-save POST route needed an
  explicit Librarian rule; this was corrected in `SecurityConfig`. Manual copy
  editing was also restricted from setting the not-yet-implemented
  `RESERVED` state.
- Added Week 6 use cases and updated the requirement traceability notes.
- This work is on local branch `feature/week-6-circulation`; it has not been
  reviewed, committed or pushed yet.
