# Project Workflow

## Guiding Principles

1.  **The Plan is the Source of Truth:** All work must be tracked in `plan.md`
2.  **The Tech Stack is Deliberate:** Changes to the tech stack must be
    documented in `tech-stack.md` *before* implementation
3.  **Test-Driven Development:** Write unit and integration tests before implementing
    functionality. Every domain rule (e.g., over-allocation >100% soft warning calculation,
    inactive Workday leaver handling & flagging, idempotent Workday sync upserts,
    skill assignment validation, and Jira team directory payload contract) MUST have
    automated tests written first in Red-Green-Refactor order.
4.  **High Code Coverage:** Aim for >80% code coverage for all modules
5.  **User Experience First:** Every decision should prioritize user experience
6.  **Non-Interactive & CI-Aware (Single-Run Mode Only):** All test and linter executions
    MUST run in single-run mode with **no watch flags** (e.g., `./gradlew test` for the
    Spring Boot backend using Docker Testcontainers PostgreSQL, and `npx vitest run` or
    `CI=true npm test -- --run` for the React SPA frontend).

## Task Workflow

All tasks follow a strict lifecycle:

### Standard Task Workflow

1.  **Select Task:** Choose the next available task from `plan.md` in sequential
    order

2.  **Mark In Progress:** Before beginning work, edit `plan.md` and change the
    task from `[ ]` to `[~]`

3.  **Write Failing Tests (Red Phase):**

    -   Create a new test file for the feature or bug fix.
    -   Write one or more unit/integration tests that clearly define the expected behavior
        and acceptance criteria for the task (including domain rules such as >100%
        over-allocation soft-warning flags, inactive Workday leaver handling, and
        idempotent `WorkdayDirectoryPort` sync).
    -   **CRITICAL:** Run the tests in single-run mode (`./gradlew test` or `npx vitest run`)
        and confirm that they fail as expected. This is the "Red" phase of TDD.
        Do not proceed until you have failing tests.

4.  **Implement to Pass Tests (Green Phase):**

    -   Write the minimum amount of application code necessary to make the
        failing tests pass.
    -   Run the test suite again in single-run mode and confirm that all tests now pass.
        This is the "Green" phase.

5.  **Refactor (Optional but Recommended):**

    -   With the safety of passing tests, refactor the implementation code and
        the test code to improve clarity, remove duplication, and enhance
        performance without changing the external behavior.
    -   Rerun tests in single-run mode to ensure they still pass after refactoring.

6.  **Verify Coverage:** Run coverage reports using the project's chosen tools:
    -   **Backend (Spring Boot / Gradle / JaCoCo):** `./gradlew test jacocoTestReport`
    -   **Frontend (React / Vitest):** `npx vitest run --coverage`
    Target: >80% coverage for new code.

7.  **Document Deviations:** If implementation differs from tech stack:

    -   **STOP** implementation
    -   Update `tech-stack.md` with new design
    -   Add dated note explaining the change
    -   Resume implementation

8.  **Commit Code Changes:**

    -   Stage all code changes related to the task.
    -   Propose a clear, concise commit message e.g., `feat(workday): Implement idempotent sync and leaver flagging`.
    -   Perform the commit.

9.  **Attach Task Summary with Git Notes:**

    -   **Step 9.1: Get Commit Hash:** Obtain the hash of the *just-completed
        commit* (`git log -1 --format="%H"`).
    -   **Step 9.2: Draft Note Content:** Create a detailed summary for the
        completed task. This should include the task name, a summary of changes,
        a list of all created/modified files, and the core "why" for the change.
    -   **Step 9.3: Attach Note:** Use the `git notes` command to attach the
        summary to the commit:
        `git notes add -m "<note content>" <commit_hash>`

10. **Get and Record Task Commit SHA:**

    -   **Step 10.1: Update Plan:** Read `plan.md`, find the line for the
        completed task, update its status from `[~]` to `[x]`, and append the
        first 7 characters of the *just-completed commit's* commit hash.
    -   **Step 10.2: Write Plan:** Write the updated content back to `plan.md`.

11. **Commit Plan Update:**

    -   **Action:** Stage the modified `plan.md` file.
    -   **Action:** Commit this change with a descriptive message (e.g.,
        `conductor(plan): Mark task 'Create user model' as complete`).

### Task Correction & Plan Amendment Workflows

When an implemented task or phase requires corrections, amendments, or additions, follow these standard workflows to maintain plan integrity and avoid untracked code drift:

