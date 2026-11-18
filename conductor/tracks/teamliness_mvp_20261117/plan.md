# Implementation Plan: Teamliness MVP (`teamliness_mvp_20261117`)

## Phase 1: Backend Foundation, Database Schema & Workday Hexagonal Port
- [ ] Task: Initialize Gradle Spring Boot 3 (Java 21) project, Docker Compose PostgreSQL, and Flyway schema migrations
  - [ ] Create `docker-compose.yml` for local PostgreSQL 16 and `backend/` Gradle wrapper (`build.gradle`, `settings.gradle`) with Spring Boot 3, Flyway, Spring Data JPA/JDBC, Spring Security, Spring Boot Actuator, and Docker Testcontainers (`org.testcontainers:postgresql`)
  - [ ] Write failing Testcontainers database migration integration test verifying schema creation (`V1__init_schema.sql`) and seed fixtures (`V2__seed_local_data.sql`)
  - [ ] Implement `V1__init_schema.sql` (tables for `org_units`, `persons`, `skills`, `team_roles`, `person_skills`, `teams`, `team_memberships`, `outcomes`, `key_results`, `outcome_teams` with allocation rationale/intent, and `outcome_contributors`) and `V2__seed_local_data.sql` (including unallocated, under-allocated, over-allocated, fragmented, and inactive leaver fixtures)
  - [ ] Run `./gradlew test` in single-run mode and verify migration tests pass
- [ ] Task: Implement `WorkdayDirectoryPort`, `MockWorkdayAdapter`, `WorkdayRaaSAdapter`, and `WorkdaySyncService` (TDD)
  - [ ] Write failing unit and Testcontainers integration tests for `MockWorkdayAdapter`, `WorkdayRaaSAdapter` (via `MockRestServiceServer` testing `{"Report_Entry": [...]}` parsing and auth headers), idempotent employee upserts, and soft-deactivation (`INACTIVE`) of omitted/terminated Workday leavers
  - [ ] Implement `WorkdayDirectoryPort`, `MockWorkdayAdapter`, `WorkdayRaaSAdapter`, `WorkdaySyncService`, and `POST /api/v1/workday/sync` + `GET /api/v1/persons`
  - [ ] Run `./gradlew test` in single-run mode and confirm all Workday sync and adapter tests pass
- [ ] Task: Phase Verification & Checkpoint (Refer to workflow.md)

## Phase 2: Core Domain — RBAC Personas, Team-Level Skill Aggregation, Teams, Outcomes & Organisational Diagnostics
- [ ] Task: Implement RBAC Persona Security Filter, Skill & Role Catalog, Direct Validated Skill Assignment, and Team-Level Skill Aggregation (TDD)
  - [ ] Write failing tests for header-based RBAC (`X-Persona-Role`: `ADMIN`, `ORG_LEADER`, `TEAM_LEAD`, `READ_ONLY_VIEWER` and `X-Person-Id`), catalog CRUD (`/api/v1/catalog/roles`, `/api/v1/catalog/skills`), direct validated skill assignment (`POST /api/v1/persons/{personId}/skills`), and Team-Level Skill Aggregation (`GET /api/v1/teams/{teamId}/skills-summary` & `GET /api/v1/skills/team-matrix`) computing member coverage, max/average proficiency, and category maturity rollups
  - [ ] Implement RBAC security filter, Role & Skill catalog controllers/services, direct validated skill assignment logic, and `TeamSkillAggregationService`
  - [ ] Run `./gradlew test` in single-run mode and verify RBAC, skill catalog, and team skill aggregation tests pass
- [ ] Task: Implement Hierarchical Org Units, Team Composition, `>100%` Soft Over-Allocation Warning, and Leaver Offboarding (TDD)
  - [ ] Write failing unit and integration tests for Org Unit tree queries, Team creation/updates (`TEAM_LEAD` ownership check), `>100%` overlapping active membership soft-warning calculation (`isOverAllocated = true`, `activeTeamCount`, and breakdown), `INACTIVE` Workday leaver flagging on memberships, and one-click membership offboarding/reassignment (`POST /api/v1/teams/{teamId}/memberships/{membershipId}/offboard`)
  - [ ] Implement Org Unit and Team domain services, capacity allocation calculator, leaver offboarding endpoint, and REST controllers (`/api/v1/org-units`, `/api/v1/teams`)
  - [ ] Run `./gradlew test` in single-run mode and verify all team and capacity domain tests pass
