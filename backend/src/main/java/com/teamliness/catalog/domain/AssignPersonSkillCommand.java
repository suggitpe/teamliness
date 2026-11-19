package com.teamliness.catalog.domain;

/**
 * Command payload for assigning a direct validated skill proficiency to an employee.
 *
 * @param skillId          standardized catalog skill ID
 * @param proficiencyLevel proficiency tier ("BEGINNER", "INTERMEDIATE", or "EXPERT")
 */
public record AssignPersonSkillCommand(
        String skillId,
        String proficiencyLevel
) {
}
