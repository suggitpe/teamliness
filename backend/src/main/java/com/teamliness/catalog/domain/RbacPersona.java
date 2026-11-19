package com.teamliness.catalog.domain;

import java.util.Locale;

/**
 * Role-Based Access Control (RBAC) personas enforced across the Teamliness REST API and SPA.
 */
public enum RbacPersona {
    ADMIN,
    ORG_LEADER,
    TEAM_LEAD,
    READ_ONLY_VIEWER;

    /**
     * Parses a persona header value (e.g., "ADMIN", "Org Leader", "Team Lead", "Read-Only Viewer")
     * into a canonical {@link RbacPersona}.
     *
     * @param rawHeader raw HTTP header value
     * @return resolved {@link RbacPersona}, defaulting to {@link #READ_ONLY_VIEWER} when unrecognized
     */
    public static RbacPersona fromHeader(String rawHeader) {
        if (rawHeader == null || rawHeader.isBlank()) {
            return READ_ONLY_VIEWER;
        }
        String normalized = rawHeader.trim()
                .toUpperCase(Locale.ROOT)
                .replace('-', '_')
                .replace(' ', '_');
        try {
            return RbacPersona.valueOf(normalized);
        } catch (IllegalArgumentException ex) {
            return READ_ONLY_VIEWER;
        }
    }

    /**
     * Returns whether this persona is authorized to mutate the standardized Role & Skill Catalog.
     *
     * @return true only for {@link #ADMIN}
     */
    public boolean canMutateCatalog() {
        return this == ADMIN;
    }

    /**
     * Returns whether this persona is authorized to assign direct validated skills to employees.
     *
     * @return true for {@link #ADMIN}, {@link #ORG_LEADER}, and {@link #TEAM_LEAD}
     */
    public boolean canAssignValidatedSkills() {
        return this == ADMIN || this == ORG_LEADER || this == TEAM_LEAD;
    }
}
