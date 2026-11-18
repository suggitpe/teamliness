# Product Guidelines: Teamliness

## 1. Brand Voice, Tone & Terminology
- **Tone:** Crisp, utilitarian enterprise tone optimized for engineering and operational leaders who need rapid situational awareness.
- **Unambiguous Domain Language:** Use consistent, canonical terms across UI labels, API payloads, and documentation:
  - **Person (Workday Record):** Always distinguish Workday-sourced fields (`Workday ID`, `Job Title`, `Department`, `Cost Centre`, `Manager`, `Employment Status`) from local enrichments (`Skills`, `Team Memberships`).
  - **Capacity Allocation:** Express as a percentage (`0%–100%+`), explicitly distinguishing `Total Active Allocation` from single-team `Membership Allocation` and surfacing `Concurrent Team Count`.
  - **Team Skill Summary:** Express aggregated team capability via `Member Coverage (e.g., 3/5 members)`, `Max Proficiency`, `Average Proficiency`, and `Category Maturity`.
  - **External Work References:** Clearly label Jira pointers (`Jira Project Key`, `Jira Board`, `Jira Epic / JQL Link`) as external execution links.
- **Actionable Microcopy:** Error, warning, and diagnostic messages must state the exact condition and the immediate remediation step (e.g., *"Total allocation is 125% across 2 active teams (Platform Core: 75%, Payments API: 50%). Allocation is saved with an over-allocation warning."* or *"Diagnostic: 2 active employees have 0% team allocation — click 'Assign to Team' to allocate capacity."*).

## 2. Glassmorphic Design System & Visual Language
- **Frosted Glass Surfaces (`Glassmorphism`):**
  - Primary workspace cards, navigation headers, hierarchy sidebars, and slide-over drawers use translucent frosted-glass surfaces (`bg-white/10` or `bg-slate-900/60` with `backdrop-blur-md` to `backdrop-blur-xl`) layered over a rich, subtle ambient gradient canvas (`radial-gradient` mesh of deep indigo, slate, and cyan accents).
  - **Specular Borders & Elevation:** Every glass panel is framed by a crisp `1px` semi-translucent luminous border (`border-white/15` or `border-white/20`) and soft ambient drop shadows (`shadow-xl shadow-black/20`) to create clear visual hierarchy without heavy opaque dividers.
- **High-Contrast Legibility & Accessibility:**
  - Data tables, skill heatmaps, and diagnostic cards maintain WCAG AA text contrast over glass surfaces by using slightly higher surface opacity (`bg-slate-900/75` or `bg-white/80` in light mode) inside dense data rows while keeping headers, toolbars, modals, and summary KPI tiles richly glassmorphic.

## 3. UX & Layout Architecture
- **High-Density Glassmorphic Operational Workspace:**
  - **Team Composition View:** Split-pane glassmorphic hierarchy explorer (`Division -> Tribe/Department -> Team`) paired with compact, sortable member tables showing Role, Skill summary, Allocation %, Total Cross-Team Allocation %, Concurrent Team count, Workday Status, and Jira Project link.
  - **Skill Matrix & Team Skill Rollup View:** Dual-mode heatmap grid:
    1. **Individual Skill Matrix:** Cross-references People against the Standardized Skill Catalog (`Beginner`, `Intermediate`, `Expert`).
    2. **Team-Level Skill Summary & Category Heatmap:** Aggregates active members' skills per Team across high-level categories and individual skills—displaying coverage ratios (`3/6 members`), peak/average proficiency, and bench depth so leaders can select teams for Outcomes based on either existing expertise or intentional **skill growth**.
  - **Outcome Commitments View:** Structured glassmorphic board and table hybrid showing Outcomes, target dates, status, Key Results, committed Teams (with side-by-side Team Skill Summary inspection during allocation), explicit Contributors, and direct deep-links to Jira Epics/JQL filters.
  - **Organisational & Capacity Diagnostics View:** Dedicated diagnostic command center displaying real-time health scorecards and filterable issue lists across four diagnostic pillars:
    1. **Unallocated (`0%`) & Under-Allocated (`<100%`) Employees**
    2. **Over-Allocated (`>100%`) & Multi-Team Fragmented (`>2` Teams) Employees**
    3. **Active Workday Leavers & Teams Missing a Team Lead**
    4. **Outcome Coverage Gaps (Active Outcomes with no committed Teams or Contributors)**
- **Fast Contextual Editing:** Favour glassmorphic slide-over drawers and inline editing controls over multi-step page reloads for assigning roles, tagging skills, adjusting capacity percentages, and resolving diagnostic findings.
- **Persona-Aware UI Controls:** Immediately reflect the active RBAC persona (`Admin`, `Org Leader`, `Team Lead`, `Read-Only Viewer`) in the glassmorphic workspace header, disabling or hiding unauthorized mutation controls with clear explanatory tooltips.

## 4. Visual Semantics & Operational Alert Badges
- **Over-Allocation (`> 100%`) & Fragmentation (`> 2 Teams`) Soft Warnings:**
  - Rendered with a high-contrast **Amber Warning Badge** (`Over-Allocated: 125%`, `Fragmented: 3 Teams`).
  - Hovering or clicking displays a glassmorphic popover breakdown of all overlapping active team memberships contributing to the total.
- **Unallocated (`0%`) & Under-Allocated (`< 100%`) Diagnostic Badges:**
  - Rendered with a **Cyan / Sky Info-Warning Badge** (`Unallocated: 0%`, `Under-Allocated: 50%`) in the Diagnostics View and Person directory with a quick action to assign the employee to a team.
- **Workday Leaver / Inactive Employee Alert:**
  - Rendered with a **Red / Rose Alert Badge** (`Workday Inactive / Leaver`) whenever an employee with `Inactive` Workday status remains on an active Team membership or Outcome contribution.
  - Accompanied by a one-click **"Close / Reassign Membership"** action button for `Team Lead`, `Org Leader`, and `Admin` personas.
- **System-of-Record Boundary Indicators:**
  - Workday-sourced fields display a subtle **Read-Only Lock Indicator** (`Synced from Workday`) alongside the last sync timestamp so users understand why HR core attributes cannot be hand-edited locally.
