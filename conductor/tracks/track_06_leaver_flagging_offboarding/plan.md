# Implementation Plan: Track 06 — Inactive Workday Leaver Flagging & One-Click Membership Offboarding (`track_06_leaver_flagging_offboarding`)

## Phase 1: Specification by Example Authoring & Leaver Offboarding Slice
- [ ] Task: Author Track 06 Gherkin Specifications by Example & Complete HITL Gate 1 Sign-Off
  - [ ] Draft `backend/src/test/resources/features/track_06_leaver_offboarding.feature` and `frontend/src/features/track_06_leaver_badge_and_offboard.feature`
  - [ ] **HITL Gate 1:** Present the Gherkin Specification by Example scenarios to the user and obtain explicit approval before implementation
- [ ] Task: Implement Leaver Warning Detection, One-Click Offboarding API & Glassmorphic Offboard/Reassign Drawer (ATDD + TDD)
  - [ ] Wire failing Cucumber step definitions and unit tests for `isWorkdayInactiveLeaver` detection and `POST /api/v1/teams/{teamId}/memberships/{membershipId}/offboard`
  - [ ] Implement backend offboarding/reassignment logic and frontend Red/Rose `Workday Inactive / Leaver` badge + one-click Offboard/Reassign drawer
- [ ] Task: Track 06 Verification, HTML Living Docs Inspection & HITL Gate 2 Sign-Off (Refer to workflow.md)
