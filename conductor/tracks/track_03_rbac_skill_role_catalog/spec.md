# Track Specification: Track 03 — RBAC Personas, Skill & Role Catalog & Direct Validated Skill Tagging

## 1. Overview & Small-Scope Goal
Implement the RBAC security filter and Glassmorphic Persona Switcher (`ADMIN`, `ORG_LEADER`, `TEAM_LEAD`, `READ_ONLY_VIEWER`), the standardized Role & Skill Catalogs (`/api/v1/catalog/roles`, `/api/v1/catalog/skills`), and direct validated skill assignment (`BEGINNER`, `INTERMEDIATE`, `EXPERT`) on employees (`POST /api/v1/persons/{personId}/skills`) with the Individual Skill Matrix UI view.

## 2. Specification by Example (ATDD) Scenarios
- **Feature:** `rbac_and_validated_skill_catalog.feature`
  - **Scenario Outline:** Persona permission matrix for catalog management and direct validated skill assignment
    - *Given* an authenticated persona (`Examples:` table covering `ADMIN`, `ORG_LEADER`, `TEAM_LEAD` for own team vs other team, and `READ_ONLY_VIEWER`)
    - *When* the persona attempts to create a catalog skill or assign a validated skill proficiency (`BEGINNER`, `INTERMEDIATE`, `EXPERT`) to an employee
    - *Then* the system enforces the expected HTTP outcome (`200 OK` with `validatedByPersonId` recorded vs `403 Forbidden`)

## 3. Acceptance Criteria & Living Documentation Output
1. All RBAC and skill assignment Gherkin scenarios pass and render in the HTML Living Documentation report (`backend/build/reports/living-docs/index.html` and `frontend/reports/living-docs/index.html`).
2. Two-Stage HITL gates completed.
