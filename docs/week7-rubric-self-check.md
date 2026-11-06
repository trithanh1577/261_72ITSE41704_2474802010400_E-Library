# Week 7 rubric self-check

> Retrospective reconstruction prepared on 6 November 2026 from the local
> `v0.2` checkpoint (commit `01adcf3`, dated 3 November 2026) and the records
> available in the repository. This was not written during Week 7. The scores
> are a draft for both team members to review and change; they must not be
> presented as a contemporaneous assessment.

| Rubric criterion | Suggested score at the Week 7 checkpoint (1–10) | Evidence and remaining gap | Action planned for Weeks 8–9 |
|---|---:|---|---|
| Requirements analysis & specifications | 8 | The proposal and requirements baseline defined two roles, 18 functional requirements, measurable non-functional requirements, business rules and 11 distinct use cases by the Week 7 checkpoint. Browser acceptance and the performance target had not yet been measured. | Add the circulation popularity report from Topic 6, keep requirement-to-test links current, and check each main flow with realistic valid and invalid inputs. |
| Collaborative development & version control | 5 | The repository has Week 5 and Week 7 checkpoints and commits from both members. The history available on 6 November shows commits by Trí in 3 distinct ISO weeks and Ngân in 2, below the course target of at least 5 weeks per member. Several later changes are still uncommitted. | Continue making genuine, attributable work in the remaining course weeks; use reviewed pull requests and preserve each contributor's actual authorship. Do not create retrospective commits to fill missing weeks. |
| System design & user interface | 7 | Layered architecture, use-case/class/ER diagrams, sequence diagrams and wireframe notes exist. The reservation, member-management and report additions still need a consistency review against the implemented pages and database. | Update diagrams after feature freeze, then walk through the member and librarian screens in a browser and record layout or navigation issues. |
| Implementation & testing | 7 | The Week 7 checkpoint recorded 34 automated tests and the main request, loan, return and reservation flows. Tests used mocks/H2; MySQL, browser, production persistence and the 10,000-title performance target were not yet accepted. | Run the current full suite, complete the browser checklist against MySQL, verify restart persistence, and record actual results and defects without substituting automated results for manual checks. |
| Report writing quality & documentation | 6 | A proposal, README, run guide, weekly notes, requirements, test tables and design documents exist. The Week 7 self-check itself was missing, manual test observations were still pending, and the final report belongs to a later course deliverable. | Keep the technical run guide and test evidence current, proofread the user-facing documentation, and keep the final course report separate from the application repository work. |

## Review note

The team should discuss these suggested scores and actions before treating this
file as its self-check. The current browser and database acceptance results are
recorded separately in [`week9-acceptance.md`](week9-acceptance.md). The final
course report is a separate deliverable.
