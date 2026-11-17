# Track Specification: Teamliness MVP — Organisational Team & Outcome Management

## 1. Overview
Build the initial end-to-end **Teamliness** client-server application: a modular monolith **Java 21 + Spring Boot 3 (Gradle)** backend backed by **PostgreSQL + Flyway** and tested with **Docker Testcontainers**, paired with a **TypeScript + React 18 (Vite)** Single-Page Application (SPA). The MVP enables leaders to synchronize employee records from Workday via a hexagonal `WorkdayDirectoryPort`, manage a standardized Skill & Role Catalog, assign validated skills to employees, compose cross-functional teams within a hierarchical Org Unit tree with capacity allocation tracking (`>100%` soft over-allocation warnings and inactive Workday leaver offboarding), commit teams to Outcomes linked to external **Jira** work items, and expose a dedicated read-only **Jira Team Directory API**.

## 2. Functional Requirements

### 2.1 Hexagonal Workday Integration (`WorkdayDirectoryPort`) & Person Projection
- **Domain Model (`Person`):** Stores `id` (UUID), `workdayEmployeeId` (unique external key), `fullName`, `email`, `jobTitle`, `department`, `costCentre`, `managerEmployeeId` (nullable self-reference), `employmentStatus` (`ACTIVE` | `INACTIVE`), `lastSyncedAt`, and locally assigned `PersonSkill` mappings.
- **Hexagonal Port (`WorkdayDirectoryPort`):** Defines `List<WorkdayEmployeeRecord> fetchDirectory()` isolated from persistence and HTTP details.
- **Adapters:**
  1. `MockWorkdayAdapter` (`teamliness.workday.mode=mock`): Returns a rich hierarchical organisation fixture (~15 employees across 2 Divisions and 4 Departments, manager chains, 1 `INACTIVE` leaver, and employees pre-configured for >100% over-allocation).
  2. `WorkdayRaaSAdapter` (`teamliness.workday.mode=raas`): Uses Spring `RestClient` to fetch Workday Report-as-a-Service JSON (`{"Report_Entry": [...]}`) with configurable URL, Basic/Bearer auth headers, and timeouts.
- **Idempotent Sync & Soft-Deactivation Service (`WorkdaySyncService`):**
  - Upserts employees by `workdayEmployeeId` without duplicating records or overwriting local skill assignments.
  - Marks employees as `INACTIVE` when their Workday status is inactive or when omitted from a full directory snapshot.
  - Exposes `POST /api/v1/workday/sync` (Admin-only on-demand trigger) and optional scheduled background sync.

### 2.2 Skill & Role Catalog and Direct Validated Skill Assignments
- **Role Catalog (`TeamRole`):** Standardized roles (`Tech Lead`, `Product Owner`, `Software Engineer`, `Platform Engineer`, `Delivery Manager`, `QA Engineer`) managed via `/api/v1/catalog/roles`.
- **Skill Catalog (`Skill`):** Standardized skills grouped by category (e.g., `Backend`, `Frontend`, `Cloud & DevOps`, `Product & Delivery`) managed via `/api/v1/catalog/skills`.
- **Direct Skill Validation (`PersonSkill`):** `ADMIN`, `ORG_LEADER`, and `TEAM_LEAD` (for members of their own teams) can directly assign or update a skill on a `Person` with proficiency (`BEGINNER`, `INTERMEDIATE`, `EXPERT`). Assigned skills are immediately marked validated with `validatedByPersonId` and `validatedAt`.

### 2.3 Hierarchical Org Units, Team Composition & Capacity Allocation
- **Org Unit Hierarchy (`OrgUnit`):** Tree structure (`DIVISION` -> `TRIBE` / `DEPARTMENT`) with optional parent reference, aligning with Workday departments while allowing cross-functional teams.
- **Team (`Team`):** Name, unique slug, mission, `parentOrgUnitId`, `leadPersonId`, optional `jiraProjectKey` and `jiraBoardUrl`, and time-bounded `TeamMembership` records (`personId`, `roleId`, `allocationPercentage`, `startDate`, optional `endDate`).
- **Soft Over-Allocation Warning (`> 100%`):**
  - When creating or updating a membership, the domain service computes the person's total active allocation across all overlapping active memberships.
  - Totals `> 100%` are permitted (soft warning) and return `isOverAllocated = true`, `totalAllocationPercentage`, and the breakdown of overlapping team memberships.
