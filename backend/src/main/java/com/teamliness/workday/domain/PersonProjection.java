package com.teamliness.workday.domain;

/**
 * Read-only local projection of a Workday employee record exposed via {@code GET /api/v1/persons}.
 *
 * @param id                internal surrogate ID
 * @param employeeId        external Workday Employee ID
 * @param fullName          employee full name
 * @param email             corporate email address
 * @param jobTitle          Workday business title
 * @param department        Workday department
 * @param costCentre        Workday cost centre code
 * @param managerEmployeeId manager's Workday Employee ID
 * @param employmentStatus  employment status ("Active" or "Inactive")
 * @param syncedFromWorkday system-of-record indicator flag (always true for Workday-sourced projections)
 */
public record PersonProjection(
        String id,
        String employeeId,
        String fullName,
        String email,
        String jobTitle,
        String department,
        String costCentre,
        String managerEmployeeId,
        String employmentStatus,
        boolean syncedFromWorkday
) {
}