1.  **In-Flight Refinements:** If minor gaps are found while a task is actively
    in-progress (`[~]`), make the adjustments directly in the active
    implementation stream and ensure passing tests before committing.
2.  **Code Review Corrections (`conductor-review`):** If issues are identified
    during or after a code review, instruct the agent to review your changes
    (e.g., *"run a review"* or triggering the action manually in compatible
    clients). The review agent will automatically append a `Review Fixes` phase
    to `plan.md` so that correction tasks are formally tracked and
    checkpointed.
3.  **Logical State Reversions (`conductor-revert`):** If a task implementation
    is fundamentally flawed or needs to be redone, instruct the agent to revert
    the changes (e.g., *"revert the last task"* or triggering the action
    manually in compatible clients). This safely rolls back associated git
    commits and resets the task state in `plan.md` back to pending `[ ]` to
    allow a clean restart.

### Phase Completion Verification and Checkpointing Protocol

**Trigger:** This protocol is executed immediately after a task is completed
that also concludes a phase in `plan.md`.

1.  **Announce Protocol Start:** Inform the user that the phase is complete and
    the verification and checkpointing protocol has begun.

2.  **Ensure Test Coverage for Phase Changes:**

    -   **Step 2.1: Determine Phase Scope:** To identify the files changed in
        this phase, you must first find the starting point. Read `plan.md` to
        find the Git commit SHA of the *previous* phase's checkpoint. If no
        previous checkpoint exists, the scope is all changes since the first
        commit.
    -   **Step 2.2: List Changed Files:** Execute `git diff --name-only
        <previous_checkpoint_sha> HEAD` to get a precise list of all files
        modified during this phase.
    -   **Step 2.3: Verify and Create Tests:** For each file in the list:
        -   **CRITICAL:** First, check its extension. Exclude non-code files
            (e.g., `.json`, `.md`, `.yaml`, `.sql`).
        -   For each remaining code file, verify a corresponding test file
            exists.
        -   If a test file is missing, you **must** create one. Before writing
            the test, **first, analyze other test files in the repository to
            determine the correct naming convention and testing style.** The new
            tests **must** validate the functionality described in this phase's
            tasks (`plan.md`).

3.  **Execute Automated Tests with Proactive Debugging:**

    -   Before execution, you **must** announce the exact shell command you will
        use to run the tests (always single-run, no watch flags).
    -   **Example Announcement:** "I will now run the automated test suite to
        verify the phase. **Command:** `cd backend && ./gradlew test && cd ../frontend && npx vitest run`"
    -   Execute the announced command.
    -   If tests fail, you **must** inform the user and begin debugging. You may
        attempt to propose a fix a **maximum of two times**. If the tests still
        fail after your second proposed fix, you **must stop**, report the
        persistent failure, and ask the user for guidance.

