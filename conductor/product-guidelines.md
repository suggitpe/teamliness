# Product Guidelines: Teamliness

## 1. Brand Voice, Tone & Terminology
- **Tone:** Crisp, utilitarian enterprise tone optimized for engineering and operational leaders who need rapid situational awareness.
- **Unambiguous Domain Language:** Use consistent, canonical terms across UI labels, API payloads, and documentation:
  - **Person (Workday Record):** Always distinguish Workday-sourced fields (`Workday ID`, `Job Title`, `Department`, `Cost Centre`, `Manager`, `Employment Status`) from local enrichments (`Skills`, `Team Memberships`).
  - **Capacity Allocation:** Express as a percentage (`0%–100%+`), explicitly distinguishing `Total Active Allocation` from single-team `Membership Allocation`.
  - **External Work References:** Clearly label Jira pointers (`Jira Project Key`, `Jira Board`, `Jira Epic / JQL Link`) as external execution links.
- **Actionable Microcopy:** Error and warning messages must state the exact condition and the immediate remediation step (e.g., *"Total allocation is 125% across 2 active teams (Platform Core: 75%, Payments API: 50%). Allocation is saved with an over-allocation warning."*).

## 2. UX & Layout Architecture
- **High-Density Operational Workspace:**
  - **Team Composition View:** Split-pane hierarchy explorer (`Division -> Tribe/Department -> Team`) paired with compact, sortable member tables showing Role, Skill summary, Allocation %, Total Cross-Team Allocation %, Workday Status, and Jira Project link.
  - **Skill Matrix View:** Heatmap-style tabular grid cross-referencing People and Teams against the Standardized Skill Catalog (`Beginner`, `Intermediate`, `Expert`) to quickly surface capability gaps and single points of failure.
  - **Outcome Commitments View:** Structured board and table hybrid showing Outcomes, target dates, status, Key Results, committed Teams, explicit Contributors, and direct deep-links to Jira Epics/JQL filters.
- **Fast Contextual Editing:** Favour slide-over drawers and inline editing controls over multi-step page reloads for assigning roles, tagging skills, and adjusting capacity percentages.
- **Persona-Aware UI Controls:** Immediately reflect the active RBAC persona (`Admin`, `Org Leader`, `Team Lead`, `Read-Only Viewer`) in the workspace header, disabling or hiding unauthorized mutation controls with clear explanatory tooltips.

## 3. Visual Semantics & Operational Alert Badges
- **Over-Allocation Soft Warning (`> 100%`):**
  - Rendered with a high-contrast **Amber Warning Badge** (`Over-Allocated: 125%`).
  - Hovering or clicking displays a breakdown tooltip of all overlapping active team memberships contributing to the total.
- **Workday Leaver / Inactive Employee Alert:**
  - Rendered with a **Red / Rose Alert Badge** (`Workday Inactive / Leaver`) whenever an employee with `Inactive` Workday status remains on an active Team membership or Outcome contribution.
  - Accompanied by a one-click **"Close / Reassign Membership"** action button for `Team Lead`, `Org Leader`, and `Admin` personas.
- **System-of-Record Boundary Indicators:**
  - Workday-sourced fields display a subtle **Read-Only Lock Indicator** (`Synced from Workday`) alongside the last sync timestamp so users understand why HR core attributes cannot be hand-edited locally.
