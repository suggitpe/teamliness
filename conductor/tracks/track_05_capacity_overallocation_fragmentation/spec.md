# Track Specification: Track 05 — Capacity Allocation Rules (`>100%` Soft Over-Allocation & Multi-Team Fragmentation)

## 1. Overview & Small-Scope Goal
Implement the overlapping date-range capacity calculator that computes each person's `totalAllocationPercentage`, flags `isOverAllocated = true` as a soft warning when total active allocation exceeds `100%`, tracks `activeTeamCount` (flagging fragmentation when `>2` concurrent active teams), and renders high-contrast Amber warning badges with glassmorphic breakdown popovers.

## 2. Specification by Example (ATDD) Scenarios
- **Feature:** `capacity_overallocation_and_fragmentation.feature`
  - **Scenario Outline:** Overlapping active team memberships calculate total allocation, soft over-allocation warning (`>100%`), and team fragmentation (`>2` teams)
    - *Given* an employee with existing active team memberships (`Examples:` table covering `0%`, `50% + 50% = 100%`, `75% + 50% = 125%` over-allocated, `30% + 30% + 30% = 90%` across 3 teams fragmented, and non-overlapping historical memberships)
    - *When* a new membership is saved or queried
    - *Then* the assignment succeeds and returns the exact `totalAllocationPercentage`, `isOverAllocated`, `activeTeamCount`, `isFragmented`, and membership breakdown

## 3. Acceptance Criteria & Living Documentation Output
1. All capacity allocation and date-overlap example tables pass and render in the HTML Living Documentation report.
2. Two-Stage HITL gates completed.
