# Track Specification: Track 01 — Project Foundation, Glassmorphic Shell & ATDD HTML Living Docs Harness

## 1. Overview & Small-Scope Goal
Establish the foundational project skeleton for both `backend/` (Java 21 + Spring Boot 3 + PostgreSQL + Flyway + Docker Testcontainers) and `frontend/` (Vite + React 18 + TypeScript + Glassmorphic Tailwind CSS shell), along with the **Specification by Example (ATDD) HTML Living Documentation harness** (`Cucumber-JVM + Masterthought/Serenity HTML` on backend and `Vitest/Playwright BDD HTML` on frontend).

## 2. Specification by Example (ATDD) Scenarios
- **Feature:** `platform_foundation_and_living_docs.feature`
  - **Scenario:** Platform health check & core database schema readiness
    - *Given* the PostgreSQL database is migrated via Flyway (`V1__init_schema.sql`, `V2__seed_local_data.sql`)
    - *When* an operator queries `GET /api/v1/system/status`
    - *Then* the response status is `200 OK` with `databaseStatus = "UP"`, `schemaVersion = "2"`, and `uiTheme = "GLASSMORPHIC"`
  - **Scenario:** Glassmorphic workspace shell renders with navigation and living-docs link
    - *Given* a user opens the Teamliness SPA
    - *Then* the Glassmorphic header (`backdrop-blur`), ambient mesh gradient canvas, and navigation bar render cleanly

## 3. Acceptance Criteria & Living Documentation Output
1. Running `cd backend && ./gradlew test` executes the Gherkin `.feature` specification against Testcontainers PostgreSQL and generates a readable HTML Living Documentation report at `backend/build/reports/living-docs/index.html`.
2. Running `cd frontend && npx vitest run` executes the frontend BDD/component suite and generates an HTML Living Documentation report at `frontend/reports/living-docs/index.html`.
3. Two-Stage Human-in-the-Loop (HITL) gates completed: (Gate 1) Gherkin scenarios approved before coding; (Gate 2) HTML Living Documentation report and Glassmorphic shell inspected and signed off.
