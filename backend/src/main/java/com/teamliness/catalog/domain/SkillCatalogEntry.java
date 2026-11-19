package com.teamliness.catalog.domain;

/**
 * Standardized skill entry in the curated Skill Catalog.
 *
 * @param id          unique skill ID
 * @param code        canonical skill code (e.g. "JAVA_SPRING_BOOT")
 * @param displayName human-readable skill name
 * @param category    high-level capability category (e.g. "Backend", "Frontend", "Cloud & DevOps")
 * @param description capability description
 * @param active      whether the skill is active for employee tagging
 */
public record SkillCatalogEntry(
        String id,
        String code,
        String displayName,
        String category,
        String description,
        boolean active
) {
}
