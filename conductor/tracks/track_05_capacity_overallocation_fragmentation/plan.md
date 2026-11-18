# Implementation Plan: Track 05 — Capacity Allocation Rules (`>100%` Soft Over-Allocation & Multi-Team Fragmentation) (`track_05_capacity_overallocation_fragmentation`)

## Phase 1: Specification by Example Authoring & Capacity Calculator Slice
- [ ] Task: Author Track 05 Gherkin Specifications by Example & Complete HITL Gate 1 Sign-Off
  - [ ] Draft `backend/src/test/resources/features/track_05_capacity_allocation.feature` and `frontend/src/features/track_05_overallocation_badges.feature` with date-overlap and percentage example tables
  - [ ] **HITL Gate 1:** Present the Gherkin Specification by Example scenarios to the user and obtain explicit approval before implementation
- [ ] Task: Implement Overlapping Capacity Calculator (`>100%` Soft Warning & `>2` Team Fragmentation) and Glassmorphic Amber Breakdown Popovers (ATDD + TDD)
  - [ ] Wire failing Cucumber step definitions and unit tests for overlapping date ranges, `>100%` soft warnings, and `>2` team fragmentation flags
  - [ ] Implement domain capacity calculation service and Glassmorphic Amber Warning Badges + breakdown tooltips in the Team Composition View
- [ ] Task: Track 05 Verification, HTML Living Docs Inspection & HITL Gate 2 Sign-Off (Refer to workflow.md)
