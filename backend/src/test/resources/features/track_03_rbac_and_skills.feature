Feature: Track 03 — RBAC Personas, Skill & Role Catalog & Direct Validated Skill Tagging
  As an Admin, Org Leader, Team Lead, or Read-Only Viewer
  I want Teamliness to enforce Role-Based Access Control (RBAC) across the Standardized Role & Skill Catalogs and direct employee skill proficiency tagging
  So that skills are curated consistently and validated directly by authorized leaders without an approval queue.

  Rule: Standardized Role and Skill Catalogs are readable by all personas and mutable exclusively by Admin

    Scenario: Consumer fetches standardized Role and Skill Catalogs
      Given the database is seeded with standardized roles and skills
      When a consumer with persona "READ_ONLY_VIEWER" queries "GET /api/v1/catalog/roles" and "GET /api/v1/catalog/skills"
      Then both catalog responses return status code 200 with at least 5 roles and 6 skills

    Scenario Outline: Persona permission matrix for creating a new skill in the catalog via POST /api/v1/catalog/skills
      Given an authenticated caller with persona "<persona_role>" and person ID "<actor_person_id>"
      When the caller submits a new catalog skill with code "<skill_code>", name "<display_name>", and category "<category>" to "POST /api/v1/catalog/skills"
      Then the catalog creation response status code is <expected_status>

      Examples:
        | persona_role     | actor_person_id | skill_code         | display_name           | category         | expected_status |
        | ADMIN            | per-101         | KAFKA_EVENT_STREAM | Kafka Event Streaming  | Backend          | 201             |
        | ORG_LEADER       | per-101         | GRAPHQL_FEDERATION | GraphQL Federation     | Backend          | 403             |
        | TEAM_LEAD        | per-102         | PLAYWRIGHT_E2E     | Playwright E2E Testing | Frontend         | 403             |
        | READ_ONLY_VIEWER | per-104         | BIGQUERY_ANALYTICS | BigQuery Analytics     | Data & Analytics | 403             |

  Rule: Authorized leaders assign validated skill proficiencies (BEGINNER, INTERMEDIATE, EXPERT) directly to employees

    Scenario Outline: Persona permission matrix for direct validated skill assignment via POST /api/v1/persons/{personId}/skills
      Given an authenticated caller with persona "<persona_role>" and person ID "<actor_person_id>"
      When the caller assigns skill "<skill_id>" at proficiency "<proficiency_level>" to person "<target_person_id>"
      Then the skill assignment response status code is <expected_status>
      And if permitted, the validated skill record for "<target_person_id>" and "<skill_id>" persists proficiency "<proficiency_level>" validated by "<persona_role>"

      Examples:
        | persona_role     | actor_person_id | target_person_id | skill_id          | proficiency_level | expected_status |
        | ADMIN            | per-101         | per-103          | skill-java-spring | EXPERT            | 200             |
        | ORG_LEADER       | per-101         | per-103          | skill-react-ts    | INTERMEDIATE      | 200             |
        | TEAM_LEAD        | per-102         | per-103          | skill-postgres    | BEGINNER          | 200             |
        | READ_ONLY_VIEWER | per-104         | per-103          | skill-gcp-cloudrun| EXPERT            | 403             |
