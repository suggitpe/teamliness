package com.teamliness.catalog.domain;

/**
 * Command payload for creating a standardized skill in the Skill Catalog.
 *
 * @param code        canonical skill code
 * @param displayName human-readable skill title
 * @param category    capability category
 * @param description skill description
 */
public record CreateSkillCommand(
        String code,
        String displayName,
        String category,
        String description
) {
}
