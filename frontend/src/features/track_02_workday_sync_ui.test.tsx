import fs from 'node:fs';
import path from 'node:path';
import { render, screen, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, expect, it } from 'vitest';
import { App } from '../App';

const featurePath = path.resolve(process.cwd(), 'src/features/track_02_workday_sync_ui.feature');
const featureContent = fs.readFileSync(featurePath, 'utf-8');

interface EmployeeDirectoryExample {
  employee_id: string;
  full_name: string;
  employment_status_badge: string;
}

function parseEmployeeExamples(gherkin: string): EmployeeDirectoryExample[] {
  const lines = gherkin
    .split(/\r?\n/)
    .map((line) => line.trim())
    .filter((line) => line.startsWith('|') && line.endsWith('|'));

  if (lines.length < 2) {
    return [];
  }

  return lines.slice(1).map((row) => {
    const cells = row
      .split('|')
      .map((cell) => cell.trim())
      .filter(Boolean);
    return {
      employee_id: cells[0],
      full_name: cells[1],
      employment_status_badge: cells[2],
    };
  });
}

describe('Feature: Track 02 — Glassmorphic Workday Sync Bar & Read-Only System-of-Record Directory', () => {
  it('Scenario: Admin triggers an on-demand Workday Directory Sync from the Glassmorphic workspace header', async () => {
    const user = userEvent.setup();
    render(<App />);

    const personaSelector = screen.getByLabelText('Switch RBAC Persona');
    await user.selectOptions(personaSelector, 'Admin');

    const syncButton = screen.getByRole('button', { name: /Sync Workday Directory/i });
    await user.click(syncButton);

    const syncStatusBadge = screen.getByTestId('workday-sync-status-badge');
    expect(syncStatusBadge).toHaveTextContent('Last Sync: 6 Upserted • 1 Leaver Deactivated (MOCK)');
  });

  const examples = parseEmployeeExamples(featureContent);

  it.each(examples)(
    'Scenario Outline: Employee row "$employee_id" displays "$full_name", "$employment_status_badge", and "Synced from Workday" read-only lock',
    ({ employee_id, full_name, employment_status_badge }) => {
      render(<App />);

      const row = screen.getByTestId(`workday-person-row-${employee_id}`);
      expect(within(row).getByText(full_name)).toBeInTheDocument();
      expect(within(row).getByText(employment_status_badge)).toBeInTheDocument();
      expect(within(row).getByText(/Synced from Workday/i)).toBeInTheDocument();
    }
  );
});
