# Technology Stack: Teamliness

## 1. Architecture Overview
- **Pattern:** Client-Server application with a **Hexagonal / Modular Monolith Backend** and a **Glassmorphic Single-Page Application (SPA) Frontend**, verified via **Specification by Example (ATDD) HTML Living Documentation** and containerized for **Google Cloud Platform (GCP)** deployment.
- **Repository Structure:** Multi-module workspace containing `backend/` (Java 21 + Spring Boot 3), `frontend/` (TypeScript + React 18+ + Vite), and cloud deployment configurations (`cloudbuild.yaml`, `.github/workflows/deploy-gcp.yml`, `backend/Dockerfile`, `frontend/Dockerfile`).

## 2. Backend Stack (`backend/`)
- **Language:** Java 21 LTS (leveraging records, sealed interfaces, pattern matching, and virtual threads where appropriate).
- **Framework:** Spring Boot 3.x (`spring-boot-starter-web`, `spring-boot-starter-validation`, `spring-boot-starter-security` / RBAC filter, `spring-boot-starter-actuator` for Cloud Run health probes).
- **Persistence & Database:**
  - **Primary Database:** PostgreSQL 16+ (local Docker Compose / Testcontainers; **Google Cloud SQL for PostgreSQL 16** in GCP via Cloud SQL Java Socket Factory / Private IP).
  - **Data Access:** Spring Data JDBC / Spring Data JPA (Hibernate) with explicit domain-to-entity mapping to preserve hexagonal domain purity.
  - **Schema Migrations:** **Flyway** (`db/migration/V1__*.sql`) with strict schema validation on startup (`ddl-auto: validate`).
- **Hexagonal Ports, Adapters & Domain Engines:**
  - **Workday Integration Boundary (`WorkdayDirectoryPort`):**
    1. `MockWorkdayAdapter` (`@Profile({"local", "test"})` or config toggle `teamliness.workday.mode=mock`): Seeded with realistic hierarchical organisation fixtures (`Division -> Department -> Manager -> Employee`, including unallocated, under-allocated, over-allocated, fragmented, and inactive leaver employees).
    2. `WorkdayRaaSAdapter` (`teamliness.workday.mode=raas`): Spring `RestClient` consuming Workday Report-as-a-Service (RaaS) / HCM REST JSON endpoints with idempotent upserts and soft-deactivation (`Inactive`) for leavers.
  - **Team Skill Aggregation Service:** Computes team-level skill rollups and category maturity heatmaps (`coverageCount`, `maxProficiency`, `averageProficiencyScore`, `benchDepth`) across active team members.
  - **Organisational & Capacity Diagnostics Engine:** Evaluates organisation-wide health rules (`/api/v1/diagnostics`) covering Unallocated (`0%`) & Under-Allocated (`<100%`) employees, Over-Allocated (`>100%`) & Multi-Team Fragmented (`>2` concurrent teams) employees, Active Workday Leavers & Leadless Teams, and Outcome Coverage Gaps.
  - **Jira Integration Boundary:** Dedicated read-only REST endpoint (`/api/v1/integrations/jira/teams`) exposing canonical active teams, hierarchy, leads, members, and Jira Project Key mappings.
- **API Documentation & Contracts:** SpringDoc OpenAPI 3 (`swagger-ui` / `/v3/api-docs`).
- **Backend Testing & ATDD HTML Living Documentation (Single-Run Mode):**
  - **Specification by Example / ATDD:** **Cucumber-JVM 7.x** (`io.cucumber:cucumber-java`, `io.cucumber:cucumber-spring`, `io.cucumber:cucumber-junit-platform-engine`) executing human-readable Gherkin `.feature` specifications (`backend/src/test/resources/features/**/*.feature`) with example tables.
  - **HTML Living Documentation Generator:** Built-in Cucumber HTML Plugin + **Masterthought / Serenity BDD HTML Living Documentation Reports** output to `backend/build/reports/living-docs/index.html` on every `./gradlew test` run.
  - **Unit & Integration Tests:** JUnit 5 (`jupiter`), AssertJ, Mockito, Spring Boot `MockMvc` / `RestTestClient`, and **Docker Testcontainers PostgreSQL** (`org.testcontainers:postgresql` + `@ServiceConnection`).

