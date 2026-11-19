import fs from 'node:fs';
import path from 'node:path';
import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, expect, it } from 'vitest';
import { App } from '../App';

const featurePath = path.resolve(process.cwd(), 'src/features/track_01_glassmorphic_shell.feature');
const featureContent = fs.readFileSync(featurePath, 'utf-8');

/**
 * Parses the Examples table from `track_01_glassmorphic_shell.feature` so the Gherkin
 * `.feature` file serves as the single authoritative source of truth for ATDD execution.
 */
function parseExamplesFromFeature(gherkin: string): Array<{ nav_label: string; section_heading: string }> {
  const lines = gherkin
    .split(/\r?\n/)
    .map((l) => l.trim())
    .filter((l) => l.startsWith('|') && l.endsWith('|'));

  if (lines.length < 2) {
    return [];
  }

  const rows = lines.slice(1);
  return rows.map((row) => {
    const cells = row
      .split('|')
      .map((c) => c.trim())
      .filter(Boolean);
    return {
      nav_label: cells[0],
      section_heading: cells[1],
    };
  });
}

describe('Feature: Track 01 — Glassmorphic Operational Workspace Shell & Living Docs Navigation', () => {
  it('Scenario: Glassmorphic workspace shell renders with navigation and living-docs link', () => {
    render(<App />);

    const canvas = screen.getByTestId('glassmorphic-mesh-canvas');
    expect(canvas.className).toContain('glass-mesh-canvas');

    const header = screen.getByTestId('glassmorphic-workspace-header');
    expect(header.className).toContain('backdrop-blur');

    const personaBadge = screen.getByTestId('active-rbac-persona-badge');
    expect(personaBadge).toHaveTextContent('Org Leader');

    const livingDocsLink = screen.getByTestId('living-docs-status-link');
    expect(livingDocsLink).toBeVisible();
    expect(livingDocsLink).toHaveTextContent(/Living Docs/i);
  });

  it('Scenario: Switching the RBAC persona updates the active persona badge in the glassmorphic header', async () => {
    const user = userEvent.setup();
    render(<App />);

    const selector = screen.getByLabelText('Switch RBAC Persona');
    await user.selectOptions(selector, 'Admin');

    expect(screen.getByTestId('active-rbac-persona-badge')).toHaveTextContent('Admin');
  });

  const examples = parseExamplesFromFeature(featureContent);

  it.each(examples)(
    'Scenario Outline: Navigating to "$nav_label" renders "$section_heading" with glassmorphic surface elevation',
    async ({ nav_label, section_heading }) => {
      const user = userEvent.setup();
      render(<App />);

      const navTab = screen.getByRole('button', { name: new RegExp(nav_label, 'i') });
      await user.click(navTab);

      const mainPanel = screen.getByTestId('workspace-main-glass-panel');
      expect(mainPanel.className).toContain('backdrop-blur');
      expect(screen.getByRole('heading', { name: section_heading })).toBeInTheDocument();
    }
  );
});