4.  **Propose a Detailed, Actionable Manual Verification Plan:**

    -   **CRITICAL:** To generate the plan, first analyze `product.md`,
        `product-guidelines.md`, and `plan.md` to determine the user-facing
        goals of the completed phase.
    -   You **must** generate a step-by-step plan that walks the user through
        the verification process, including any necessary commands and specific,
        expected outcomes.
    -   The plan you present to the user **must** follow this format:

        **For a Frontend Change:**
        ```
        The automated tests have passed. For manual verification, please follow these steps:

        **Manual Verification Steps:**
        1. **Start the development server with the command:** `cd frontend && npm run dev`
        2. **Open your browser to:** `http://localhost:5173`
        3. **Confirm that you see:** The Team Composition table with over-allocation and Workday status badges displayed correctly.
        ```

        **For a Backend Change:**
        ```
        The automated tests have passed. For manual verification, please follow these steps:

        **Manual Verification Steps:**
        1. **Ensure the backend server is running:** `cd backend && ./gradlew bootRun`
        2. **Execute the following command in your terminal:** `curl -X GET http://localhost:8080/api/v1/integrations/jira/teams`
        3. **Confirm that you receive:** A JSON response with a status of `200 OK` listing active teams and their Jira project mappings.
        ```

5.  **Await Explicit User Feedback:**

    -   After presenting the detailed plan, ask the user for confirmation:
        "**Does this meet your expectations? Please confirm with yes or provide
        feedback on what needs to be changed.**"
    -   **PAUSE** and await the user's response. Do not proceed without an
        explicit yes or confirmation.

6.  **Identify Target Commit for Report:**

    -   Do NOT create a new empty commit for checkpointing.
    -   Identify the hash of the last functional commit made during this phase. This will be the target for the verification report.

7.  **Attach Auditable Verification Report using Git Notes:**

    -   **Step 7.1: Draft Note Content:** Create a detailed verification report
        including the automated test command, the manual verification steps, and
        the user's confirmation.
    -   **Step 7.2: Attach Note:** Use the `git notes` command to attach the full report to the target commit identified in step 6.

8.  **Get and Record Phase Checkpoint SHA:**

    -   **Step 8.1: Get Commit Hash:** Obtain the hash of the *just-created
        checkpoint commit* (`git log -1 --format="%H"`).
    -   **Step 8.2: Update Plan:** Read `plan.md`, find the heading for the
        completed phase, and append the first 7 characters of the commit hash in
        the format `[checkpoint: <sha>]`.
    -   **Step 8.3: Write Plan:** Write the updated content back to `plan.md`.

9.  **Commit Plan Update:**

    -   **Action:** Stage the modified `plan.md` file.
    -   **Action:** Commit this change with a descriptive message following the
        format `conductor(plan): Mark phase '<PHASE NAME>' as complete`.

10. **Announce Completion:** Inform the user that the phase is complete and the
    checkpoint has been created, with the detailed verification report attached
    as a git note.

### Quality Gates

Before marking any task complete, verify:

-   [ ] All tests pass in single-run mode (no watch flags)
-   [ ] Code coverage meets requirements (>80%)
-   [ ] Code follows project's code style guidelines (as defined in
    `code_styleguides/`)
-   [ ] All public functions/methods are documented (e.g., Javadoc, JSDoc)
-   [ ] Type safety is enforced (Java 21 strict compilation, TypeScript `strict: true`)
-   [ ] No linting or static analysis errors
-   [ ] Documentation updated if needed
-   [ ] No security vulnerabilities introduced

## Development Commands

### Setup

```bash
# Start local PostgreSQL database
docker compose up -d

# Backend setup & test compilation (Gradle)
cd backend && ./gradlew testClasses

# Frontend dependency installation
cd frontend && npm install
```

### Daily Development

```bash
# Start Backend API (Spring Boot via Gradle)
cd backend && ./gradlew bootRun

# Run Backend Tests (Single-run mode with Docker Testcontainers, no watch)
cd backend && ./gradlew test

# Start Frontend SPA (Vite)
cd frontend && npm run dev

# Run Frontend Tests (Single-run mode, no watch)
cd frontend && npx vitest run
```

### Before Committing

```bash
# Backend full verification & JaCoCo coverage
cd backend && ./gradlew check jacocoTestReport

# Frontend type-check, lint, and single-run tests
cd frontend && npm run lint && npm run build && npx vitest run --coverage
```

## Testing Requirements

### Domain Rule TDD Mandate

Every core domain rule MUST be covered by dedicated unit and integration tests executed in single-run mode:
1.  **Workday Directory Sync (`WorkdayDirectoryPort`):**
    -   Idempotent upserts (repeated sync runs produce no duplicate records or unintended mutations).
    -   Soft-deactivation for leavers (employees marked inactive or omitted in Workday transition to `Inactive` status locally while preserving historical records).
    -   Adapter isolation (`MockWorkdayAdapter` vs `WorkdayRaaSAdapter`).
2.  **Team Capacity & Over-Allocation (`>100%` Soft Warning):**
    -   Calculation of total allocation percentage across overlapping active team membership date ranges (`start_date` to `end_date`).
    -   Soft-warning enforcement: assignments exceeding `100%` are persisted and flagged with `is_over_allocated = true` and exact total allocation percentage.
3.  **Inactive Workday Leaver Handling on Teams & Outcomes:**
    -   Active memberships and outcome contributions linked to an `Inactive` Workday employee are automatically flagged with a leaver warning indicator.
    -   One-click offboarding/reassignment closes active memberships (`end_date`) without deleting historical allocation records.
4.  **Direct Skill Validation & RBAC Personas:**
    -   Skills assigned by `Admin`, `Org Leader`, or `Team Lead` are persisted as validated with proficiency ratings (`Beginner`, `Intermediate`, `Expert`).
    -   RBAC enforcement across `Admin`, `Org Leader`, `Team Lead`, and `Read-Only Viewer`.
5.  **Jira Canonical Team Directory Contract:**
    -   Read-only integration endpoint returns active teams, org unit hierarchy, leads, members, and Jira Project Key mappings.

### Unit Testing

-   Every module must have corresponding tests.
-   Use appropriate test setup/teardown mechanisms (e.g., fixtures,
    `@BeforeEach`, `beforeEach`/`afterEach`).
-   Mock external dependencies (`WorkdayDirectoryPort` in unit tests; WireMock / MockRestServiceServer for `WorkdayRaaSAdapter` tests).
-   Test both success and failure cases.

### Integration Testing

-   Test complete user flows and REST controller contracts (`MockMvc` / `SpringBootTest`).
-   Verify Flyway schema migrations and database transactions.
-   Test RBAC authentication and persona authorization across endpoints.
-   Check React SPA views (Team Composition, Skill Matrix, Outcome Commitments) using React Testing Library + Vitest in single-run mode.

## Code Review Process

### Self-Review Checklist

Before requesting review:

1.  **Functionality**

    -   Feature works as specified
    -   Edge cases handled (e.g., date boundary overlaps, 0% / 100% / >100% allocations, Workday re-hires)
    -   Error and warning messages are clear and actionable

2.  **Code Quality**

    -   Follows style guide
    -   Hexagonal port/adapter boundaries respected
    -   Clear variable/function names
    -   Appropriate comments

3.  **Testing**

    -   Unit tests comprehensive
    -   Integration tests pass in single-run mode
    -   Coverage adequate (>80%)

4.  **Security**

    -   No hardcoded secrets (Workday RaaS credentials injected via environment/configuration)
    -   Input validation present (`jakarta.validation` on all DTOs)
    -   SQL injection prevented (parameterized queries)
    -   XSS protection in place

5.  **Performance**

    -   Database queries indexed and optimized (avoid N+1 queries on team hierarchies and skill matrices)

## Commit Guidelines

### Message Format

```
<type>(<scope>): <description>

