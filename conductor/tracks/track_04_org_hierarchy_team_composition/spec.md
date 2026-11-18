# Track Specification: Track 04 — Hierarchical Org Units & Glassmorphic Team Composition

## 1. Overview & Small-Scope Goal
Implement the hierarchical `OrgUnit` tree (`Division -> Tribe/Department -> Team`), Team creation/editing with Jira Project Key & Board URL links, time-bounded `TeamMembership` management, and the split-pane Glassmorphic Team Composition UI view.

## 2. Specification by Example (ATDD) Scenarios
- **Feature:** `org_hierarchy_and_team_composition.feature`
  - **Scenario:** Navigating the Division -> Tribe -> Team hierarchy and composing cross-functional team memberships
    - *Given* an Organisational Unit hierarchy (`Engineering Division -> Core Platform Tribe`)
    - *When* an `ORG_LEADER` or owning `TEAM_LEAD` assigns members from multiple Workday departments with roles and start/end dates
    - *Then* the Team Composition API and Glassmorphic split-pane view display the team mission, lead, Jira Project link, and active roster

## 3. Acceptance Criteria & Living Documentation Output
1. All Gherkin scenarios in `org_hierarchy_and_team_composition.feature` pass and render in the HTML Living Documentation report.
2. Two-Stage HITL gates completed.
