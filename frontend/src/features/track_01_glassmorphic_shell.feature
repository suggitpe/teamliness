Feature: Track 01 — Glassmorphic Operational Workspace Shell & Living Docs Navigation
  As an engineering or operational leader
  I want the Teamliness Single-Page Application to render a high-contrast Glassmorphic workspace shell with navigation and system status indicators
  So that I can navigate between Team Composition, Skill Matrix, Outcome Commitments, and Diagnostics with immediate situational awareness.

  Rule: The Glassmorphic application shell renders translucent surfaces, ambient mesh backdrop, and primary navigation

    Scenario: Glassmorphic workspace shell renders with navigation and living-docs link
      Given a leader opens the Teamliness SPA workspace
      Then the ambient radial mesh gradient canvas and Glassmorphic header with "backdrop-blur" styling are rendered
      And the active RBAC persona badge displays "Org Leader"
      And the Living Documentation status link is visible in the workspace header

    Scenario Outline: Primary workspace navigation items render with their operational view descriptions
      Given a leader opens the Teamliness SPA workspace
      When the leader selects the "<nav_label>" navigation tab
      Then the workspace main panel displays the "<section_heading>" view with glassmorphic surface elevation

      Examples:
        | nav_label            | section_heading                              |
        | Team Composition     | Hierarchical Org Units & Team Composition    |
        | Skill Matrix         | Individual & Team-Aggregated Skill Matrix    |
        | Outcome Commitments  | Outcome Commitments & Jira Execution Links   |
        | Health Diagnostics   | Organisational & Capacity Health Diagnostics |
