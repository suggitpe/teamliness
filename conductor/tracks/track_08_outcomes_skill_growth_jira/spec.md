# Track Specification: Track 08 — Outcome Commitments, Skill-Growth Team Allocation & Jira Canonical Directory API

## 1. Overview & Small-Scope Goal
Implement Outcomes, Key Results, Contributor mappings, the Glassmorphic Outcome Commitments View with inline **Team Skill Summary inspection** during team allocation (so leaders can choose teams either for existing capability or to **intentionally grow skills in the team**), Jira Epic/JQL deep-links, and the read-only **Jira Canonical Team Directory API** (`GET /api/v1/integrations/jira/teams`).

## 2. Specification by Example (ATDD) Scenarios
- **Feature:** `outcomes_skill_growth_allocation_and_jira_api.feature`
  - **Scenario:** Allocating an Outcome to a Team for intentional skill growth vs capability delivery
    - *Given* an active Outcome and the Team-Level Skill Summary for candidate teams
    - *When* an `ORG_LEADER` allocates the Outcome to a team and records `allocationIntent = "SKILL_GROWTH"` (or `"CAPABILITY_MATCH"`) with rationale notes and Jira Epic/JQL links
    - *Then* the Outcome Commitment records the committed team, allocation intent, contributors, and Jira links
  - **Scenario:** Jira queries the Canonical Team Directory API (`GET /api/v1/integrations/jira/teams?jiraProjectKey=...`)

## 3. Acceptance Criteria & Living Documentation Output
1. All Outcome allocation and Jira Directory Gherkin scenarios pass and render in the HTML Living Documentation report.
2. Two-Stage HITL gates completed.
