Feature: Track 03 — Glassmorphic Persona Switcher & Individual Validated Skill Matrix
  As an Engineering Leader or Team Lead
  I want to inspect the Individual Skill Matrix across standardized capability categories and directly assign validated skill proficiencies (Beginner, Intermediate, Expert)
  So that employee capabilities are immediately visible and protected by persona-aware UI controls.

  Rule: The Individual Skill Matrix renders standardized skill proficiencies and enforces persona-aware mutation controls

    Scenario Outline: Persona-aware skill assignment controls reflect the active RBAC persona
      Given a leader opens the Teamliness SPA workspace and navigates to "Skill Matrix"
      When the leader switches the active RBAC persona to "<persona_name>"
      Then the direct skill assignment action button is "<control_state>"

      Examples:
        | persona_name     | control_state |
        | Admin            | enabled       |
        | Org Leader       | enabled       |
        | Team Lead        | enabled       |
        | Read-Only Viewer | disabled      |

    Scenario: Authorized leader assigns a validated Expert skill proficiency in the Individual Skill Matrix
      Given a leader opens the Teamliness SPA workspace and navigates to "Skill Matrix"
      When the leader with "Org Leader" persona assigns "Java 21 & Spring Boot" at "Expert" proficiency to "Priya Nair"
      Then the Individual Skill Matrix row for "Priya Nair" displays the validated badge "Java 21 & Spring Boot: Expert"
