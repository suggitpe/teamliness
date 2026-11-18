# Product Definition: Teamliness — Organisational Team & Outcome Management

## 1. Product Vision & Summary
**Teamliness** is an Organisational Team & Outcome Management platform designed for engineering and operational leaders. Featured in a modern **Glassmorphic UI**, documented continuously through **Specification by Example (ATDD) HTML Living Documentation**, and engineered to run natively on **Google Cloud Platform (GCP)** with automated continuous deployment, Teamliness bridges the gap between HR system-of-record data (sourced from **Workday**), internal capability tracking (validated skills, standardized team roles, and **team-level skill rollups**), cross-functional team composition with capacity tracking, automated **Organisational & Capacity Health Diagnostics**, and high-level outcome commitments aligned with external work execution in **Jira**. Teamliness serves as the canonical system of record for team topology, exposing a dedicated read API so Jira and other external systems can fetch the authoritative list of teams, while linking Teams and Outcomes back to Jira Projects, Boards, Epics, and JQL filters.

## 2. Target Personas & Role-Based Access Control (RBAC)
Role-based personas are enforced across both the REST API and React SPA:
- **Admin:** Full system privileges, including triggering and configuring Workday directory synchronization, managing the standardized Skill & Role Catalog, managing the Organisational Unit hierarchy, running organisation-wide health diagnostics, and administering user/persona roles.
- **Org Leader:** Creates and structures Teams within Hierarchical Org Units (e.g., Division -> Tribe/Department -> Team), assigns Team Leads, oversees cross-team capacity allocations, inspects **Team-Level Aggregated Skill Profiles** to allocate Outcomes to teams (either for strong existing capability or intentional skill growth), and resolves organisation-wide diagnostic warnings.
- **Team Lead:** Manages memberships, role assignments, direct skill proficiency assignments, leaver offboarding/reassignment actions, team skill growth visibility, and outcome commitments/contributor mappings for their assigned Team(s).
- **Read-Only Viewer (& External Jira Integration Consumer):** Views team topologies, individual and team-aggregated skill matrices, capacity allocations, diagnostic reports, and outcome commitments without mutation rights; external systems (such as Jira) query the dedicated read-only Team Directory API.

## 3. Core Domain Entities & Capabilities

### 3.1 Person (Workday-Sourced System of Record)
- **Attributes:** External Workday Employee ID (`employee_id`), full name, email, job title, department, cost centre, manager hierarchy reference, and employment status (`Active` / `Inactive`).
- **Read-Only Projection:** Core HR fields are maintained as a local read-only projection updated exclusively via the `WorkdayDirectoryPort` boundary.
- **Validated Skill Tagging:** Enriched locally with skill tags and proficiency levels (`Beginner`, `Intermediate`, `Expert`). Skills are assigned directly by authorised leaders (`Team Lead`, `Org Leader`, `Admin`) and are treated as validated upon assignment without requiring a separate multi-step approval queue.
- **Leaver Handling & One-Click Offboarding:** When an employee is marked `Inactive` or omitted in a Workday sync, they are soft-deactivated locally (`Inactive`). Any active Team memberships or Outcome contributor mappings for that person are automatically flagged with a **"Workday Inactive / Leaver"** warning badge, accompanied by a one-click action for Team Leads/Org Leaders to close (set `end_date`) or reassign their memberships while preserving historical records.

### 3.2 Skill & Role Catalog & Team-Level Skill Aggregation
- **Standardized Roles:** Curated catalog of team roles (e.g., `Tech Lead`, `Product Owner`, `Software Engineer`, `Platform Engineer`, `Delivery Manager`) used when assigning members to teams.
- **Skills & Proficiency Levels:** Standardized skill taxonomy grouped by high-level capability category (e.g., `Backend`, `Frontend`, `Cloud & DevOps`, `Data & Analytics`, `Product & Delivery`) and rated across three explicit proficiency tiers: `Beginner`, `Intermediate`, and `Expert`.
- **Team-Level Aggregated Skill Assessments:**
  - Automatically rolls up active team members' validated skills into a **Team Skill Summary & Category Heatmap** (surfacing member coverage count, highest proficiency tier, weighted average team proficiency, and single-point-of-failure / bench-depth indicators).
  - Enables leaders to compare teams side-by-side when deciding where to allocate Outcomes or work streams—supporting both **capability-led allocation** (assigning work to teams with proven `Expert` depth) and **skill-growth allocation** (intentionally assigning work to a team to develop emerging `Beginner`/`Intermediate` skills or broaden team capabilities).

