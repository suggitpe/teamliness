# Track Specification: Track 07 — Team-Level Skill Aggregation & Category Maturity Heatmap

## 1. Overview & Small-Scope Goal
Aggregate individual active members' validated skills up to the **Team** level across both individual skills and high-level categories (`GET /api/v1/teams/{teamId}/skills-summary` & `GET /api/v1/skills/team-matrix`), surfacing member coverage count (`3/5 members`), `maxProficiency`, `averageProficiencyScore`, and bench-depth / skill-growth indicators in a toggleable Glassmorphic **Team Skill Rollup & Category Heatmap**.

## 2. Specification by Example (ATDD) Scenarios
- **Feature:** `team_level_skill_aggregation.feature`
  - **Scenario Outline:** Rolling up active team members' validated skills into Team Skill Summary & Category Maturity
    - *Given* a team with active members holding validated skills (`Examples:` table of member proficiencies across `Backend`, `Frontend`, `Cloud & DevOps`)
    - *When* a leader requests the Team Skill Summary or cross-team Skill Matrix
    - *Then* the system computes the exact `membersWithSkillCount`, `coveragePercentage`, `maxProficiency`, `averageProficiencyScore`, and highlights whether the team has strong delivery depth or a skill-growth opportunity

## 3. Acceptance Criteria & Living Documentation Output
1. All team skill aggregation Gherkin example tables pass and render in the HTML Living Documentation report.
2. Two-Stage HITL gates completed.
