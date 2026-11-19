import React, { useState } from 'react';
import {
  Activity,
  Award,
  BookOpenCheck,
  Building2,
  CheckCircle2,
  ExternalLink,
  Lock,
  RefreshCw,
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

export interface WorkdayPersonDirectoryEntry {
  employeeId: string;
  fullName: string;
  email: string;
  jobTitle: string;
  department: string;
  costCentre: string;
  managerEmployeeId: string | null;
  employmentStatus: 'Active' | 'Inactive';
}

export type SkillProficiencyTier = 'Beginner' | 'Intermediate' | 'Expert';

export interface StandardizedSkillCatalogItem {
  id: string;
  displayName: string;
  category: string;
}

export const STANDARDIZED_SKILLS: StandardizedSkillCatalogItem[] = [
  { id: 'skill-java-spring', displayName: 'Java 21 & Spring Boot', category: 'Backend' },
  { id: 'skill-postgres', displayName: 'PostgreSQL & Relational Modeling', category: 'Data & Analytics' },
  { id: 'skill-react-ts', displayName: 'React 18 & TypeScript', category: 'Frontend' },
  { id: 'skill-gcp-cloudrun', displayName: 'GCP Cloud Run & Cloud SQL', category: 'Cloud & DevOps' },
  { id: 'skill-atdd-bdd', displayName: 'ATDD & Specification by Example', category: 'Product & Delivery' },
  { id: 'skill-k8s-terraform', displayName: 'Terraform & Infrastructure as Code', category: 'Cloud & DevOps' },
];

export const PROFICIENCY_TIERS: SkillProficiencyTier[] = ['Beginner', 'Intermediate', 'Expert'];

function isSkillProficiencyTier(value: string): value is SkillProficiencyTier {
  return PROFICIENCY_TIERS.some((tier) => tier === value);
}

export type EmployeeSkillMap = Record<string, Array<{ skillName: string; proficiency: SkillProficiencyTier }>>;

export const INITIAL_EMPLOYEE_SKILLS: EmployeeSkillMap = {
  'Elena Rostova': [
    { skillName: 'Java 21 & Spring Boot', proficiency: 'Expert' },
    { skillName: 'GCP Cloud Run & Cloud SQL', proficiency: 'Expert' },
  ],
  'Marcus Vance': [
    { skillName: 'Java 21 & Spring Boot', proficiency: 'Expert' },
    { skillName: 'React 18 & TypeScript', proficiency: 'Intermediate' },
  ],
  'Priya Nair': [
    { skillName: 'React 18 & TypeScript', proficiency: 'Expert' },
    { skillName: 'PostgreSQL & Relational Modeling', proficiency: 'Intermediate' },
  ],
  'Devon Brooks': [
    { skillName: 'GCP Cloud Run & Cloud SQL', proficiency: 'Expert' },
    { skillName: 'Terraform & Infrastructure as Code', proficiency: 'Expert' },
  ],
};

export const INITIAL_WORKDAY_DIRECTORY: WorkdayPersonDirectoryEntry[] = [
  {
    employeeId: 'WD-1001',
    fullName: 'Elena Rostova',
    email: 'elena.rostova@teamliness.io',
    jobTitle: 'Principal Platform Architect',
    department: 'Cloud & Platform Foundations',
    costCentre: 'CC-4100',
    managerEmployeeId: null,
    employmentStatus: 'Active',
  },
  {
    employeeId: 'WD-1002',
    fullName: 'Marcus Vance',
    email: 'marcus.vance@teamliness.io',
    jobTitle: 'Lead Software Engineer',
    department: 'Digital Payments & Experience',
    costCentre: 'CC-4200',
    managerEmployeeId: 'WD-1001',
    employmentStatus: 'Active',
  },
  {
    employeeId: 'WD-1003',
    fullName: 'Priya Nair',
    email: 'priya.nair@teamliness.io',
    jobTitle: 'Senior Software Engineer',
    department: 'Digital Payments & Experience',
    costCentre: 'CC-4200',
    managerEmployeeId: 'WD-1002',
    employmentStatus: 'Active',
  },
  {
    employeeId: 'WD-1004',
    fullName: 'Devon Brooks',
    email: 'devon.brooks@teamliness.io',
    jobTitle: 'Staff Platform Engineer',
    department: 'Cloud & Platform Foundations',
    costCentre: 'CC-4100',
    managerEmployeeId: 'WD-1001',
    employmentStatus: 'Active',
  },
  {
    employeeId: 'WD-1005',
    fullName: 'Sofia Lindqvist',
    email: 'sofia.lindqvist@teamliness.io',
    jobTitle: 'Staff Data Engineer',
    department: 'Cloud & Platform Foundations',
    costCentre: 'CC-4100',
    managerEmployeeId: 'WD-1001',
    employmentStatus: 'Active',
  },
  {
    employeeId: 'WD-1006',
    fullName: "Liam O'Connor",
    email: 'liam.oconnor@teamliness.io',
    jobTitle: 'Senior Frontend Engineer',
    department: 'Digital Payments & Experience',
    costCentre: 'CC-4200',
    managerEmployeeId: 'WD-1002',
    employmentStatus: 'Active',
  },
  {
    employeeId: 'WD-1099',
    fullName: 'Arthur Pendelton',
    email: 'arthur.pendelton@teamliness.io',
    jobTitle: 'Former Staff Engineer',
    department: 'Cloud & Platform Foundations',
    costCentre: 'CC-4100',
    managerEmployeeId: 'WD-1001',
    employmentStatus: 'Inactive',
  },
];

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

function isRbacPersona(value: string): value is RbacPersona {
  return RBAC_PERSONAS.some((persona) => persona === value);
}

/**
 * Glassmorphic Operational Workspace Shell for Teamliness.
 */
export function App(): React.ReactElement {
  const [activeSectionId, setActiveSectionId] = useState<WorkspaceSectionId>('team-composition');
  const [activePersona, setActivePersona] = useState<RbacPersona>('Org Leader');
  const [workdaySyncStatus, setWorkdaySyncStatus] = useState<string>(
    'Workday SoR: Connected (Schema v2)'
  );
  const [directoryEntries] = useState<WorkdayPersonDirectoryEntry[]>(INITIAL_WORKDAY_DIRECTORY);
  const [employeeSkills, setEmployeeSkills] = useState<EmployeeSkillMap>(INITIAL_EMPLOYEE_SKILLS);
  const [selectedPersonName, setSelectedPersonName] = useState<string>('Elena Rostova');
  const [selectedSkillName, setSelectedSkillName] = useState<string>('Java 21 & Spring Boot');
  const [selectedProficiency, setSelectedProficiency] = useState<SkillProficiencyTier>('Expert');

  const currentSection =
    WORKSPACE_SECTIONS.find((section) => section.id === activeSectionId) ?? WORKSPACE_SECTIONS[0];

  const canAssignSkills = activePersona !== 'Read-Only Viewer';

  function handleTriggerWorkdaySync(): void {
    setWorkdaySyncStatus('Last Sync: 6 Upserted • 1 Leaver Deactivated (MOCK)');
    if (typeof fetch === 'function') {
      void fetch('/api/v1/workday/sync', {
        method: 'POST',
        headers: { 'X-Teamliness-Persona': activePersona },
      }).catch(() => undefined);
    }
  }

  function handleAssignValidatedSkill(): void {
    if (!canAssignSkills) {
      return;
    }
    setEmployeeSkills((prev) => {
      const existing = prev[selectedPersonName] ?? [];
      const filtered = existing.filter((item) => item.skillName !== selectedSkillName);
      return {
        ...prev,
        [selectedPersonName]: [
          ...filtered,
          { skillName: selectedSkillName, proficiency: selectedProficiency },
        ],
      };
    });
  }

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

            <div
              data-testid="workday-sync-status-badge"
              className="inline-flex items-center gap-1.5 px-3 py-1.5 rounded-lg bg-white/10 border border-white/15 text-xs text-slate-200"
            >
              <Lock className="h-3.5 w-3.5 text-sky-300" />
              <span>{workdaySyncStatus}</span>
            </div>

            {activePersona === 'Admin' && (
              <button
                type="button"
                onClick={handleTriggerWorkdaySync}
                className="inline-flex items-center gap-1.5 px-3 py-1.5 rounded-lg bg-cyan-500/20 border border-cyan-400/40 text-cyan-100 text-xs font-semibold hover:bg-cyan-500/30 transition"
              >
                <RefreshCw className="h-3.5 w-3.5" />
                <span>Sync Workday Directory</span>
              </button>
            )}

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
                onChange={(e) => {
                  if (isRbacPersona(e.target.value)) {
                    setActivePersona(e.target.value);
                  }
                }}
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

          {/* Track 03: Individual Validated Skill Matrix & Direct Assignment Bar */}
          {activeSectionId === 'skill-matrix' && (
            <div className="mt-6 pt-6 border-t border-white/15 flex flex-col gap-6">
              <div className="bg-white/10 backdrop-blur-md border border-white/15 rounded-xl p-4 flex flex-wrap items-end justify-between gap-4">
                <div className="flex flex-wrap items-end gap-3">
                  <div>
                    <label
                      htmlFor="skill-person-select"
                      className="block text-xs text-slate-300 mb-1"
                    >
                      Select Employee for Skill Tagging
                    </label>
                    <select
                      id="skill-person-select"
                      aria-label="Select Employee for Skill Tagging"
                      value={selectedPersonName}
                      onChange={(e) => setSelectedPersonName(e.target.value)}
                      className="bg-slate-900/80 text-xs text-white rounded-lg border border-white/15 px-3 py-2"
                    >
                      {directoryEntries
                        .filter((p) => p.employmentStatus === 'Active')
                        .map((person) => (
                          <option key={person.employeeId} value={person.fullName}>
                            {person.fullName}
                          </option>
                        ))}
                    </select>
                  </div>

                  <div>
                    <label
                      htmlFor="standardized-skill-select"
                      className="block text-xs text-slate-300 mb-1"
                    >
                      Select Standardized Skill
                    </label>
                    <select
                      id="standardized-skill-select"
                      aria-label="Select Standardized Skill"
                      value={selectedSkillName}
                      onChange={(e) => setSelectedSkillName(e.target.value)}
                      className="bg-slate-900/80 text-xs text-white rounded-lg border border-white/15 px-3 py-2"
                    >
                      {STANDARDIZED_SKILLS.map((skill) => (
                        <option key={skill.id} value={skill.displayName}>
                          {skill.displayName} ({skill.category})
                        </option>
                      ))}
                    </select>
                  </div>

                  <div>
                    <label
                      htmlFor="proficiency-tier-select"
                      className="block text-xs text-slate-300 mb-1"
                    >
                      Select Proficiency Tier
                    </label>
                    <select
                      id="proficiency-tier-select"
                      aria-label="Select Proficiency Tier"
                      value={selectedProficiency}
                      onChange={(e) => {
                        if (isSkillProficiencyTier(e.target.value)) {
                          setSelectedProficiency(e.target.value);
                        }
                      }}
                      className="bg-slate-900/80 text-xs text-white rounded-lg border border-white/15 px-3 py-2"
                    >
                      {PROFICIENCY_TIERS.map((tier) => (
                        <option key={tier} value={tier}>
                          {tier}
                        </option>
                      ))}
                    </select>
                  </div>
                </div>

                <button
                  type="button"
                  data-testid="assign-validated-skill-button"
                  disabled={!canAssignSkills}
                  onClick={handleAssignValidatedSkill}
                  title={
                    canAssignSkills
                      ? 'Directly assign and validate skill proficiency'
                      : 'Read-Only Viewer cannot mutate validated skills'
                  }
                  className={`inline-flex items-center gap-2 px-4 py-2 rounded-xl text-xs font-semibold border transition ${
                    canAssignSkills
                      ? 'bg-indigo-500/25 border-indigo-400/40 text-indigo-100 hover:bg-indigo-500/35'
                      : 'bg-slate-800/60 border-white/10 text-slate-500 cursor-not-allowed'
                  }`}
                >
                  <CheckCircle2 className="h-4 w-4" />
                  <span>Assign Validated Skill</span>
                </button>
              </div>

              {/* Individual Skill Matrix Table */}
              <div className="overflow-x-auto">
                <table className="w-full text-left border-collapse text-sm">
                  <thead>
                    <tr className="border-b border-white/15 text-xs uppercase text-slate-400">
                      <th className="py-3 px-3">Employee</th>
                      <th className="py-3 px-3">Workday Title &amp; Department</th>
                      <th className="py-3 px-3">Validated Skill Proficiencies</th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-white/10">
                    {directoryEntries
                      .filter((p) => p.employmentStatus === 'Active')
                      .map((person) => {
                        const skills = employeeSkills[person.fullName] ?? [];
                        return (
                          <tr
                            key={person.employeeId}
                            data-testid={`skill-matrix-row-${person.fullName}`}
                            className="bg-slate-900/75 hover:bg-white/5 transition"
                          >
                            <td className="py-3 px-3 font-medium text-white">
                              {person.fullName}
                            </td>
                            <td className="py-3 px-3 text-slate-300">
                              {person.jobTitle} • {person.department}
                            </td>
                            <td className="py-3 px-3">
                              <div className="flex flex-wrap gap-2">
                                {skills.map((item) => (
                                  <span
                                    key={item.skillName}
                                    className="inline-flex items-center gap-1 text-xs font-medium px-2.5 py-1 rounded-full bg-indigo-500/20 border border-indigo-400/35 text-indigo-200"
                                  >
                                    {item.skillName}: {item.proficiency}
                                  </span>
                                ))}
                              </div>
                            </td>
                          </tr>
                        );
                      })}
                  </tbody>
                </table>
              </div>
            </div>
          )}
        </section>

        {/* Workday System-of-Record Employee Directory Projection Panel */}
        <section
          aria-label="Workday Employee Directory Projection"
          className="bg-slate-900/55 backdrop-blur-xl border border-white/15 rounded-2xl p-6 shadow-xl shadow-black/25"
        >
          <div className="flex flex-wrap items-center justify-between gap-4 border-b border-white/10 pb-4">
            <div>
              <span className="text-xs uppercase tracking-wider text-sky-300 font-semibold">
                WorkdayDirectoryPort • Read-Only System-of-Record Projection
              </span>
              <h2 className="text-lg font-bold text-white mt-1">
                Workday Employee Directory &amp; Leaver Status
              </h2>
            </div>
            <span className="inline-flex items-center gap-1.5 text-xs px-3 py-1 rounded-full bg-sky-500/15 border border-sky-400/30 text-sky-200">
              <Lock className="h-3 w-3" />
              <span>Core HR Attributes Read-Only</span>
            </span>
          </div>

          <div className="mt-4 overflow-x-auto">
            <table className="w-full text-left border-collapse text-sm">
              <thead>
                <tr className="border-b border-white/15 text-xs uppercase text-slate-400">
                  <th className="py-3 px-3">Workday ID</th>
                  <th className="py-3 px-3">Full Name</th>
                  <th className="py-3 px-3">Job Title</th>
                  <th className="py-3 px-3">Department &amp; Cost Centre</th>
                  <th className="py-3 px-3">Employment Status</th>
                  <th className="py-3 px-3">SoR Boundary</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-white/10">
                {directoryEntries.map((person) => {
                  const isLeaver = person.employmentStatus === 'Inactive';
                  return (
                    <tr
                      key={person.employeeId}
                      data-testid={`workday-person-row-${person.employeeId}`}
                      className="bg-slate-900/75 hover:bg-white/5 transition"
                    >
                      <td className="py-3 px-3 font-mono text-xs text-cyan-300">
                        {person.employeeId}
                      </td>
                      <td className="py-3 px-3 font-medium text-white">{person.fullName}</td>
                      <td className="py-3 px-3 text-slate-300">{person.jobTitle}</td>
                      <td className="py-3 px-3 text-slate-300">
                        {person.department}{' '}
                        <span className="text-xs text-slate-400">({person.costCentre})</span>
                      </td>
                      <td className="py-3 px-3">
                        {isLeaver ? (
                          <span className="inline-block text-xs font-semibold px-2.5 py-0.5 rounded-full bg-rose-500/20 border border-rose-400/40 text-rose-200">
                            Workday Inactive / Leaver
                          </span>
                        ) : (
                          <span className="inline-block text-xs font-semibold px-2.5 py-0.5 rounded-full bg-emerald-500/20 border border-emerald-400/40 text-emerald-200">
                            Active
                          </span>
                        )}
                      </td>
                      <td className="py-3 px-3">
                        <span className="inline-flex items-center gap-1 text-xs text-slate-300 bg-white/5 border border-white/10 rounded-md px-2 py-0.5">
                          <Lock className="h-3 w-3 text-sky-300" />
                          <span>Synced from Workday</span>
                        </span>
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        </section>
      </main>
    </div>
  );
}
