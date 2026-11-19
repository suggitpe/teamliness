import fs from 'node:fs';
import path from 'node:path';
import { render, screen, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, expect, it } from 'vitest';
import { App } from '../App';

const featurePath = path.resolve(
  process.cwd(),
  'src/features/track_03_persona_and_skill_matrix.feature'
);
const featureContent = fs.readFileSync(featurePath, 'utf-8');

interface PersonaControlExample {
  persona_name: string;
  control_state: string;
}

function parsePersonaControlExamples(gherkin: string): PersonaControlExample[] {
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
      persona_name: cells[0],
      control_state: cells[1],
    };
  });
}

describe('Feature: Track 03 — Glassmorphic Persona Switcher & Individual Validated Skill Matrix', () => {
  const examples = parsePersonaControlExamples(featureContent);

  it.each(examples)(
    'Scenario Outline: Active persona "$persona_name" sets direct skill assignment button to "$control_state"',
    async ({ persona_name, control_state }) => {
      const user = userEvent.setup();
      render(<App />);

      const skillMatrixTab = screen.getByRole('button', { name: /Skill Matrix/i });
      await user.click(skillMatrixTab);

      const personaSelector = screen.getByLabelText('Switch RBAC Persona');
      await user.selectOptions(personaSelector, persona_name);

      const assignButton = screen.getByTestId('assign-validated-skill-button');
      if (control_state === 'disabled') {
        expect(assignButton).toBeDisabled();
      } else {
        expect(assignButton).toBeEnabled();
      }
    }
  );

  it('Scenario: Authorized leader assigns a validated Expert skill proficiency in the Individual Skill Matrix', async () => {
    const user = userEvent.setup();
    render(<App />);

    const skillMatrixTab = screen.getByRole('button', { name: /Skill Matrix/i });
    await user.click(skillMatrixTab);

    const personSelect = screen.getByLabelText('Select Employee for Skill Tagging');
    await user.selectOptions(personSelect, 'Priya Nair');

    const skillSelect = screen.getByLabelText('Select Standardized Skill');
    await user.selectOptions(skillSelect, 'Java 21 & Spring Boot');

    const proficiencySelect = screen.getByLabelText('Select Proficiency Tier');
    await user.selectOptions(proficiencySelect, 'Expert');

    const assignButton = screen.getByTestId('assign-validated-skill-button');
    await user.click(assignButton);

    const priyaRow = screen.getByTestId('skill-matrix-row-Priya Nair');
    expect(within(priyaRow).getByText('Java 21 & Spring Boot: Expert')).toBeInTheDocument();
  });
});
