# Product Definition: Teamliness — Organisational Team & Outcome Management

## 1. Product Vision & Summary
**Teamliness** is an Organisational Team & Outcome Management platform designed for engineering and operational leaders. It bridges the gap between HR system-of-record data (sourced from **Workday**), internal capability tracking (validated skills and standardized team roles), cross-functional team composition with capacity tracking, and high-level outcome commitments aligned with external work execution in **Jira**. Teamliness serves as the canonical system of record for team topology, exposing a dedicated read API so Jira and other external systems can fetch the authoritative list of teams, while linking Teams and Outcomes back to Jira Projects, Boards, Epics, and JQL filters.

## 2. Target Personas & Role-Based Access Control (RBAC)
Role-based personas are enforced across both the REST API and React SPA:
- **Admin:** Full system privileges, including triggering and configuring Workday directory synchronization, managing the standardized Skill & Role Catalog, managing the Organisational Unit hierarchy, and administering user/persona roles.
- **Org Leader:** Creates and structures Teams within Hierarchical Org Units (e.g., Division -> Tribe/Department -> Team), assigns Team Leads, oversees cross-team capacity allocations (including over-allocation and leaver warnings), assigns validated skills, and defines/commits Teams to Outcomes.
- **Team Lead:** Manages memberships, role assignments, direct skill proficiency assignments, leaver offboarding/reassignment actions, and outcome commitments/contributor mappings for their assigned Team(s).
- **Read-Only Viewer (& External Jira Integration Consumer):** Views team topologies, skill matrices, capacity allocations, and outcome commitments without mutation rights; external systems (such as Jira) query the dedicated read-only Team Directory API.

## 3. Core Domain Entities & Capabilities

### 3.1 Person (Workday-Sourced System of Record)
- **Attributes:** External Workday Employee ID (`employee_id`), full name, email, job title, department, cost centre, manager hierarchy reference, and employment status (`Active` / `Inactive`).
- **Read-Only Projection:** Core HR fields are maintained as a local read-only projection updated exclusively via the `WorkdayDirectoryPort` boundary.
- **Validated Skill Tagging:** Enriched locally with skill tags and proficiency levels (`Beginner`, `Intermediate`, `Expert`). Skills are assigned directly by authorised leaders (`Team Lead`, `Org Leader`, `Admin`) and are treated as validated upon assignment without requiring a separate multi-step approval queue.
- **Leaver Handling & One-Click Offboarding:** When an employee is marked `Inactive` or omitted in a Workday sync, they are soft-deactivated locally (`Inactive`). Any active Team memberships or Outcome contributor mappings for that person are automatically flagged with a **"Workday Inactive / Leaver"** warning badge, accompanied by a one-click action for Team Leads/Org Leaders to close (set `end_date`) or reassign their memberships while preserving historical records.

### 3.2 Skill & Role Catalog
- **Standardized Roles:** Curated catalog of team roles (e.g., `Tech Lead`, `Product Owner`, `Software Engineer`, `Platform Engineer`, `Delivery Manager`) used when assigning members to teams.
- **Skills & Proficiency Levels:** Standardized skill taxonomy rated across three explicit proficiency tiers: `Beginner`, `Intermediate`, and `Expert`.

### 3.3 Organisational Hierarchy, Teams & Capacity Allocation
- **Hierarchical Org Units:** Supports a tree of Organisational Units (`Division -> Tribe / Department -> Team`) that can align with Workday departments while allowing cross-functional teams to include members from multiple Workday departments and cost centres.
- **Team Attributes:** Name, mission statement, parent Org Unit, designated Team Lead (`Person`), optional **Jira Project Key / Board URL**, and time-bounded memberships (`start_date`, optional `end_date`).
- **Soft Over-Allocation Warning (>100%):** Each membership pairs a `Person` + `Role` + `allocation_percentage`. When a person's combined allocation across overlapping active team memberships exceeds `100%`, the assignment is permitted with a **soft warning** — the domain layer calculates total active allocation and sets an explicit `is_over_allocated` flag surfaced as an alert badge in the UI and API.
- **Dedicated Jira Team Directory Endpoint:** Exposes a stable, read-only REST endpoint returning active teams, their hierarchy, leads, members, and Jira Project Key mappings so Jira can consume and synchronize the canonical list of teams.

### 3.4 Outcome Commitments & External Jira Alignment
- **Measurable Outcomes:** High-level targets (`title`, `description`, `target_date`, `status`, `key_results`) committed to by one or more Teams, with explicit contributor mappings (`Person` + contribution role/notes).
- **Jira Work Execution Linkage:** Day-to-day task assignment and sprint execution reside in **Jira** (where work is allocated to Teams). Each Outcome and Key Result in Teamliness can store an optional **Jira Epic Key / Issue URL / JQL Query link**, enabling leaders to jump directly from high-level outcome commitments and team capacity in Teamliness to granular work items in Jira.

## 4. Workday Integration Boundary (`WorkdayDirectoryPort`)
- **Hexagonal Port:** All Workday interactions are isolated behind `WorkdayDirectoryPort`.
- **Adapters:**
  1. `MockWorkdayAdapter`: Seeded with realistic hierarchical organisation fixtures for local development and automated testing.
  2. `WorkdayRaaSAdapter`: REST/JSON Report-as-a-Service (RaaS) / Workday HCM API client with idempotent upserts and soft-deactivation for leavers.
- **Sync Triggers:** Supports on-demand Admin triggers (API/UI button) and configurable scheduled background synchronization, with environment/config-based adapter switching.