- [ ] Task: Implement Outcome Commitments, Jira Canonical Team Directory API, and Organisational & Capacity Health Diagnostics Suite (TDD)
  - [ ] Write failing integration tests for Outcome & Key Result management (`/api/v1/outcomes` including team allocation rationale for skill growth vs capability delivery), the read-only Jira Team Directory API (`GET /api/v1/integrations/jira/teams?jiraProjectKey=...&orgUnitId=...`), and the Diagnostics Suite (`GET /api/v1/diagnostics` detecting `0%` unallocated employees, `<100%` under-allocated employees, `>100%` over-allocated employees, `>2` concurrent team fragmentation, active Workday leaver assignments, teams missing an active lead, and outcomes missing committed teams/contributors)
  - [ ] Implement Outcome domain service/controller, `JiraIntegrationController`, and `DiagnosticsService` / `DiagnosticsController` (`/api/v1/diagnostics`)
  - [ ] Run `./gradlew test jacocoTestReport` in single-run mode and confirm >80% backend coverage
- [ ] Task: Phase Verification & Checkpoint (Refer to workflow.md)

## Phase 3: Glassmorphic React SPA Frontend — High-Density Operational Views, Team Skill Rollup & Diagnostics Dashboard
- [ ] Task: Scaffold Vite + React 18 + TypeScript SPA with Glassmorphic Design System, API Client, and Header Persona Switcher (TDD)
  - [ ] Initialize `frontend/` with Vite, React 18, TypeScript, Tailwind CSS (configured with Glassmorphic frosted-glass utilities `backdrop-blur-md`/`xl`, translucent surface tokens, specular `border-white/15` borders, and ambient mesh gradient background), TanStack Query/Table, React Router, and Vitest + React Testing Library
  - [ ] Write failing component tests (`npx vitest run`) for the Glassmorphic Workspace Shell, RBAC Persona Switcher (`Admin`, `Org Leader`, `Team Lead`, `Read-Only Viewer`), live Diagnostics header badge, and Admin on-demand Workday Sync trigger
  - [ ] Implement API client (propagating `X-Persona-Role` and `X-Person-Id` headers), Glassmorphic Workspace Layout, and Persona Switcher
  - [ ] Run `npx vitest run` and confirm shell and persona switcher tests pass
- [ ] Task: Implement Glassmorphic Team Composition View & Dual-Mode Skill Matrix / Team Skill Rollup View (TDD)
  - [ ] Write failing component tests (`npx vitest run`) for the split-pane Org Unit explorer + Team Composition table (verifying Amber `>100%` over-allocation and `>2` team fragmentation badges with breakdown tooltip, Red/Rose `Workday Inactive / Leaver` badge with one-click Offboard/Reassign action, Read-Only Workday lock indicators, and Jira Project links) and the dual-mode Skill Matrix heatmap grid (toggling between Individual Skill Matrix and Team-Level Skill Summary & Category Heatmap)
  - [ ] Implement Glassmorphic Team Composition View, Membership & Leaver Offboarding drawers/actions, and Dual-Mode Skill Matrix / Team Skill Rollup Heatmap View
  - [ ] Run `npx vitest run` and verify all Team Composition and Skill Matrix tests pass
- [ ] Task: Implement Outcome Commitments View (with Team Skill Comparison), Diagnostics Dashboard & Jira Directory Preview (TDD)
  - [ ] Write failing component tests (`npx vitest run`) for the Outcome Commitments view (including the inline Team Skill Summary comparison in the team allocation drawer so leaders can select teams for capability match or skill growth), the Organisational & Capacity Diagnostics View (filtering `0%` unallocated, `<100%` under-allocated, `>100%` over-allocated, `>2` fragmented teams, active leavers, leadless teams, and outcome gaps), and the Jira Team Directory payload inspector
  - [ ] Implement Glassmorphic Outcome Commitments View, Team Skill Allocation Drawer, Organisational Diagnostics Dashboard View, and Jira Team Directory integration view
  - [ ] Run `npx vitest run --coverage` and `npm run build` in single-run mode to verify >80% frontend coverage and clean production build
- [ ] Task: Phase Verification & Checkpoint (Refer to workflow.md)

## Phase 4: Google Cloud Platform (GCP) Containerization & Automated CI/CD Push Pipeline
- [ ] Task: Implement Cloud Run Multi-Stage Dockerfiles, Cloud SQL PostgreSQL Configuration, and Automated Cloud Build / GitHub Actions Pipelines (TDD)
  - [ ] Write automated configuration and build verification tests validating `backend/Dockerfile`, `frontend/Dockerfile` (with Nginx SPA routing and `$PORT` binding), Spring Boot `gcp` profile for Cloud SQL PostgreSQL (`spring-cloud-gcp-starter-sql-postgresql` / environment variable configuration), `cloudbuild.yaml`, and `.github/workflows/deploy-gcp.yml`
  - [ ] Implement multi-stage `backend/Dockerfile` and `frontend/Dockerfile`, `application-gcp.yml`, `cloudbuild.yaml` (Artifact Registry build/push + `gcloud run deploy` for backend and frontend with Cloud SQL attachment), and `.github/workflows/deploy-gcp.yml` (Workload Identity Federation automated push & deployment on `main`)
  - [ ] Run full backend and frontend verification suites in single-run mode and validate container/pipeline configurations
- [ ] Task: Phase Verification & Checkpoint (Refer to workflow.md)