### 3.3 Organisational Hierarchy, Teams & Capacity Allocation
- **Hierarchical Org Units:** Supports a tree of Organisational Units (`Division -> Tribe / Department -> Team`) that can align with Workday departments while allowing cross-functional teams to include members from multiple Workday departments and cost centres.
- **Team Attributes:** Name, mission statement, parent Org Unit, designated Team Lead (`Person`), optional **Jira Project Key / Board URL**, and time-bounded memberships (`start_date`, optional `end_date`).
- **Soft Over-Allocation Warning (>100%):** Each membership pairs a `Person` + `Role` + `allocation_percentage`. When a person's combined allocation across overlapping active team memberships exceeds `100%`, the assignment is permitted with a **soft warning** — the domain layer calculates total active allocation and sets an explicit `is_over_allocated` flag surfaced as an alert badge in the UI and API.
- **Dedicated Jira Team Directory Endpoint:** Exposes a stable, read-only REST endpoint returning active teams, their hierarchy, leads, members, and Jira Project Key mappings so Jira can consume and synchronize the canonical list of teams.

### 3.4 Outcome Commitments & External Jira Alignment
- **Measurable Outcomes:** High-level targets (`title`, `description`, `target_date`, `status`, `key_results`) committed to by one or more Teams (with optional allocation rationale/notes such as *"Skill Growth Target"* or *"Core Capability Delivery"*), plus explicit contributor mappings (`Person` + contribution role/notes).
- **Team Skill Context During Outcome Allocation:** When committing Teams to an Outcome, leaders can view the **Team-Level Skill Summary & Category Heatmap** inline to make informed trade-offs between delivery velocity and team skill development.
- **Jira Work Execution Linkage:** Day-to-day task assignment and sprint execution reside in **Jira** (where work is allocated to Teams). Each Outcome and Key Result in Teamliness can store an optional **Jira Epic Key / Issue URL / JQL Query link**, enabling leaders to jump directly from high-level outcome commitments and team capacity in Teamliness to granular work items in Jira.

### 3.5 Organisational & Capacity Health Diagnostics Suite
Teamliness provides an on-demand and real-time **Diagnostics Suite** (`/api/v1/diagnostics` and a dedicated **Diagnostics View**) that evaluates organisational health rules and surfaces actionable remediation steps:
1. **Unallocated (`0%`) & Under-Allocated (`<100%`) Employees Check:** Identifies active Workday employees who have no active team allocation (`0%` allocation) or partial capacity (`1%–99%` allocation) so no employee is unintentionally left off a team.
2. **Over-Allocated Employees (`>100%`) & High Team Fragmentation (`>2` Teams) Check:** Flags individuals whose combined active team allocations exceed `100%` or who are fragmented across more than 2 concurrent active teams (high context-switching risk).
3. **Active Workday Leavers & Leadless Teams Check:** Detects `Inactive` Workday employees still occupying active Team memberships or Outcome contributor slots, and flags active Teams that are missing a designated `Team Lead` (or whose `Team Lead` is inactive in Workday).
4. **Outcome Coverage Gaps Check:** Flags active (`Active` / `At Risk`) Outcomes that have zero committed Teams or zero active Contributors assigned.

## 4. Workday Integration Boundary (`WorkdayDirectoryPort`)
- **Hexagonal Port:** All Workday interactions are isolated behind `WorkdayDirectoryPort`.
- **Adapters:**
  1. `MockWorkdayAdapter`: Seeded with realistic hierarchical organisation fixtures for local development and automated testing (including unallocated, under-allocated, over-allocated, fragmented, and inactive leaver scenarios for diagnostic verification).
  2. `WorkdayRaaSAdapter`: REST/JSON Report-as-a-Service (RaaS) / Workday HCM API client with idempotent upserts and soft-deactivation for leavers.
- **Sync Triggers:** Supports on-demand Admin triggers (API/UI button) and configurable scheduled background synchronization, with environment/config-based adapter switching.

## 5. User Experience (Glassmorphic Design), ATDD Living Documentation & Cloud-Native GCP Deployment
- **Glassmorphic Look & Feel:** The React SPA features a cohesive **Glassmorphic UI** aesthetic—combining frosted translucent panels (`backdrop-blur`), multi-layered depth over ambient mesh gradients, crisp luminous `1px` specular borders, and high-contrast accessible status badges for operational clarity.
- **Human-in-the-Loop & Specification by Example (ATDD) HTML Living Documentation:**
  - Delivery is organized into small, focused vertical-slice tracks with two explicit **Human-in-the-Loop (HITL)** checkpoints per track: (1) reviewing and approving executable Gherkin **Specification by Example** scenarios before implementation, and (2) reviewing the generated **HTML Living Documentation** reports (`Cucumber/Masterthought/Serenity HTML` on the backend and `Playwright/Vitest BDD HTML` on the frontend) alongside the running feature at track completion.
- **Google Cloud Platform (GCP) & Automated Continuous Push:** Built to run on **Google Cloud Run** (containerized backend and frontend services) backed by **Cloud SQL for PostgreSQL** and **Google Artifact Registry**, with automated CI/CD pipelines (**Google Cloud Build** `cloudbuild.yaml` and **GitHub Actions** via Workload Identity Federation) that run single-run unit and ATDD suites, publish the HTML Living Documentation, and automatically push/deploy on commit.
