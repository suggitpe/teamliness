# Track Specification: Teamliness MVP — Organisational Team & Outcome Management

## 1. Overview
Build the end-to-end **Teamliness** client-server application: a modular monolith **Java 21 + Spring Boot 3 (Gradle)** backend backed by **PostgreSQL + Flyway** and tested with **Docker Testcontainers**, paired with a **Glassmorphic TypeScript + React 18 (Vite)** Single-Page Application (SPA) and automated continuous deployment to **Google Cloud Platform (Cloud Run + Cloud SQL for PostgreSQL + Artifact Registry via Cloud Build & GitHub Actions)**.

The MVP enables leaders to:
1. Synchronize employee records from Workday via a hexagonal `WorkdayDirectoryPort`.
2. Manage a standardized Skill & Role Catalog, assign validated skills to employees, and view **Team-Level Aggregated Skill Summaries & Category Heatmaps** so leaders can select teams for Outcomes based on either existing capability depth or intentional **team skill growth**.
3. Compose cross-functional teams within a hierarchical Org Unit tree with capacity allocation tracking (`>100%` soft over-allocation warnings and inactive Workday leaver offboarding).
4. Run an **Organisational & Capacity Health Diagnostics Suite** checking for unallocated (`0%`) / under-allocated (`<100%`) employees, over-allocated (`>100%`) / multi-team fragmented (`>2` concurrent teams) employees, active Workday leavers on teams/outcomes, leadless teams, and outcome coverage gaps.
5. Commit teams to Outcomes linked to external **Jira** work items and expose a dedicated read-only **Jira Team Directory API**.

## 2. Functional Requirements

### 2.1 Hexagonal Workday Integration (`WorkdayDirectoryPort`) & Person Projection
- **Domain Model (`Person`):** Stores `id` (UUID), `workdayEmployeeId` (unique external key), `fullName`, `email`, `jobTitle`, `department`, `costCentre`, `managerEmployeeId` (nullable self-reference), `employmentStatus` (`ACTIVE` | `INACTIVE`), `lastSyncedAt`, and locally assigned `PersonSkill` mappings.
- **Hexagonal Port (`WorkdayDirectoryPort`):** Defines `List<WorkdayEmployeeRecord> fetchDirectory()` isolated from persistence and HTTP details.
- **Adapters:**
  1. `MockWorkdayAdapter` (`teamliness.workday.mode=mock`): Returns a rich hierarchical organisation fixture (~15 employees across 2 Divisions and 4 Departments, manager chains, 1 `INACTIVE` leaver, unallocated `0%` employees, under-allocated `<100%` employees, and employees configured for `>100%` over-allocation and `>2` team fragmentation).
  2. `WorkdayRaaSAdapter` (`teamliness.workday.mode=raas`): Uses Spring `RestClient` to fetch Workday Report-as-a-Service JSON (`{"Report_Entry": [...]}`) with configurable URL, Basic/Bearer auth headers, and timeouts.
- **Idempotent Sync & Soft-Deactivation Service (`WorkdaySyncService`):**
  - Upserts employees by `workdayEmployeeId` without duplicating records or overwriting local skill assignments.
  - Marks employees as `INACTIVE` when their Workday status is inactive or when omitted from a full directory snapshot.
  - Exposes `POST /api/v1/workday/sync` (Admin-only on-demand trigger) and optional scheduled background sync.

