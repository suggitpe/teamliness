import fs from 'node:fs';
import path from 'node:path';
import type { Reporter, File } from 'vitest';

/**
 * Custom Vitest reporter that compiles Gherkin .feature specifications in `src/features/`
 * alongside executed ATDD scenario results into `reports/living-docs/index.html`.
 */
export default class LivingDocsReporter implements Reporter {
  onFinished(files: File[] = []): void {
    const projectRoot = process.cwd();
    const featuresDir = path.join(projectRoot, 'src', 'features');
    const outputDir = path.join(projectRoot, 'reports', 'living-docs');
    if (!fs.existsSync(outputDir)) {
      fs.mkdirSync(outputDir, { recursive: true });
    }

    const featureFiles = fs.existsSync(featuresDir)
      ? fs.readdirSync(featuresDir).filter((f) => f.endsWith('.feature'))
      : [];

    const testResults: Array<{ name: string; state: string }> = [];
    for (const file of files) {
      for (const task of file.tasks) {
        if (task.type === 'suite') {
          for (const child of task.tasks) {
            testResults.push({
              name: `${task.name} > ${child.name}`,
              state: child.result?.state ?? 'unknown',
            });
          }
        } else {
          testResults.push({
            name: task.name,
            state: task.result?.state ?? 'unknown',
          });
        }
      }
    }

    const passedCount = testResults.filter((t) => t.state === 'pass').length;
    const failedCount = testResults.filter((t) => t.state === 'fail').length;

    const featureBlocksHtml = featureFiles
      .map((fileName) => {
        const raw = fs.readFileSync(path.join(featuresDir, fileName), 'utf-8');
        const escaped = raw
          .replace(/&/g, '&amp;')
          .replace(/</g, '&lt;')
          .replace(/>/g, '&gt;');
        return `
        <section class="feature-card">
          <div class="feature-header">
            <span class="badge-spec">Gherkin Specification</span>
            <h2>${fileName}</h2>
          </div>
          <pre class="gherkin-source">${escaped}</pre>
        </section>`;
      })
      .join('\n');

    const testRowsHtml = testResults
      .map(
        (tr) => `
        <tr>
          <td>${tr.name}</td>
          <td>
            <span class="status-pill ${tr.state === 'pass' ? 'status-pass' : 'status-fail'}">
              ${tr.state.toUpperCase()}
            </span>
          </td>
        </tr>`
      )
      .join('\n');

    const html = `<!doctype html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <title>Teamliness Frontend — ATDD HTML Living Documentation</title>
  <style>
    body {
      margin: 0;
      font-family: Inter, system-ui, -apple-system, sans-serif;
      background: radial-gradient(circle at 15% 15%, rgba(56, 189, 248, 0.16), transparent 45%),
                  radial-gradient(circle at 85% 20%, rgba(99, 102, 241, 0.18), transparent 50%),
                  #020617;
      color: #f1f5f9;
      padding: 2rem;
    }
    .container { max-width: 1100px; margin: 0 auto; }
    .header-card, .feature-card, .results-card {
      background: rgba(15, 23, 42, 0.65);
      backdrop-filter: blur(16px);
      border: 1px solid rgba(255, 255, 255, 0.15);
      border-radius: 16px;
      padding: 1.5rem;
      margin-bottom: 1.5rem;
      box-shadow: 0 20px 40px rgba(0, 0, 0, 0.35);
    }
    h1 { margin: 0 0 0.5rem 0; font-size: 1.6rem; color: #38bdf8; }
    h2 { margin: 0.25rem 0 0.75rem 0; font-size: 1.15rem; color: #e2e8f0; }
    .summary-bar { display: flex; gap: 1rem; margin-top: 1rem; }
    .metric {
      background: rgba(255, 255, 255, 0.06);
      border: 1px solid rgba(255, 255, 255, 0.12);
      border-radius: 10px;
      padding: 0.75rem 1.25rem;
    }
    .badge-spec {
      display: inline-block;
      font-size: 0.75rem;
      text-transform: uppercase;
      letter-spacing: 0.06em;
      background: rgba(56, 189, 248, 0.18);
      color: #7dd3fc;
      border: 1px solid rgba(56, 189, 248, 0.35);
      border-radius: 999px;
      padding: 0.2rem 0.65rem;
    }
    .gherkin-source {
      background: rgba(2, 6, 23, 0.75);
      border: 1px solid rgba(255, 255, 255, 0.1);
      border-radius: 10px;
      padding: 1rem;
      overflow-x: auto;
      color: #cbd5e1;
      line-height: 1.5;
      font-size: 0.9rem;
    }
    table { width: 100%; border-collapse: collapse; margin-top: 0.75rem; }
    th, td {
      text-align: left;
      padding: 0.75rem;
      border-bottom: 1px solid rgba(255, 255, 255, 0.1);
    }
    .status-pill {
      display: inline-block;
      padding: 0.2rem 0.65rem;
      border-radius: 999px;
      font-size: 0.75rem;
      font-weight: 700;
    }
    .status-pass {
      background: rgba(16, 185, 129, 0.2);
      color: #6ee7b7;
      border: 1px solid rgba(16, 185, 129, 0.4);
    }
    .status-fail {
      background: rgba(244, 63, 94, 0.2);
      color: #fda4af;
      border: 1px solid rgba(244, 63, 94, 0.4);
    }
  </style>
</head>
<body>
  <div class="container">
    <header class="header-card">
      <h1>Teamliness Frontend — Specification by Example (ATDD) Living Documentation</h1>
      <p>Generated automatically from executable Gherkin specifications and single-run Vitest BDD verification.</p>
      <div class="summary-bar">
        <div class="metric"><strong>Features:</strong> ${featureFiles.length}</div>
        <div class="metric"><strong>Scenarios Passed:</strong> ${passedCount}</div>
        <div class="metric"><strong>Scenarios Failed:</strong> ${failedCount}</div>
      </div>
    </header>
    ${featureBlocksHtml}
    <section class="results-card">
      <h2>Executed ATDD &amp; Component Verification Scenarios</h2>
      <table>
        <thead>
          <tr><th>Scenario / Example</th><th>Status</th></tr>
        </thead>
        <tbody>
          ${testRowsHtml}
        </tbody>
      </table>
    </section>
  </div>
</body>
</html>`;

    fs.writeFileSync(path.join(outputDir, 'index.html'), html, 'utf-8');
  }
}
