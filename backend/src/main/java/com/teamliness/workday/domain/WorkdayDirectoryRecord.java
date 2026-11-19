package com.teamliness.workday.domain;

/**
 * Canonical employee projection fetched from Workday via {@code WorkdayDirectoryPort}.
 *
 * @param employeeId        external Workday Employee ID (e.g. "WD-1001")
 * @param fullName          employee full name
 * @param email             corporate email address
 * @param jobTitle          business title in Workday
 * @param department        supervisory organization / department
 * @param costCentre        cost centre code
 * @param managerEmployeeId manager's Workday Employee ID (nullable for top-level leaders)
 * @param employmentStatus  normalized employment status ("Active" or "Inactive")
 */
public record WorkdayDirectoryRecord(
        String employeeId,
        String fullName,
        String email,
        String jobTitle,
        String department,
        String costCentre,
        String managerEmployeeId,
        String employmentStatus
) {
}
