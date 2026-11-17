# Implementation Plan: Teamliness MVP (`teamliness_mvp_20261117`)

## Phase 1: Backend Foundation, Database Schema & Workday Hexagonal Port
- [ ] Task: Initialize Gradle Spring Boot 3 (Java 21) project, Docker Compose PostgreSQL, and Flyway schema migrations
  - [ ] Create `docker-compose.yml` for local PostgreSQL 16 and `backend/` Gradle wrapper (`build.gradle`, `settings.gradle`) with Spring Boot 3, Flyway, Spring Data JPA/JDBC, Spring Security, and Docker Testcontainers (`org.testcontainers:postgresql`)
  - [ ] Write failing Testcontainers database migration integration test verifying schema creation (`V1__init_schema.sql`) and seed fixtures (`V2__seed_local_data.sql`)
  - [ ] Implement `V1__init_schema.sql` (tables for `org_units`, `persons`, `skills`, `team_roles`, `person_skills`, `teams`, `team_memberships`, `outcomes`, `key_results`, `outcome_teams`, `outcome_contributors`) and `V2__seed_local_data.sql`
  - [ ] Run `./gradlew test` in single-run mode and verify migration tests pass
- [ ] Task: Implement `WorkdayDirectoryPort`, `MockWorkdayAdapter`, `WorkdayRaaSAdapter`, and `WorkdaySyncService` (TDD)
  - [ ] Write failing unit and Testcontainers integration tests for `MockWorkdayAdapter`, `WorkdayRaaSAdapter` (via `MockRestServiceServer` testing `{"Report_Entry": [...]}` parsing and auth headers), idempotent employee upserts, and soft-deactivation (`INACTIVE`) of omitted/terminated Workday leavers
  - [ ] Implement `WorkdayDirectoryPort`, `MockWorkdayAdapter`, `WorkdayRaaSAdapter`, `WorkdaySyncService`, and `POST /api/v1/workday/sync` + `GET /api/v1/persons`
  - [ ] Run `./gradlew test` in single-run mode and confirm all Workday sync and adapter tests pass
- [ ] Task: Phase Verification & Checkpoint (Refer to workflow.md)

## Phase 2: Core Domain — RBAC Personas, Skill/Role Catalog, Teams & Capacity Allocation
- [ ] Task: Implement RBAC Persona Security Filter and Skill & Role Catalog with Direct Validated Skill Assignment (TDD)
  - [ ] Write failing tests for header-based RBAC (`X-Persona-Role`: `ADMIN`, `ORG_LEADER`, `TEAM_LEAD`, `READ_ONLY_VIEWER` and `X-Person-Id`), catalog CRUD (`/api/v1/catalog/roles`, `/api/v1/catalog/skills`), and direct validated skill assignment (`POST /api/v1/persons/{personId}/skills`) enforcing `TEAM_LEAD` team-member scoping
  - [ ] Implement RBAC security filter, Role & Skill catalog controllers/services, and direct validated skill assignment logic
  - [ ] Run `./gradlew test` in single-run mode and verify RBAC and skill catalog tests pass
- [ ] Task: Implement Hierarchical Org Units, Team Composition, `>100%` Soft Over-Allocation Warning, and Leaver Offboarding (TDD)
  - [ ] Write failing unit and integration tests for Org Unit tree queries, Team creation/updates (`TEAM_LEAD` ownership check), `>100%` overlapping active membership soft-warning calculation (`isOverAllocated = true` + breakdown), `INACTIVE` Workday leaver flagging on memberships, and one-click membership offboarding/reassignment (`POST /api/v1/teams/{teamId}/memberships/{membershipId}/offboard`)
  - [ ] Implement Org Unit and Team domain services, capacity allocation calculator, leaver offboarding endpoint, and REST controllers (`/api/v1/org-units`, `/api/v1/teams`)
  - [ ] Run `./gradlew test` in single-run mode and verify all team and capacity domain tests pass
- [ ] Task: Implement Outcome Commitments & Jira Canonical Team Directory API (TDD)
  - [ ] Write failing integration tests for Outcome & Key Result management (`/api/v1/outcomes`), explicit team and contributor mappings (including leaver warning flags and Jira Epic/JQL links), and the read-only Jira Team Directory API (`GET /api/v1/integrations/jira/teams?jiraProjectKey=...&orgUnitId=...`)
  - [ ] Implement Outcome domain service/controller and `JiraIntegrationController` exposing canonical active teams, hierarchy paths, leads, members, and Jira Project Key mappings
  - [ ] Run `./gradlew test jacocoTestReport` in single-run mode and confirm >80% backend coverage
- [ ] Task: Phase Verification & Checkpoint (Refer to workflow.md)

## Phase 3: React SPA Frontend — High-Density Operational Views & End-to-End Integration
- [ ] Task: Scaffold Vite + React 18 + TypeScript SPA, API Client, and Header Persona Switcher (TDD)
  - [ ] Initialize `frontend/` with Vite, React 18, TypeScript, Tailwind CSS, TanStack Query/Table, React Router, and Vitest + React Testing Library
  - [ ] Write failing component tests (`npx vitest run`) for the Workspace Shell, RBAC Persona Switcher (`Admin`, `Org Leader`, `Team Lead`, `Read-Only Viewer`), and Admin on-demand Workday Sync trigger
  - [ ] Implement API client (propagating `X-Persona-Role` and `X-Person-Id` headers), Workspace Layout, and Persona Switcher
  - [ ] Run `npx vitest run` and confirm shell and persona switcher tests pass
- [ ] Task: Implement Team Composition View & Skill Matrix View (TDD)
  - [ ] Write failing component tests (`npx vitest run`) for the split-pane Org Unit explorer + Team Composition table (verifying Amber `>100%` over-allocation badge with breakdown tooltip, Red/Rose `Workday Inactive / Leaver` badge with one-click Offboard/Reassign action, Read-Only Workday lock indicators, and Jira Project links) and the Skill Matrix heatmap grid (with direct skill proficiency assignment)
  - [ ] Implement Team Composition View, Membership & Leaver Offboarding drawers/actions, and Skill Matrix Heatmap View
  - [ ] Run `npx vitest run` and verify all Team Composition and Skill Matrix tests pass
- [ ] Task: Implement Outcome Commitments View & Jira Directory Preview (TDD)
  - [ ] Write failing component tests (`npx vitest run`) for the Outcome Commitments board/table view (Key Results progress, committed Teams, Contributors with leaver badges, Jira Epic/JQL deep-links, and Jira Team Directory payload inspector)
  - [ ] Implement Outcome Commitments View, Outcome creation/editing drawer, and Jira Team Directory integration view
  - [ ] Run `npx vitest run --coverage` and `npm run build` in single-run mode to verify >80% frontend coverage and clean production build
- [ ] Task: Phase Verification & Checkpoint (Refer to workflow.md)
