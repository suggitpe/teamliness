-- V1__init_schema.sql
-- Foundational PostgreSQL 16 schema for Teamliness (Organisational Team & Outcome Management)

CREATE TABLE org_units (
    id VARCHAR(64) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    unit_type VARCHAR(64) NOT NULL,
    parent_id VARCHAR(64) REFERENCES org_units(id),
    workday_department_code VARCHAR(128),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE TABLE roles_catalog (
    id VARCHAR(64) PRIMARY KEY,
    code VARCHAR(64) NOT NULL UNIQUE,
    display_name VARCHAR(128) NOT NULL,
    description VARCHAR(512),
    active BOOLEAN DEFAULT TRUE NOT NULL
);

CREATE TABLE skills_catalog (
    id VARCHAR(64) PRIMARY KEY,
    code VARCHAR(64) NOT NULL UNIQUE,
    display_name VARCHAR(128) NOT NULL,
    category VARCHAR(128) NOT NULL,
    description VARCHAR(512),
    active BOOLEAN DEFAULT TRUE NOT NULL
);

CREATE TABLE persons (
    id VARCHAR(64) PRIMARY KEY,
    employee_id VARCHAR(64) NOT NULL UNIQUE,
    full_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    job_title VARCHAR(255) NOT NULL,
    department VARCHAR(255) NOT NULL,
    cost_centre VARCHAR(128) NOT NULL,
    manager_employee_id VARCHAR(64),
    employment_status VARCHAR(32) NOT NULL,
    last_synced_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE TABLE person_skills (
    id VARCHAR(64) PRIMARY KEY,
    person_id VARCHAR(64) NOT NULL REFERENCES persons(id) ON DELETE CASCADE,
    skill_id VARCHAR(64) NOT NULL REFERENCES skills_catalog(id) ON DELETE CASCADE,
    proficiency_level VARCHAR(32) NOT NULL,
    validated_by_persona VARCHAR(64) NOT NULL,
    validated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT uq_person_skill UNIQUE (person_id, skill_id)
);

CREATE TABLE teams (
    id VARCHAR(64) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    mission_statement VARCHAR(1024),
    org_unit_id VARCHAR(64) NOT NULL REFERENCES org_units(id),
    team_lead_person_id VARCHAR(64) REFERENCES persons(id),
    jira_project_key VARCHAR(64),
    jira_board_url VARCHAR(512),
    active BOOLEAN DEFAULT TRUE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE TABLE team_memberships (
    id VARCHAR(64) PRIMARY KEY,
    team_id VARCHAR(64) NOT NULL REFERENCES teams(id) ON DELETE CASCADE,
    person_id VARCHAR(64) NOT NULL REFERENCES persons(id),
    role_id VARCHAR(64) NOT NULL REFERENCES roles_catalog(id),
    allocation_percentage INT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE TABLE outcomes (
    id VARCHAR(64) PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description VARCHAR(2048),
    target_date DATE NOT NULL,
    status VARCHAR(64) NOT NULL,
    key_results_summary VARCHAR(2048),
    jira_epic_key VARCHAR(64),
    jira_issue_url VARCHAR(512),
    jira_jql_query VARCHAR(1024),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE TABLE outcome_team_commitments (
    id VARCHAR(64) PRIMARY KEY,
    outcome_id VARCHAR(64) NOT NULL REFERENCES outcomes(id) ON DELETE CASCADE,
    team_id VARCHAR(64) NOT NULL REFERENCES teams(id) ON DELETE CASCADE,
    allocation_mode VARCHAR(64) NOT NULL,
    allocation_rationale VARCHAR(1024),
    committed_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT uq_outcome_team UNIQUE (outcome_id, team_id)
);

CREATE TABLE outcome_contributors (
    id VARCHAR(64) PRIMARY KEY,
    outcome_id VARCHAR(64) NOT NULL REFERENCES outcomes(id) ON DELETE CASCADE,
    person_id VARCHAR(64) NOT NULL REFERENCES persons(id),
    contribution_role VARCHAR(128) NOT NULL,
    contribution_notes VARCHAR(1024),
    active BOOLEAN DEFAULT TRUE NOT NULL
);

CREATE TABLE workday_sync_audit (
    id VARCHAR(64) PRIMARY KEY,
    adapter_mode VARCHAR(32) NOT NULL,
    triggered_by VARCHAR(128) NOT NULL,
    upserted_count INT NOT NULL,
    deactivated_count INT NOT NULL,
    executed_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);
