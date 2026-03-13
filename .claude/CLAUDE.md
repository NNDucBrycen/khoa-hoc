# CLAUDE.md — Project Constitution

## Repo overview

| Sub-project     | Stack                                   | Root            |
| --------------- | --------------------------------------- | --------------- |
| `demo/`         | Spring Boot 3.5.x · Java 25 · Gradle    | `demo/`         |
| `my-react-app/` | React 19 · TypeScript · Vite 7 · ESLint | `my-react-app/` |

## SDD operating model

- Every ticket's deliverables live under `docs/changes/{{TICKET}}/`
- Shared living docs: `docs/architecture/` and `docs/standards/`
- Permanent AI rules: `.claude/rules/*.md` (auto-loaded)
- **Spec source of truth**: `docs/changes/{{TICKET}}/spec-pack.md`

## Absolute rules (short version)

1. **Plan before edit** — output a Plan; do not touch files until approved.
2. **No secrets** — never read, log, or suggest changes to `.env`, keys, certs, credentials.
3. **No destructive commands** — `rm -rf`, `git reset --hard`, `git push --force`, `DROP TABLE` etc. are banned.
4. **No scope creep** — implement only what spec-pack says; put unclear items in Open Issues.
5. **Small slices** — one reviewable diff at a time.

See `.claude/rules/00-safety.md` for the full list.

## Key commands

```bash
# Backend (demo/)
cd demo && ./gradlew test
cd demo && ./gradlew build

# Frontend (my-react-app/)
cd my-react-app && npm run lint
cd my-react-app && npm run build
```
