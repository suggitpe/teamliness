Feature: Track 02 — Workday Directory Sync (WorkdayDirectoryPort) & Leaver Soft-Deactivation
  As an Admin or Engineering Leader
  I want Teamliness to synchronize employee system-of-record attributes from Workday via WorkdayDirectoryPort
  So that employee records are upserted idempotently without duplicating data, local skill tags are preserved, and leavers are soft-deactivated to Inactive status.

  Rule: Workday directory synchronization performs idempotent upserts, preserves local enrichments, and soft-deactivates leavers

    Scenario: Admin triggers an idempotent Workday synchronization run via POST /api/v1/workday/sync
      Given the local person directory contains baseline Workday employees including "WD-1001" and an employee "WD-1099" who has left the company
      And employee "WD-1001" has a locally validated skill "skill-java-spring" at proficiency "Expert"
      When an Admin triggers Workday synchronization via "POST /api/v1/workday/sync" twice consecutively
      Then the synchronization response status code is 200
      And the synchronization summary reports adapterMode "MOCK", upsertedCount 6, and deactivatedCount 1
      And employee "WD-1001" retains the locally validated skill "skill-java-spring" at proficiency "Expert"

    Scenario Outline: Synchronized person directory via GET /api/v1/persons reflects Workday attributes and soft-deactivated leavers
      Given Workday directory synchronization has completed
      When a consumer queries "GET /api/v1/persons"
      Then the directory contains employee "<employee_id>" named "<full_name>" with title "<job_title>", department "<department>", and status "<employment_status>"

      Examples:
        | employee_id | full_name      | job_title                    | department                     | employment_status |
        | WD-1001     | Elena Rostova  | Principal Platform Architect | Cloud & Platform Foundations   | Active            |
        | WD-1002     | Marcus Vance   | Lead Software Engineer       | Digital Payments & Experience  | Active            |
        | WD-1005     | Sofia Lindqvist| Staff Data Engineer          | Cloud & Platform Foundations   | Active            |
        | WD-1006     | Liam O'Connor  | Senior Frontend Engineer     | Digital Payments & Experience  | Active            |
        | WD-1099     | Arthur Pendelton| Former Staff Engineer       | Cloud & Platform Foundations   | Inactive          |

  Rule: WorkdayRaaSAdapter parses Workday Report-as-a-Service (RaaS) JSON payloads behind WorkdayDirectoryPort

    Scenario: WorkdayRaaSAdapter maps Report_Entry JSON payloads into canonical Workday directory records
      Given a Workday RaaS JSON payload containing Report_Entry rows for "WD-2001" (Active) and "WD-2002" (Terminated)
      When the WorkdayRaaSAdapter fetches directory records from the configured RaaS endpoint
      Then the adapter returns 2 canonical Workday records with employment statuses "Active" and "Inactive"