### 2.2 Skill & Role Catalog, Direct Validated Skill Assignments & Team-Level Skill Aggregation
- **Role Catalog (`TeamRole`):** Standardized roles (`Tech Lead`, `Product Owner`, `Software Engineer`, `Platform Engineer`, `Delivery Manager`, `QA Engineer`) managed via `/api/v1/catalog/roles`.
- **Skill Catalog (`Skill`):** Standardized skills grouped by high-level category (`Backend`, `Frontend`, `Cloud & DevOps`, `Data & Analytics`, `Product & Delivery`) managed via `/api/v1/catalog/skills`.
- **Direct Skill Validation (`PersonSkill`):** `ADMIN`, `ORG_LEADER`, and `TEAM_LEAD` (for members of their own teams) can directly assign or update a skill on a `Person` with proficiency (`BEGINNER`, `INTERMEDIATE`, `EXPERT`). Assigned skills are immediately marked validated with `validatedByPersonId` and `validatedAt`.
- **Team-Level Skill Summary & Category Heatmap (`GET /api/v1/teams/{teamId}/skills-summary` & `GET /api/v1/skills/team-matrix`):**
  - Aggregates active team members' validated skills up to the **Team** level by both individual `Skill` and high-level `Skill Category`.
  - Computes per-team metrics: `activeMemberCount`, `membersWithSkillCount`, `coveragePercentage`, `maxProficiency` (`NONE`, `BEGINNER`, `INTERMEDIATE`, `EXPERT`), `averageProficiencyScore` (0.0–3.0 scale), and `proficiencyDistribution` (`expertCount`, `intermediateCount`, `beginnerCount`).
  - Surfaces **Skill Growth / Mentorship Indicators** (e.g., highlighting teams with emerging `BEGINNER`/`INTERMEDIATE` capability or single-expert mentorship potential) so leaders can manually compare teams and choose whether to allocate an Outcome to a high-proficiency team or to a team where the work will intentionally **grow skills**.

### 2.3 Hierarchical Org Units, Team Composition & Capacity Allocation
- **Org Unit Hierarchy (`OrgUnit`):** Tree structure (`DIVISION` -> `TRIBE` / `DEPARTMENT`) with optional parent reference, aligning with Workday departments while allowing cross-functional teams.
- **Team (`Team`):** Name, unique slug, mission, `parentOrgUnitId`, `leadPersonId`, optional `jiraProjectKey` and `jiraBoardUrl`, and time-bounded `TeamMembership` records (`personId`, `roleId`, `allocationPercentage`, `startDate`, optional `endDate`).
- **Soft Over-Allocation Warning (`> 100%`) & Concurrent Team Count:**
  - When creating or updating a membership, the domain service computes the person's total active allocation and active team count across all overlapping active memberships.
  - Totals `> 100%` are permitted (soft warning) and return `isOverAllocated = true`, `totalAllocationPercentage`, `activeTeamCount`, and the breakdown of overlapping team memberships.
- **Inactive Workday Leaver Flagging & One-Click Offboarding:**
  - Any active membership or outcome contribution where `person.employmentStatus == INACTIVE` is flagged with `isWorkdayInactiveLeaver = true`.
  - Endpoint `POST /api/v1/teams/{teamId}/memberships/{membershipId}/offboard` (or reassign) sets `endDate = LocalDate.now()` (and optionally creates a replacement membership) in one action while preserving historical records.

### 2.4 Organisational & Capacity Health Diagnostics Suite (`GET /api/v1/diagnostics`)
- **Diagnostics Engine & REST Endpoint (`GET /api/v1/diagnostics`):**
  - Evaluates the organisation state on demand (with optional `?orgUnitId=...` filtering) and returns a structured diagnostic report with summary KPI counts and actionable findings across four categories:
    1. **Unallocated (`0%`) & Under-Allocated (`<100%`) Active Employees:** Lists every `ACTIVE` Workday employee with `0%` active team allocation (`UNALLOCATED`) or `1%–99%` total active allocation (`UNDER_ALLOCATED`).
    2. **Over-Allocated (`>100%`) & Multi-Team Fragmented (`>2` Concurrent Teams) Employees:** Lists every employee whose overlapping active memberships exceed `100%` (`OVER_ALLOCATED`) or who is assigned to more than 2 active teams simultaneously (`HIGH_TEAM_FRAGMENTATION`), including their full team allocation breakdown.
    3. **Active Workday Leavers & Leadless Teams:** Lists `INACTIVE` Workday employees still assigned to active Team memberships or Outcome contributions (`ACTIVE_LEAVER_ASSIGNMENT`), plus active Teams with no `leadPersonId` or an `INACTIVE` lead (`TEAM_MISSING_ACTIVE_LEAD`).
    4. **Outcome Coverage Gaps:** Lists `ACTIVE` or `AT_RISK` Outcomes that have zero committed Teams (`OUTCOME_NO_COMMITTED_TEAMS`) or zero active Contributors (`OUTCOME_NO_ACTIVE_CONTRIBUTORS`).

