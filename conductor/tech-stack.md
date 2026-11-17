# Technology Stack: Teamliness

## 1. Architecture Overview
- **Pattern:** Client-Server application with a **Hexagonal / Modular Monolith Backend** and a **Single-Page Application (SPA) Frontend**.
- **Repository Structure:** Multi-module workspace containing `backend/` (Java 21 + Spring Boot 3) and `frontend/` (TypeScript + React 18+ + Vite).

## 2. Backend Stack (`backend/`)
- **Language:** Java 21 LTS (leveraging records, sealed interfaces, pattern matching, and virtual threads where appropriate).
- **Framework:** Spring Boot 3.x (`spring-boot-starter-web`, `spring-boot-starter-validation`, `spring-boot-starter-security` / RBAC filter).
- **Persistence & Database:**
  - **Primary Database:** PostgreSQL 16+.
  - **Data Access:** Spring Data JDBC / Spring Data JPA (Hibernate) with explicit domain-to-entity mapping to preserve hexagonal domain purity.
  - **Schema Migrations:** **Flyway** (`db/migration/V1__*.sql`) with strict schema validation on startup (`ddl-auto: validate`).
- **Hexagonal Ports & Adapters:**
  - **Workday Integration Boundary (`WorkdayDirectoryPort`):**
    1. `MockWorkdayAdapter` (`@Profile({"local", "test"})` or config toggle `teamliness.workday.mode=mock`): Seeded with realistic hierarchical organisation fixtures (`Division -> Department -> Manager -> Employee`).
    2. `WorkdayRaaSAdapter` (`teamliness.workday.mode=raas`): Spring `RestClient` consuming Workday Report-as-a-Service (RaaS) / HCM REST JSON endpoints with idempotent upserts and soft-deactivation (`Inactive`) for leavers.
  - **Jira Integration Boundary:** Dedicated read-only REST endpoint (`/api/v1/integrations/jira/teams`) exposing canonical active teams, hierarchy, leads, members, and Jira Project Key mappings.
- **API Documentation & Contracts:** SpringDoc OpenAPI 3 (`swagger-ui` / `/v3/api-docs`).
- **Backend Testing (Single-Run TDD):**
  - JUnit 5 (`jupiter`), AssertJ, Mockito, and Spring Boot `MockMvc` / `RestTestClient`.
  - Database integration tests backed strictly by **Docker Testcontainers PostgreSQL** (`org.testcontainers:postgresql` + `@ServiceConnection`).

## 3. Frontend Stack (`frontend/`)
- **Language:** TypeScript 5.x (strict mode enabled).
- **Framework & Build Tool:** React 18+ with Vite.
- **State, Data Fetching & Routing:**
  - **Server State:** TanStack Query (React Query v5) for caching, optimistic updates, and sync invalidation.
  - **Data Grids & Matrices:** TanStack Table v8 for high-density Team Composition tables and Skill Matrix heatmaps.
  - **Routing:** React Router v6+.
- **Styling & UI Components:** Tailwind CSS with accessible headless primitives (Radix UI / Lucide icons) and semantic operational badges (Amber for `>100%` over-allocation, Red/Rose for `Workday Inactive / Leaver`, Read-only lock badges for Workday attributes).
- **Frontend Testing (Single-Run TDD):**
  - **Unit & Component Tests:** Vitest (`vitest run` — strictly single-run, no watch mode) + React Testing Library + `@testing-library/user-event` + MSW (Mock Service Worker).

## 4. Infrastructure & Local Development
- **Database Containerization:** `docker-compose.yml` providing PostgreSQL 16 for local development.
- **Build & Package Management:** **Gradle** (`./gradlew`, `build.gradle`) for `backend/`; `npm` for `frontend/`.
