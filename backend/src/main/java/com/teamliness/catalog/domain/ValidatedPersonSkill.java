package com.teamliness.catalog.domain;

/**
 * Validated skill proficiency record assigned to an employee.
 *
 * @param id                 surrogate record ID
 * @param personId           employee surrogate ID
 * @param skillId            standardized skill ID
 * @param proficiencyLevel   proficiency rating ("BEGINNER", "INTERMEDIATE", or "EXPERT")
 * @param validatedByPersona RBAC persona that validated the assignment
 */
public record ValidatedPersonSkill(
        String id,
        String personId,
        String skillId,
        String proficiencyLevel,
        String validatedByPersona
) {
}