### 2.5 Outcome Commitments & External Jira Alignment
- **Outcome (`Outcome`):** `title`, `description`, `targetDate`, `status` (`DRAFT`, `ACTIVE`, `AT_RISK`, `COMPLETED`), optional `jiraEpicKey` and `jiraJqlUrl`, child `KeyResult` items (`title`, `targetValue`, `currentValue`, `unit`, `jiraIssueUrl`), committed `Team` mappings (with optional `allocationIntent` / `rationale`, e.g., `CAPABILITY_MATCH` vs `SKILL_GROWTH` and notes), and explicit `OutcomeContributor` mappings (`personId`, `teamId`, `contributionRole`).
- **Jira Canonical Team Directory API (`GET /api/v1/integrations/jira/teams`):**
  - Read-only endpoint returning active teams with `teamId`, `slug`, `name`, `mission`, `orgUnitPath`, `jiraProjectKey`, `jiraBoardUrl`, `teamLead`, and active `members` (with role and allocation %).
  - Supports query filtering by `?jiraProjectKey=...` and `?orgUnitId=...`.

### 2.6 Role-Based Access Control (RBAC) & Glassmorphic React SPA Views
- **RBAC Enforcement:** Spring Security filter inspecting `X-Persona-Role` (`ADMIN`, `ORG_LEADER`, `TEAM_LEAD`, `READ_ONLY_VIEWER`) and `X-Person-Id`.
- **Glassmorphic Design System:**
  - Built with Tailwind CSS frosted-glass surfaces (`backdrop-blur-md`/`xl`, translucent `bg-slate-900/55` and `bg-white/10` panels, `1px` specular `border-white/15` borders, and radial mesh gradient background) while maintaining high-contrast accessible status badges.
- **React SPA Views:**
  1. **Glassmorphic Header & Persona Switcher:** Switch active persona (`Admin`, `Org Leader`, `Team Lead`, `Read-Only Viewer`), view Workday sync status, view a live Diagnostics Alert badge count, and trigger on-demand Workday sync (`Admin`).
  2. **Team Composition View:** Split-pane glassmorphic Org Unit hierarchy explorer + compact team roster table with Amber `>100%` over-allocation and `>2` team fragmentation badges (with breakdown tooltip), Red/Rose `Workday Inactive / Leaver` badges + one-click offboard/reassign button, read-only Workday lock icons, inline Team Skill Category summary bar, and Jira Project links.
  3. **Skill Matrix & Team Skill Rollup View:** Toggleable heatmap grid showing:
     - **Individual Skill Matrix:** People vs. Skill Catalog (`Beginner`, `Intermediate`, `Expert`) with inline skill assignment.
     - **Team-Level Skill Summary & Category Heatmap:** Teams vs. Skill Categories & Individual Skills showing member coverage (`e.g., 3/5`), max/average proficiency, and skill growth potential.
  4. **Outcome Commitments View (with Team Skill Comparison):** Glassmorphic board/table view of Outcomes, Key Results, committed Teams, Contributors, and Jira Epic/JQL links. Includes a **Team Skill Summary Preview** inside the Outcome Team Allocation drawer so leaders can manually select teams for delivery capability or **team skill growth**.
  5. **Organisational & Capacity Diagnostics View:** Interactive glassmorphic diagnostic dashboard displaying KPI scorecards and actionable issue tables for Unallocated (`0%`) / Under-Allocated (`<100%`) employees, Over-Allocated (`>100%`) / Fragmented (`>2` teams) employees, Active Leavers & Leadless Teams, and Outcome Coverage Gaps.

