# Track Specification: Track 09 — Organisational & Capacity Health Diagnostics Suite

## 1. Overview & Small-Scope Goal
Implement the **Organisational & Capacity Health Diagnostics Engine** (`GET /api/v1/diagnostics`) and the **Glassmorphic Diagnostics Dashboard View** checking whether:
1. Every active Workday employee has a team allocation (`UNALLOCATED` at `0%` and `UNDER_ALLOCATED` at `1%–99%`).
2. Any employee is allocated to too many teams or over capacity (`OVER_ALLOCATED` at `>100%` and `HIGH_TEAM_FRAGMENTATION` at `>2` concurrent active teams).
3. Any `INACTIVE` Workday leaver remains on active teams/outcomes (`ACTIVE_LEAVER_ASSIGNMENT`) or any active team lacks an active `Team Lead` (`TEAM_MISSING_ACTIVE_LEAD`).
4. Any active Outcome lacks committed teams or active contributors (`OUTCOME_NO_COMMITTED_TEAMS`, `OUTCOME_NO_ACTIVE_CONTRIBUTORS`).

## 2. Specification by Example (ATDD) Scenarios
- **Feature:** `organisational_and_capacity_diagnostics.feature`
  - **Scenario Outline:** Running organisation-wide diagnostic checks across employees, teams, and outcomes
    - *Given* an organisation snapshot with specific capacity and topology conditions (`Examples:` table covering `0%` unallocated employees, `50%` under-allocated employees, `125%` over-allocated employees, `3-team` fragmented employees, active Workday leavers, leadless teams, and unstaffed outcomes)
    - *When* a user runs the Diagnostics check (`GET /api/v1/diagnostics`)
    - *Then* the diagnostic report returns the exact expected finding code, severity, affected entity, and actionable remediation link

## 3. Acceptance Criteria & Living Documentation Output
1. All diagnostic rule Gherkin example tables pass and render in the HTML Living Documentation report.
2. Two-Stage HITL gates completed.
