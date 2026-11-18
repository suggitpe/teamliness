# Implementation Plan: Track 03 — RBAC Personas, Skill & Role Catalog & Direct Validated Skill Tagging (`track_03_rbac_skill_role_catalog`)

## Phase 1: Specification by Example Authoring & RBAC + Skill Catalog Slice
- [ ] Task: Author Track 03 Gherkin Specifications by Example & Complete HITL Gate 1 Sign-Off
  - [ ] Draft `backend/src/test/resources/features/track_03_rbac_and_skills.feature` and `frontend/src/features/track_03_persona_and_skill_matrix.feature` with RBAC permission decision tables
  - [ ] **HITL Gate 1:** Present the Gherkin Specification by Example scenarios to the user and obtain explicit approval before implementation
- [ ] Task: Implement RBAC Security Filter, Role/Skill Catalogs, Direct Skill Assignment & Glassmorphic Individual Skill Matrix (ATDD + TDD)
  - [ ] Wire failing Cucumber step definitions and unit tests for RBAC persona headers (`X-Persona-Role`, `X-Person-Id`), catalog CRUD, and direct validated skill assignment
  - [ ] Implement backend RBAC filter, Catalog & PersonSkill services/controllers, and Frontend Persona Switcher + Glassmorphic Individual Skill Matrix View
- [ ] Task: Track 03 Verification, HTML Living Docs Inspection & HITL Gate 2 Sign-Off (Refer to workflow.md)