### 2.7 Google Cloud Platform (GCP) Containerization & Automated CI/CD Push
- **Containerization:**
  - `backend/Dockerfile`: Multi-stage build (Gradle + Eclipse Temurin JDK 21 build stage -> minimal Temurin JRE 21 runtime stage) exposing `$PORT` (`8080`) and `/actuator/health` for Cloud Run, with Cloud SQL PostgreSQL socket/JDBC configuration support.
  - `frontend/Dockerfile`: Multi-stage build (Node 20 Vite production build -> Nginx Alpine runtime) serving the SPA on `$PORT` (`8080`) with SPA fallback routing and configurable backend API URL.
- **Automated GCP Deployment Pipelines:**
  - `cloudbuild.yaml`: Google Cloud Build pipeline executing single-run tests, building & pushing backend/frontend images to Google Artifact Registry, and deploying both services to Google Cloud Run connected to Cloud SQL for PostgreSQL.
  - `.github/workflows/deploy-gcp.yml`: GitHub Actions workflow using GCP Workload Identity Federation (`google-github-actions/auth` & `deploy-cloudrun`) to automatically test, build, push, and deploy to GCP on push to `main`.

## 3. Non-Functional Requirements
- **Strict TDD & Single-Run Tests:** Every domain rule and endpoint covered by JUnit 5 + Docker Testcontainers PostgreSQL (`./gradlew test`) and Vitest (`npx vitest run`) with `>80%` code coverage and zero watch flags.
- **Strict Schema Migrations:** Flyway SQL migrations (`V1__init_schema.sql`, `V2__seed_local_data.sql`) validated via Hibernate/JDBC.

## 4. Acceptance Criteria
1. `WorkdaySyncService` idempotently upserts Workday employees via both `MockWorkdayAdapter` and `WorkdayRaaSAdapter` (verified via MockRestServiceServer) and soft-deactivates omitted/inactive leavers.
2. Assigning a person across teams totaling `>100%` succeeds and flags `isOverAllocated = true` with an Amber warning badge in the UI.
3. Deactivating a person in Workday flags their active team memberships and outcome contributions with a Red/Rose leaver badge, and the one-click offboard action closes their active membership while preserving history.
4. `GET /api/v1/teams/{teamId}/skills-summary` and `GET /api/v1/skills/team-matrix` accurately aggregate individual member skills into Team-level skill and category rollups, surfaced in both the Skill Matrix View and Outcome Allocation drawer to support capability-based or skill-growth team allocation.
5. `GET /api/v1/diagnostics` accurately identifies unallocated (`0%`), under-allocated (`<100%`), over-allocated (`>100%`), multi-team fragmented (`>2` teams), active Workday leaver assignments, teams missing an active lead, and outcomes missing committed teams/contributors.
6. The React SPA implements the Glassmorphic UI theme (`backdrop-blur`, translucent surfaces, specular borders, ambient gradient canvas) across all views.
7. Multi-stage `Dockerfile`s, `cloudbuild.yaml`, and `.github/workflows/deploy-gcp.yml` are present and validated for automated deployment to Google Cloud Run + Cloud SQL PostgreSQL + Artifact Registry.
8. `TEAM_LEAD` can mutate their own led team(s) and receives `403 Forbidden` when attempting to mutate another lead's team.
9. `GET /api/v1/integrations/jira/teams` returns the structured team topology and filters accurately by `jiraProjectKey` and `orgUnitId`.
10. All backend (`./gradlew test`) and frontend (`npx vitest run`) suites pass in single-run mode.

## 5. Out of Scope (MVP)
- Two-way push mutations from Teamliness into Workday or Jira REST APIs.
- External OAuth2/OIDC identity provider integration (replaced by header-based RBAC + Persona Switcher for MVP).
