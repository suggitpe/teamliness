# Implementation Plan: Track 02 — Workday Directory Sync (`WorkdayDirectoryPort`) & Leaver Soft-Deactivation (`track_02_workday_sync_leavers`)

## Phase 1: Specification by Example Authoring & Workday Hexagonal Sync Implementation
- [x] Task: Author Track 02 Gherkin Specifications by Example & Complete HITL Gate 1 Sign-Off
  - [x] Draft `backend/src/test/resources/features/track_02_workday_sync.feature` and `frontend/src/features/track_02_workday_sync_ui.feature` with concrete employee upsert and leaver soft-deactivation example tables
  - [x] **HITL Gate 1:** Present the Gherkin Specification by Example scenarios to the user and obtain explicit approval before implementation
- [x] Task: Implement `WorkdayDirectoryPort`, `MockWorkdayAdapter`, `WorkdayRaaSAdapter`, and `WorkdaySyncService` (ATDD + TDD)
  - [x] Wire failing Cucumber step definitions and unit tests for `MockWorkdayAdapter`, `WorkdayRaaSAdapter` (`MockRestServiceServer`), idempotent upserts, and leaver soft-deactivation (`INACTIVE`)
  - [x] Implement `WorkdayDirectoryPort`, adapters, `WorkdaySyncService`, `POST /api/v1/workday/sync`, and `GET /api/v1/persons`
  - [x] Implement Glassmorphic Header Workday Sync status indicator and on-demand sync button in `frontend/`
- [x] Task: Track 02 Verification, HTML Living Docs Inspection & HITL Gate 2 Sign-Off (Refer to workflow.md)
