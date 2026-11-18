# Implementation Plan: Track 09 — Organisational & Capacity Health Diagnostics Suite (`track_09_org_capacity_diagnostics`)

## Phase 1: Specification by Example Authoring & Diagnostics Suite Slice
- [ ] Task: Author Track 09 Gherkin Specifications by Example & Complete HITL Gate 1 Sign-Off
  - [ ] Draft `backend/src/test/resources/features/track_09_org_diagnostics.feature` and `frontend/src/features/track_09_diagnostics_dashboard.feature` with diagnostic rule decision tables
  - [ ] **HITL Gate 1:** Present the Gherkin Specification by Example scenarios to the user and obtain explicit approval before implementation
- [ ] Task: Implement `DiagnosticsService` (`GET /api/v1/diagnostics`) & Glassmorphic Diagnostics Dashboard View (ATDD + TDD)
  - [ ] Wire failing Cucumber step definitions and unit tests for all four diagnostic check categories (`0%`/`<100%` allocation, `>100%`/`>2` teams fragmentation, active leavers & leadless teams, and outcome coverage gaps)
  - [ ] Implement backend `DiagnosticsService` / `DiagnosticsController` and frontend Glassmorphic Diagnostics Dashboard View + Header alert count badge
- [ ] Task: Track 09 Verification, HTML Living Docs Inspection & HITL Gate 2 Sign-Off (Refer to workflow.md)
