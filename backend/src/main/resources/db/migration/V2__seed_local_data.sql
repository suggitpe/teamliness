-- V2__seed_local_data.sql
-- Seed baseline hierarchical Org Units, Standardized Role & Skill Catalogs, Workday Employees, and Teams

INSERT INTO org_units (id, name, unit_type, parent_id, workday_department_code) VALUES
('ou-div-eng', 'Core Engineering & Platform Division', 'DIVISION', NULL, 'WD-DIV-ENG'),
('ou-tribe-plat', 'Cloud & Platform Foundations Tribe', 'TRIBE', 'ou-div-eng', 'WD-TRIBE-PLAT'),
('ou-tribe-prod', 'Digital Payments & Experience Tribe', 'TRIBE', 'ou-div-eng', 'WD-TRIBE-PAY');

INSERT INTO roles_catalog (id, code, display_name, description, active) VALUES
('role-tl', 'TECH_LEAD', 'Tech Lead', 'Accountable for technical architecture, engineering quality, and team delivery mentorship.', TRUE),
('role-po', 'PRODUCT_OWNER', 'Product Owner', 'Accountable for outcome prioritization, backlog alignment, and stakeholder value.', TRUE),
('role-swe', 'SOFTWARE_ENGINEER', 'Software Engineer', 'Designs, builds, tests, and operates full-stack and backend product capabilities.', TRUE),
('role-pe', 'PLATFORM_ENGINEER', 'Platform Engineer', 'Builds cloud infrastructure, CI/CD pipelines, reliability tooling, and developer platforms.', TRUE),
('role-dm', 'DELIVERY_MANAGER', 'Delivery Manager', 'Facilitates cross-team flow, dependency resolution, and agile delivery cadence.', TRUE);

INSERT INTO skills_catalog (id, code, display_name, category, description, active) VALUES
('skill-java-spring', 'JAVA_SPRING_BOOT', 'Java 21 & Spring Boot', 'Backend', 'Enterprise hexagonal microservices and modular monolith architecture with Java 21 and Spring Boot 3.', TRUE),
('skill-postgres', 'POSTGRESQL_DATA', 'PostgreSQL & Relational Modeling', 'Data & Analytics', 'Schema design, Flyway migrations, query tuning, and transactional integrity.', TRUE),
('skill-react-ts', 'REACT_TYPESCRIPT', 'React 18 & TypeScript', 'Frontend', 'Single-page applications with TypeScript, TanStack Query/Table, and Glassmorphic UI.', TRUE),
('skill-gcp-cloudrun', 'GCP_CLOUD_RUN', 'GCP Cloud Run & Cloud SQL', 'Cloud & DevOps', 'Containerized cloud-native deployment, Workload Identity Federation, and Cloud Build.', TRUE),
('skill-atdd-bdd', 'ATDD_LIVING_DOCS', 'ATDD & Specification by Example', 'Product & Delivery', 'Gherkin specification authoring, Cucumber-JVM, and HTML Living Documentation.', TRUE),
('skill-k8s-terraform', 'TERRAFORM_IAC', 'Terraform & Infrastructure as Code', 'Cloud & DevOps', 'Declarative cloud infrastructure provisioning and policy automation.', TRUE);

INSERT INTO persons (id, employee_id, full_name, email, job_title, department, cost_centre, manager_employee_id, employment_status) VALUES
('per-101', 'WD-1001', 'Elena Rostova', 'elena.rostova@teamliness.io', 'Principal Platform Architect', 'Cloud & Platform Foundations', 'CC-4100', NULL, 'Active'),
('per-102', 'WD-1002', 'Marcus Vance', 'marcus.vance@teamliness.io', 'Lead Software Engineer', 'Digital Payments & Experience', 'CC-4200', 'WD-1001', 'Active'),
('per-103', 'WD-1003', 'Priya Nair', 'priya.nair@teamliness.io', 'Senior Software Engineer', 'Digital Payments & Experience', 'CC-4200', 'WD-1002', 'Active'),
('per-104', 'WD-1004', 'Devon Brooks', 'devon.brooks@teamliness.io', 'Staff Platform Engineer', 'Cloud & Platform Foundations', 'CC-4100', 'WD-1001', 'Active');

INSERT INTO teams (id, name, mission_statement, org_unit_id, team_lead_person_id, jira_project_key, jira_board_url, active) VALUES
('team-plat-core', 'Platform Core & Runtime', 'Provide resilient GCP Cloud Run foundations, developer tooling, and automated deployment pipelines.', 'ou-tribe-plat', 'per-101', 'PLAT', 'https://jira.teamliness.io/boards/PLAT', TRUE),
('team-pay-exp', 'Payments Experience & Checkout', 'Deliver seamless, high-conversion checkout and payment orchestration flows.', 'ou-tribe-prod', 'per-102', 'PAY', 'https://jira.teamliness.io/boards/PAY', TRUE);
