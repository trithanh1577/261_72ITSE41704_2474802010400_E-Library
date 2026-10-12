# Week 4 wireframe notes

These low-fidelity notes were prepared before the first implementation slice. They describe task-oriented screens rather than one screen per database table.

## Sign in

```text
+--------------------------------+
| E-Library                      |
| Username [                  ]  |
| Password [                  ]  |
|              [ Sign in ]       |
| Demo account hint              |
+--------------------------------+
```

## Catalogue

```text
+--------------------------------------------------+
| E-Library     [role]                    Sign out  |
| CATALOGUE                                        |
| Find a book                                      |
| [ search title/author/ISBN/category ] [Search]   |
| [book card] [book card] [book card]              |
+--------------------------------------------------+
```

## Book details / copies

```text
+--------------------------------------------------+
| [cover]  title / author / ISBN                   |
|          available copies                        |
| PHYSICAL COPIES                    [Add copy]    |
| Copy code                 Status       Edit      |
| CC-001                    AVAILABLE             |
+--------------------------------------------------+
```

The implementation adds validation messages, an empty search state, mobile layout and a clear access-denied page.