- **Inactive Workday Leaver Flagging & One-Click Offboarding:**
  - Any active membership or outcome contribution where `person.employmentStatus == INACTIVE` is flagged with `isWorkdayInactiveLeaver = true`.
  - Endpoint `POST /api/v1/teams/{teamId}/memberships/{membershipId}/offboard` (or reassign) sets `endDate = LocalDate.now()` (and optionally creates a replacement membership) in one action while preserving historical records.

### 2.4 Outcome Commitments & External Jira Alignment
- **Outcome (`Outcome`):** `title`, `description`, `targetDate`, `status` (`DRAFT`, `ACTIVE`, `AT_RISK`, `COMPLETED`), optional `jiraEpicKey` and `jiraJqlUrl`, child `KeyResult` items (`title`, `targetValue`, `currentValue`, `unit`, `jiraIssueUrl`), committed `Team` mappings, and explicit `OutcomeContributor` mappings (`personId`, `teamId`, `contributionRole`).
- **Jira Canonical Team Directory API (`GET /api/v1/integrations/jira/teams`):**
  - Read-only endpoint returning active teams with `teamId`, `slug`, `name`, `mission`, `orgUnitPath`, `jiraProjectKey`, `jiraBoardUrl`, `teamLead`, and active `members` (with role and allocation %).
  - Supports query filtering by `?jiraProjectKey=...` and `?orgUnitId=...`.

### 2.5 Role-Based Access Control (RBAC) & React SPA Views
- **RBAC Enforcement:** Spring Security filter inspecting `X-Persona-Role` (`ADMIN`, `ORG_LEADER`, `TEAM_LEAD`, `READ_ONLY_VIEWER`) and `X-Person-Id`.
  - `ADMIN`: Full access including Workday sync trigger and catalog management.
  - `ORG_LEADER`: Cross-team creation/editing, skill assignment, and outcome management.
  - `TEAM_LEAD`: Write access strictly scoped to Teams where `team.leadPersonId == X-Person-Id` (including memberships, leaver offboarding, outcome commitments, and skill assignments for their team members).
  - `READ_ONLY_VIEWER`: Read-only access across all views and Jira integration endpoints.
- **React SPA Views:**
  1. **Header & Persona Switcher:** Switch active persona (`Admin`, `Org Leader`, `Team Lead`, `Read-Only Viewer`), view Workday sync status, and trigger on-demand Workday sync (`Admin`).
  2. **Team Composition View:** Split-pane Org Unit hierarchy explorer + compact team roster table with Amber `>100%` over-allocation badges (with breakdown tooltip), Red/Rose `Workday Inactive / Leaver` badges + one-click offboard/reassign button, read-only Workday lock icons, and Jira Project links.
  3. **Skill Matrix View:** Heatmap grid cross-referencing People and Teams against the Skill Catalog (`Beginner`, `Intermediate`, `Expert`) with inline skill assignment for authorized personas.
  4. **Outcome Commitments View:** Board/table view of Outcomes, Key Results, committed Teams, Contributors (with leaver warnings), and external Jira Epic/JQL links.

## 3. Non-Functional Requirements
- **Strict TDD & Single-Run Tests:** Every domain rule and endpoint covered by JUnit 5 + Docker Testcontainers PostgreSQL (`./gradlew test`) and Vitest (`npx vitest run`) with `>80%` code coverage and zero watch flags.
- **Strict Schema Migrations:** Flyway SQL migrations (`V1__init_schema.sql`, `V2__seed_local_data.sql`) validated via Hibernate/JDBC.

## 4. Acceptance Criteria
1. `WorkdaySyncService` idempotently upserts Workday employees via both `MockWorkdayAdapter` and `WorkdayRaaSAdapter` (verified via MockRestServiceServer) and soft-deactivates omitted/inactive leavers.
2. Assigning a person across teams totaling `>100%` succeeds and flags `isOverAllocated = true` with an Amber warning badge in the UI.
3. Deactivating a person in Workday flags their active team memberships and outcome contributions with a Red/Rose leaver badge, and the one-click offboard action closes their active membership while preserving history.
4. `TEAM_LEAD` can mutate their own led team(s) and receives `403 Forbidden` when attempting to mutate another lead's team.
5. `GET /api/v1/integrations/jira/teams` returns the structured team topology and filters accurately by `jiraProjectKey` and `orgUnitId`.
6. All backend (`./gradlew test`) and frontend (`npx vitest run`) suites pass in single-run mode.

## 5. Out of Scope (MVP)
- Two-way push mutations from Teamliness into Workday or Jira REST APIs.
- External OAuth2/OIDC identity provider integration (replaced by header-based RBAC + Persona Switcher for MVP).