[optional body]

[optional footer]
```

### Types

-   `feat`: New feature
-   `fix`: Bug fix
-   `docs`: Documentation only
-   `style`: Formatting, missing semicolons, etc.
-   `refactor`: Code change that neither fixes a bug nor adds a feature
-   `test`: Adding missing tests
-   `chore`: Maintenance tasks

### Examples

```bash
git commit -m "feat(workday): Implement WorkdayDirectoryPort and MockWorkdayAdapter"
git commit -m "feat(teams): Calculate soft over-allocation warning across active memberships"
git commit -m "test(workday): Add idempotent sync and leaver soft-deactivation tests"
git commit -m "feat(jira): Expose read-only canonical teams endpoint for Jira sync"
```

## Definition of Done

A task is complete when:

1.  All code implemented to specification
2.  Unit and integration tests written first (TDD) and passing in single-run mode
3.  Code coverage meets project requirements (>80%)
4.  Documentation complete (if applicable)
5.  Code passes all configured linting and static analysis checks
6.  Implementation notes added to `plan.md`
7.  Changes committed with proper message
8.  Git note with task summary attached to the commit

## Emergency Procedures

### Critical Bug in Production

1.  Create hotfix branch from main
2.  Write failing test for bug
3.  Implement minimal fix
4.  Test thoroughly in single-run mode
5.  Deploy immediately
6.  Document in `plan.md`

### Data Loss

1.  Stop all write operations
2.  Restore from latest PostgreSQL backup
3.  Re-run idempotent Workday sync if needed
4.  Verify data integrity
5.  Document incident and update backup procedures

### Security Breach

1.  Rotate all secrets and Workday API credentials immediately
2.  Review access logs
3.  Patch vulnerability
4.  Notify affected users (if any)
5.  Document and update security procedures

## Deployment Workflow

### Pre-Deployment Checklist

-   [ ] All backend and frontend tests passing in single-run mode
-   [ ] Coverage >80%
-   [ ] No linting or type errors
-   [ ] Environment variables configured
-   [ ] Flyway database migrations verified
-   [ ] Database backup created

### Deployment Steps

1.  Merge feature branch to main
2.  Tag release with version
3.  Push to deployment service
4.  Verify Flyway database migrations execute cleanly
5.  Verify deployment health check
6.  Test critical paths (Workday sync, Team composition, Jira endpoint)
7.  Monitor for errors

### Post-Deployment

1.  Monitor application metrics
2.  Check error and sync logs
3.  Gather user feedback
4.  Plan next iteration

## Continuous Improvement

-   Review workflow weekly
-   Update based on pain points
-   Document lessons learned
-   Optimize for user happiness
-   Keep things simple and maintainable
