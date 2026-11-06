# Week 8 use cases

## UC-12 View title popularity report

- **Actor:** Librarian
- **Precondition:** The librarian is signed in; one or more loans have been recorded.
- **Main flow:** The librarian opens the circulation report. The system groups active and historical loans by book title, orders titles by descending loan count, and displays the five most-borrowed titles with authors and counts.
- **Alternative flow:** If no borrowing history exists, the report shows an empty-state message.
- **Related requirement:** FR-20.

The ranking uses recorded loans; it does not infer demand from searches, pending requests or reservations.
