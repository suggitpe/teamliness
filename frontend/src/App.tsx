import React, { useState } from 'react';
import {
  Activity,
  Award,
  BookOpenCheck,
  Building2,
  ExternalLink,
  Lock,
  ShieldCheck,
  Target,
  Users,
} from 'lucide-react';

export type WorkspaceSectionId =
  | 'team-composition'
  | 'skill-matrix'
  | 'outcome-commitments'
  | 'health-diagnostics';

export interface WorkspaceNavConfig {
  id: WorkspaceSectionId;
  navLabel: string;
  sectionHeading: string;
  subtitle: string;
  highlights: string[];
}

export const WORKSPACE_SECTIONS: WorkspaceNavConfig[] = [
  {
    id: 'team-composition',
    navLabel: 'Team Composition',
    sectionHeading: 'Hierarchical Org Units & Team Composition',
    subtitle:
      'Explore Division → Tribe/Department → Team hierarchy, time-bounded memberships, cross-team allocation %, and Workday system-of-record attributes.',
    highlights: [
      'Core Engineering & Platform Division → Cloud & Platform Foundations Tribe',
      'Soft Over-Allocation Warning (>100%) & Concurrent Team Count indicators',
      'Workday Read-Only Projection Lock with one-click Leaver offboarding',
    ],
  },
  {
    id: 'skill-matrix',
    navLabel: 'Skill Matrix',
    sectionHeading: 'Individual & Team-Aggregated Skill Matrix',
    subtitle:
      'Inspect validated skill proficiencies (Beginner, Intermediate, Expert) and Team-Level Category Maturity Heatmaps for capability-led or skill-growth allocation.',
    highlights: [
      'Direct validated skill tagging by Team Leads, Org Leaders, and Admins',
      'Team Skill Rollup: Member Coverage, Max Proficiency, Weighted Average & Bench Depth',
      'Side-by-side team capability comparison across Backend, Frontend, Cloud & DevOps',
    ],
  },
  {
    id: 'outcome-commitments',
    navLabel: 'Outcome Commitments',
    sectionHeading: 'Outcome Commitments & Jira Execution Links',
    subtitle:
      'Align measurable Outcomes and Key Results to committed Teams and Contributors while linking execution directly to Jira Projects, Epics, and JQL filters.',
    highlights: [
      'Allocate teams for Proven Core Capability Delivery or Intentional Skill Growth',
      'Inline Team Skill Heatmap context during Outcome-to-Team commitment',
      'Canonical Read-Only Jira Team Directory API (/api/v1/integrations/jira/teams)',
    ],
  },
  {
    id: 'health-diagnostics',
    navLabel: 'Health Diagnostics',
    sectionHeading: 'Organisational & Capacity Health Diagnostics',
    subtitle:
      'Real-time diagnostic rules evaluating capacity gaps, multi-team fragmentation, inactive Workday leavers, leadless teams, and outcome coverage.',
    highlights: [
      'Unallocated (0%) & Under-Allocated (<100%) active Workday employees check',
      'Over-Allocated (>100%) & Multi-Team Fragmented (>2 concurrent teams) check',
      'Active Workday Leavers on teams/outcomes & Outcome Coverage Gaps check',
    ],
  },
];

export const RBAC_PERSONAS = ['Admin', 'Org Leader', 'Team Lead', 'Read-Only Viewer'] as const;
export type RbacPersona = (typeof RBAC_PERSONAS)[number];

/**
 * Glassmorphic Operational Workspace Shell for Teamliness.
 */
