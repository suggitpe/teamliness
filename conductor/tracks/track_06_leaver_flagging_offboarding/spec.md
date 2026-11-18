# Track Specification: Track 06 — Inactive Workday Leaver Flagging & One-Click Membership Offboarding

## 1. Overview & Small-Scope Goal
Automatically flag any active Team membership where the employee has `INACTIVE` Workday status (`isWorkdayInactiveLeaver = true` with a Red/Rose `Workday Inactive / Leaver` badge), and provide a one-click offboarding/reassignment action (`POST /api/v1/teams/{teamId}/memberships/{membershipId}/offboard`) that closes the active membership (`endDate = today`) and optionally assigns a replacement while preserving historical records.

## 2. Specification by Example (ATDD) Scenarios
- **Feature:** `workday_leaver_flagging_and_offboarding.feature`
  - **Scenario:** Employee deactivated in Workday is flagged on active team roster and offboarded in one click
    - *Given* an active team member whose Workday status transitions to `INACTIVE`
    - *When* the team roster is viewed and the Team Lead triggers one-click offboarding (with optional replacement member)
    - *Then* the leaver's membership `endDate` is set to today, historical records are preserved, the active leaver warning clears, and the replacement member becomes active

## 3. Acceptance Criteria & Living Documentation Output
1. All leaver flagging and one-click offboarding Gherkin scenarios pass and render in the HTML Living Documentation report.
2. Two-Stage HITL gates completed.
