# Sample catalogue data

The non-production seed catalogue contains 50 titles and 51 physical copies. Three titles and four copies are written directly in `DataInitializer`; 47 additional bibliographic records (title, author and ISBN-13) are adapted from the public list below. The records are included as demonstration catalogue data and are not claims that the library owns these editions.

- Source: [Recommended Books](https://github.com/cliffordthompson/recommended-books), by Clifford Thompson.
- License: [Creative Commons Attribution 4.0 International (CC BY 4.0)](https://github.com/cliffordthompson/recommended-books/blob/master/LICENSE).
- Adaptation: records are represented as Java seed entries, categorized for the E-Library demo and paired with generated copy codes. The project does not include the source's book descriptions or cover images.

`DataInitializer` is disabled when the `prod` profile is active. It seeds books only while the local catalogue is empty; an existing local database with books is left as-is. `DataInitializerTest` checks that a fresh seed contains unique titles, ISBNs and copy codes.
