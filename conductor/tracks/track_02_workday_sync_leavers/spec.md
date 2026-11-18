# Track Specification: Track 02 — Workday Directory Sync (`WorkdayDirectoryPort`) & Leaver Soft-Deactivation

## 1. Overview & Small-Scope Goal
Implement the hexagonal `WorkdayDirectoryPort`, `MockWorkdayAdapter`, `WorkdayRaaSAdapter`, `WorkdaySyncService` (`POST /api/v1/workday/sync` & `GET /api/v1/persons`), and the Glassmorphic Header Workday Sync trigger/status bar.

## 2. Specification by Example (ATDD) Scenarios
- **Feature:** `workday_directory_sync_and_leavers.feature`
  - **Scenario Outline:** Idempotent Workday directory synchronization & leaver soft-deactivation
    - *Given* an initial Workday directory snapshot with employee records (`Examples:` table of active, updated, and omitted/terminated employees)
    - *When* the `WorkdaySyncService` executes a synchronization run
    - *Then* active employees are upserted without duplicates, locally enriched data is preserved, and omitted or terminated employees transition to `INACTIVE` status
  - **Scenario:** `WorkdayRaaSAdapter` parses Workday Report-as-a-Service JSON payload (`{"Report_Entry": [...]}`)

## 3. Acceptance Criteria & Living Documentation Output
1. All Gherkin examples in `workday_directory_sync_and_leavers.feature` pass and render as HTML Living Documentation in `backend/build/reports/living-docs/index.html` and `frontend/reports/living-docs/index.html`.
2. Two-Stage HITL gates completed (Gate 1: Gherkin examples approved before coding; Gate 2: HTML Living Documentation report & UI sync trigger verified).
