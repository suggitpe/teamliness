# Implementation Plan: Track 04 — Hierarchical Org Units & Glassmorphic Team Composition (`track_04_org_hierarchy_team_composition`)

## Phase 1: Specification by Example Authoring & Org Hierarchy / Team Composition Slice
- [ ] Task: Author Track 04 Gherkin Specifications by Example & Complete HITL Gate 1 Sign-Off
  - [ ] Draft `backend/src/test/resources/features/track_04_org_hierarchy_and_teams.feature` and `frontend/src/features/track_04_team_composition_view.feature`
  - [ ] **HITL Gate 1:** Present the Gherkin Specification by Example scenarios to the user and obtain explicit approval before implementation
- [ ] Task: Implement Org Unit Tree, Team & Membership Endpoints, and Split-Pane Glassmorphic Team Composition View (ATDD + TDD)
  - [ ] Wire failing Cucumber step definitions and unit tests for `/api/v1/org-units` and `/api/v1/teams` (including `TEAM_LEAD` ownership checks)
  - [ ] Implement backend OrgUnit & Team services/controllers and the frontend Glassmorphic Split-Pane Team Composition View
- [ ] Task: Track 04 Verification, HTML Living Docs Inspection & HITL Gate 2 Sign-Off (Refer to workflow.md)
