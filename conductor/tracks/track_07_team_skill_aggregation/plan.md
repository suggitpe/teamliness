# Implementation Plan: Track 07 — Team-Level Skill Aggregation & Category Maturity Heatmap (`track_07_team_skill_aggregation`)

## Phase 1: Specification by Example Authoring & Team Skill Rollup Slice
- [ ] Task: Author Track 07 Gherkin Specifications by Example & Complete HITL Gate 1 Sign-Off
  - [ ] Draft `backend/src/test/resources/features/track_07_team_skill_aggregation.feature` and `frontend/src/features/track_07_team_skill_heatmap.feature` with member skill rollup example tables
  - [ ] **HITL Gate 1:** Present the Gherkin Specification by Example scenarios to the user and obtain explicit approval before implementation
- [ ] Task: Implement `TeamSkillAggregationService`, Rollup Endpoints & Glassmorphic Team Skill Category Heatmap (ATDD + TDD)
  - [ ] Wire failing Cucumber step definitions and unit tests for `/api/v1/teams/{teamId}/skills-summary` and `/api/v1/skills/team-matrix`
  - [ ] Implement backend `TeamSkillAggregationService` and frontend toggleable Team-Level Skill Summary & Category Heatmap View
- [ ] Task: Track 07 Verification, HTML Living Docs Inspection & HITL Gate 2 Sign-Off (Refer to workflow.md)