## 3. Frontend Stack (`frontend/`)
- **Language:** TypeScript 5.x (strict mode enabled).
- **Framework & Build Tool:** React 18+ with Vite.
- **State, Data Fetching & Routing:**
  - **Server State:** TanStack Query (React Query v5) for caching, optimistic updates, and sync invalidation.
  - **Data Grids & Matrices:** TanStack Table v8 for high-density Team Composition tables, Individual & Team-Aggregated Skill Matrix heatmaps, and Diagnostic issue tables.
  - **Routing:** React Router v6+.
- **Glassmorphic Design System & UI Components:**
  - **Tailwind CSS** configured with custom **Glassmorphic design tokens** (`backdrop-blur-md`/`xl`, translucent surface utilities `bg-slate-900/55`, `bg-white/10`, luminous specular borders `border-white/15`, and ambient radial mesh gradients).
  - Accessible headless primitives (Radix UI / Lucide icons) and high-contrast semantic operational badges (Amber for `>100%` over-allocation and `>2` team fragmentation, Cyan/Sky for `0%` unallocated / `<100%` under-allocated diagnostics, Red/Rose for `Workday Inactive / Leaver`, Read-only lock badges for Workday attributes).
- **Frontend Testing & ATDD HTML Living Documentation (Single-Run Mode):**
  - **Specification by Example / ATDD:** Gherkin `.feature` specifications (`frontend/src/features/**/*.feature`) executed via Vitest BDD / Playwright BDD in single-run mode, generating formatted **HTML Living Documentation Reports** into `frontend/reports/living-docs/index.html`.
  - **Unit & Component Tests:** Vitest (`vitest run` — strictly single-run, no watch mode) + React Testing Library + `@testing-library/user-event` + MSW (Mock Service Worker).

## 4. Google Cloud Platform (GCP) Infrastructure, CI/CD & Local Development
- **Local Development:**
  - `docker-compose.yml` providing PostgreSQL 16 for local development.
  - **Build & Package Management:** **Gradle** (`./gradlew`, `build.gradle`) for `backend/`; `npm` for `frontend/`.
- **Google Cloud Platform (GCP) Target Architecture:**
  - **Compute:** **Google Cloud Run** hosting two stateless, auto-scaling container services:
    1. `teamliness-backend` (multi-stage Eclipse Temurin JRE 21 container running Spring Boot with `/actuator/health` probes).
    2. `teamliness-frontend` (multi-stage Node build + Nginx container serving the production Vite SPA and reverse-proxying `/api` requests or routing to the backend Cloud Run URL).
  - **Database:** **Google Cloud SQL for PostgreSQL 16** connected securely from Cloud Run via the Cloud SQL Auth Proxy Unix socket / Private IP (`spring-cloud-gcp-starter-sql-postgresql` / JDBC SocketFactory) with secrets managed in **Google Secret Manager**.
  - **Container Registry:** **Google Artifact Registry** (`<region>-docker.pkg.dev/<project-id>/teamliness/...`).
- **Automated Push & Continuous Deployment (CI/CD to GCP):**
  - **Google Cloud Build (`cloudbuild.yaml`):** Executes single-run backend and frontend unit + ATDD verification suites, generates HTML Living Documentation reports, builds and tags multi-stage Docker images, pushes to Artifact Registry, and deploys updated revisions to Cloud Run (`gcloud run deploy`).
  - **GitHub Actions (`.github/workflows/deploy-gcp.yml`):** Authenticates via keyless **GCP Workload Identity Federation** (`google-github-actions/auth`), runs `./gradlew test` and `npx vitest run`, uploads HTML Living Documentation artifacts, and triggers automated image push and Cloud Run deployment on every push to `main`.