export function App(): React.ReactElement {
  const [activeSectionId, setActiveSectionId] = useState<WorkspaceSectionId>('team-composition');
  const [activePersona, setActivePersona] = useState<RbacPersona>('Org Leader');

  const currentSection =
    WORKSPACE_SECTIONS.find((section) => section.id === activeSectionId) ?? WORKSPACE_SECTIONS[0];

  return (
    <div
      data-testid="glassmorphic-mesh-canvas"
      className="glass-mesh-canvas min-h-screen text-slate-100 flex flex-col"
    >
      {/* Glassmorphic Top Workspace Header */}
      <header
        data-testid="glassmorphic-workspace-header"
        className="sticky top-0 z-30 bg-slate-900/55 backdrop-blur-xl border-b border-white/15 shadow-xl shadow-black/20"
      >
        <div className="max-w-7xl mx-auto px-6 py-4 flex flex-wrap items-center justify-between gap-4">
          <div className="flex items-center gap-3">
            <div className="h-10 w-10 rounded-xl bg-cyan-500/20 border border-cyan-400/30 flex items-center justify-center text-cyan-300 shadow-inner">
              <Building2 className="h-5 w-5" />
            </div>
            <div>
              <div className="flex items-center gap-2">
                <span className="text-lg font-bold tracking-tight text-white">Teamliness</span>
                <span className="text-xs font-medium px-2 py-0.5 rounded-full bg-white/10 border border-white/15 text-cyan-200">
                  Glassmorphic Workspace
                </span>
              </div>
              <p className="text-xs text-slate-300">
                Organisational Team Topology, Skill Rollups, Capacity Diagnostics &amp; Jira Alignment
              </p>
            </div>
          </div>

          {/* Status & Persona Bar */}
          <div className="flex flex-wrap items-center gap-3">
            <a
              data-testid="living-docs-status-link"
              href="/reports/living-docs/index.html"
              className="inline-flex items-center gap-1.5 px-3 py-1.5 rounded-lg bg-emerald-500/15 border border-emerald-400/30 text-emerald-200 text-xs font-medium hover:bg-emerald-500/25 transition"
            >
              <BookOpenCheck className="h-3.5 w-3.5" />
              <span>ATDD Living Docs: Active</span>
              <ExternalLink className="h-3 w-3 opacity-80" />
            </a>

            <div className="inline-flex items-center gap-1.5 px-3 py-1.5 rounded-lg bg-white/10 border border-white/15 text-xs text-slate-200">
              <Lock className="h-3.5 w-3.5 text-sky-300" />
              <span>Workday SoR: Connected (Schema v2)</span>
            </div>

            <div className="flex items-center gap-2 bg-slate-900/70 border border-white/15 rounded-lg px-3 py-1.5">
              <ShieldCheck className="h-4 w-4 text-indigo-300" />
              <span className="text-xs text-slate-400">Persona:</span>
              <span
                data-testid="active-rbac-persona-badge"
                className="text-xs font-semibold text-indigo-200"
              >
                {activePersona}
              </span>
              <select
                aria-label="Switch RBAC Persona"
                value={activePersona}
                onChange={(e) => setActivePersona(e.target.value as RbacPersona)}
                className="ml-1 bg-slate-800/90 text-xs text-slate-200 rounded border border-white/15 px-1.5 py-0.5"
              >
                {RBAC_PERSONAS.map((persona) => (
                  <option key={persona} value={persona}>
                    {persona}
                  </option>
                ))}
              </select>
            </div>
          </div>
        </div>

        {/* Glassmorphic Navigation Tabs */}
        <nav
          aria-label="Primary Workspace Navigation"
          className="max-w-7xl mx-auto px-6 pb-3 flex flex-wrap gap-2"
        >
          {WORKSPACE_SECTIONS.map((section) => {
            const isActive = section.id === activeSectionId;
            return (
              <button
                key={section.id}
                type="button"
                onClick={() => setActiveSectionId(section.id)}
                className={`px-4 py-2 rounded-xl text-sm font-medium transition flex items-center gap-2 border ${
                  isActive
                    ? 'bg-white/15 backdrop-blur-md border-cyan-400/40 text-white shadow-lg shadow-cyan-950/30'
                    : 'bg-slate-900/40 border-white/10 text-slate-300 hover:bg-white/10 hover:text-white'
                }`}
              >
                {section.id === 'team-composition' && <Users className="h-4 w-4 text-cyan-300" />}
                {section.id === 'skill-matrix' && <Award className="h-4 w-4 text-indigo-300" />}
                {section.id === 'outcome-commitments' && <Target className="h-4 w-4 text-emerald-300" />}
                {section.id === 'health-diagnostics' && <Activity className="h-4 w-4 text-amber-300" />}
                <span>{section.navLabel}</span>
              </button>
            );
          })}
        </nav>
      </header>

      {/* Main Workspace Container */}
      <main className="max-w-7xl w-full mx-auto px-6 py-8 flex-1 flex flex-col gap-6">
        {/* Operational Semantic Badge Legend Strip */}
        <section
          aria-label="Operational Alert Semantics"
          className="grid grid-cols-1 md:grid-cols-4 gap-4"
        >
          <div className="bg-white/10 backdrop-blur-md border border-white/15 rounded-xl p-4">
            <span className="inline-block text-xs font-semibold px-2.5 py-0.5 rounded-full bg-amber-500/20 border border-amber-400/40 text-amber-200">
              Over-Allocated: 125% • Fragmented: 3 Teams
            </span>
            <p className="mt-2 text-xs text-slate-300">
              Soft warning when combined active team memberships exceed 100% capacity.
            </p>
          </div>

          <div className="bg-white/10 backdrop-blur-md border border-white/15 rounded-xl p-4">
            <span className="inline-block text-xs font-semibold px-2.5 py-0.5 rounded-full bg-sky-500/20 border border-sky-400/40 text-sky-200">
              Unallocated: 0% • Under-Allocated: 50%
            </span>
            <p className="mt-2 text-xs text-slate-300">
              Surfaces active Workday employees with available or unassigned team capacity.
            </p>
          </div>

          <div className="bg-white/10 backdrop-blur-md border border-white/15 rounded-xl p-4">
            <span className="inline-block text-xs font-semibold px-2.5 py-0.5 rounded-full bg-rose-500/20 border border-rose-400/40 text-rose-200">
              Workday Inactive / Leaver
            </span>
            <p className="mt-2 text-xs text-slate-300">
              Flags deactivated Workday records still on active teams with one-click offboarding.
            </p>
          </div>

          <div className="bg-white/10 backdrop-blur-md border border-white/15 rounded-xl p-4">
            <span className="inline-block text-xs font-semibold px-2.5 py-0.5 rounded-full bg-indigo-500/20 border border-indigo-400/40 text-indigo-200">
              Skill Rollup &amp; Jira Directory Sync
            </span>
            <p className="mt-2 text-xs text-slate-300">
              Aggregates team skill heatmaps and exposes canonical team topology to Jira.
            </p>
          </div>
        </section>

        {/* Active Section Glass Panel */}
        <section
          data-testid="workspace-main-glass-panel"
          className="bg-slate-900/55 backdrop-blur-xl border border-white/15 rounded-2xl p-6 shadow-xl shadow-black/25"
        >
          <div className="flex flex-wrap items-center justify-between gap-4 border-b border-white/10 pb-4">
            <div>
              <span className="text-xs uppercase tracking-wider text-cyan-300 font-semibold">
                Active Operational View
              </span>
              <h1 className="text-2xl font-bold text-white mt-1">
                {currentSection.sectionHeading}
              </h1>
              <p className="text-sm text-slate-300 mt-1">{currentSection.subtitle}</p>
            </div>
            <div className="text-xs px-3 py-1.5 rounded-lg bg-white/10 border border-white/15 text-slate-200">
              Active Persona Scope: <strong>{activePersona}</strong>
            </div>
          </div>

          <div className="mt-6 grid grid-cols-1 md:grid-cols-3 gap-4">
            {currentSection.highlights.map((highlight) => (
              <div
                key={highlight}
                className="bg-slate-900/75 border border-white/15 rounded-xl p-4 text-sm text-slate-200"
              >
                {highlight}
              </div>
            ))}
          </div>
        </section>
      </main>
    </div>
  );
}
