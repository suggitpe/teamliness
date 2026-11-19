Feature: Track 02 — Glassmorphic Workday Sync Bar & Read-Only System-of-Record Directory
  As an Admin or Org Leader
  I want to view the Workday System-of-Record synchronization status, inspect synced employee projections with read-only lock indicators, and trigger an on-demand Workday sync
  So that I have immediate visibility into active employees and soft-deactivated Workday leavers.

  Rule: The Glassmorphic header provides persona-aware on-demand Workday synchronization and audit feedback

    Scenario: Admin triggers an on-demand Workday Directory Sync from the Glassmorphic workspace header
      Given a leader opens the Teamliness SPA workspace
      When the leader switches the active RBAC persona to "Admin"
      And the Admin clicks the "Sync Workday Directory" button in the Glassmorphic header
      Then the Workday Sync status badge displays "Last Sync: 6 Upserted • 1 Leaver Deactivated (MOCK)"

  Rule: The Workday Employee Directory projection view displays read-only lock badges and semantic employment statuses

    Scenario Outline: Workday Employee Directory rows render read-only System-of-Record indicators and employment status badges
      Given a leader opens the Teamliness SPA workspace
      When the leader views the Workday Employee Directory projection panel
      Then the employee row for "<employee_id>" displays "<full_name>", "<employment_status_badge>", and the "Synced from Workday" read-only lock indicator

      Examples:
        | employee_id | full_name        | employment_status_badge     |
        | WD-1001     | Elena Rostova    | Active                      |
        | WD-1005     | Sofia Lindqvist  | Active                      |
        | WD-1099     | Arthur Pendelton | Workday Inactive / Leaver   |
