Feature: Track 01 — Platform Foundation, Schema Readiness & Living Documentation Harness
  As a platform operator and engineering leader
  I want the Teamliness Spring Boot backend and PostgreSQL schema to initialize cleanly with Flyway migrations and expose a system status contract
  So that downstream organisational hierarchy, Workday sync, and capacity diagnostics have a verified relational foundation and HTML Living Documentation pipeline.

  Rule: The system status endpoint reports database connectivity, active Flyway schema version, and UI theme contract

    Scenario: Platform health check & core database schema readiness
      Given the PostgreSQL database is migrated via Flyway with migrations "V1__init_schema.sql" and "V2__seed_local_data.sql"
      When an operator queries "GET /api/v1/system/status"
      Then the response status code is 200
      And the system status payload matches:
        | applicationName | databaseStatus | schemaVersion | uiTheme       | livingDocsEnabled |
        | Teamliness      | UP             | 2             | GLASSMORPHIC  | true              |

  Rule: Foundational relational tables and seeded baseline records exist after Flyway V1 and V2 migrations

    Scenario Outline: Core foundational tables are created and populated with baseline seed data
      Given the PostgreSQL database is migrated to schema version "2"
      When the operator inspects the "<table_name>" table
      Then the table exists and contains at least <min_rows> baseline rows

      Examples:
        | table_name         | min_rows |
        | org_units          | 3        |
        | roles_catalog      | 5        |
        | skills_catalog     | 6        |
        | persons            | 4        |
        | teams              | 2        |
