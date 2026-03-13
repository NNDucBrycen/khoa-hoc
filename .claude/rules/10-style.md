# 10-style.md — Naming, Structure & Format Rules

## Backend (Java)

| Rule | Detail |
|---|---|
| **ST-B1** | Class names: PascalCase. Method names: camelCase. Constants: UPPER_SNAKE_CASE. |
| **ST-B2** | Package layout: `controller/`, `service/`, `repository/`, `entity/`, `dto/`, `config/`, `exception/` under `com.example.demo`. |
| **ST-B3** | DTOs use Java records where possible. Suffix: `Request`, `Response`. |
| **ST-B4** | Spring Boot managed dependency versions — omit version in `build.gradle` unless unavoidable. |
| **ST-B5** | One class per file. No utility classes with only static methods unless genuinely stateless helpers. |

**Evidence**: [demo/build.gradle:22-26](../../demo/build.gradle#L22) · [demo/src/main/java/com/example/demo/DemoApplication.java:1](../../demo/src/main/java/com/example/demo/DemoApplication.java#L1)

---

## Frontend (TypeScript · React)

| Rule | Detail |
|---|---|
| **ST-F1** | Component files: `PascalCase.tsx`. Hook files: `camelCase.ts`. API files: `camelCase.ts`. |
| **ST-F2** | Prefer named exports. Default export only for top-level page/route components. |
| **ST-F3** | TypeScript strict mode must remain enabled — no `@ts-ignore` or `any` without a documented reason. |
| **ST-F4** | Remove all unused imports and variables before committing (`noUnusedLocals` enforced). |
| **ST-F5** | `npm run lint` must pass with zero errors before a PR is submitted. |

**Evidence**: [my-react-app/tsconfig.app.json:20-25](../../my-react-app/tsconfig.app.json#L20) · [my-react-app/eslint.config.js:11-16](../../my-react-app/eslint.config.js#L11) · [my-react-app/package.json:9](../../my-react-app/package.json#L9)
