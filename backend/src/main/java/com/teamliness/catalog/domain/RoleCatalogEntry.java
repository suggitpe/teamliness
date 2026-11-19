package com.teamliness.catalog.domain;

/**
 * Standardized role entry in the curated Role Catalog.
 *
 * @param id          unique role ID
 * @param code        canonical role code (e.g. "TECH_LEAD")
 * @param displayName human-readable role title
 * @param description role responsibilities summary
 * @param active      whether the role is active for team membership assignment
 */
public record RoleCatalogEntry(
        String id,
        String code,
        String displayName,
        String description,
        boolean active
) {
}
