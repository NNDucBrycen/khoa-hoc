# 00-safety.md — Absolute Prohibitions + Minimum Required Behaviour

## NEVER do these (hard stops — no exceptions)

| #   | Prohibition                                                       | Examples                                                                                    |
| --- | ----------------------------------------------------------------- | ------------------------------------------------------------------------------------------- |
| S-1 | Read, print, log, or suggest edits to secrets                     | `.env`, `*.pem`, `*.key`, `*.p12`, `*.jks`, `id_rsa`, `serviceAccountKey.json`              |
| S-2 | Execute or propose destructive commands                           | `rm -rf`, `git reset --hard`, `git push --force`, `DROP TABLE`, `DELETE FROM` without WHERE |
| S-3 | Commit or push on behalf of the user without explicit instruction | `git commit`, `git push` unless explicitly asked                                            |
| S-4 | Implement anything outside the approved spec-pack scope           | Spec must be approved first; unknowns → Open Issues                                         |
| S-5 | Modify application source code during Phase 0                     | config/docs only in Phase 0                                                                 |
| S-6 | Leak secrets into logs, diffs, or any output                      | Even masked versions should not appear                                                      |

## ALWAYS do these (minimum required behaviour)

| #   | Rule                                                                                             | Rationale                                 |
| --- | ------------------------------------------------------------------------------------------------ | ----------------------------------------- |
| R-1 | Output a **Plan** (files to read / files to create-update / checkpoints) before editing anything | User must approve before any file changes |
| R-2 | Every output includes **file path + content** as a pair                                          | Nothing implicit; human can verify        |
| R-3 | All per-ticket deliverables go under `docs/changes/{{TICKET}}/`                                  | Single audit trail per ticket             |
| R-4 | Shared living docs go under `docs/architecture/` or `docs/standards/`                            | Avoid scattered duplicates                |
| R-5 | Unclear requirements → Open Issues in spec-pack; **do not guess**                                | Guessing causes regressions               |
| R-6 | Keep changes **small and reviewable** (one logical unit per diff)                                | Huge diffs lose reviewers                 |
| R-7 | After editing, state "what changed", "impact", and "next verification step"                      | Traceability                              |

## Escalate to human when

- A spec conflict exists between two authoritative documents.
- A required change would touch a file matching the deny list.
- The implementation step would exceed the agreed timebox.
- A test failure cannot be diagnosed within 2 attempts.
