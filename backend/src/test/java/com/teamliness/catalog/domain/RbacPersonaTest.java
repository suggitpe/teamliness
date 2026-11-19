package com.teamliness.catalog.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link RbacPersona}.
 */
class RbacPersonaTest {

    @Test
    void parsesFormattedAndEnumHeadersCorrectly() {
        assertThat(RbacPersona.fromHeader("Admin")).isEqualTo(RbacPersona.ADMIN);
        assertThat(RbacPersona.fromHeader("ORG_LEADER")).isEqualTo(RbacPersona.ORG_LEADER);
        assertThat(RbacPersona.fromHeader("Team Lead")).isEqualTo(RbacPersona.TEAM_LEAD);
        assertThat(RbacPersona.fromHeader("Read-Only Viewer")).isEqualTo(RbacPersona.READ_ONLY_VIEWER);
        assertThat(RbacPersona.fromHeader(null)).isEqualTo(RbacPersona.READ_ONLY_VIEWER);
        assertThat(RbacPersona.fromHeader("UNKNOWN_ROLE")).isEqualTo(RbacPersona.READ_ONLY_VIEWER);
    }

    @Test
    void enforcesCatalogAndSkillPermissionsByPersona() {
        assertThat(RbacPersona.ADMIN.canMutateCatalog()).isTrue();
        assertThat(RbacPersona.ORG_LEADER.canMutateCatalog()).isFalse();
        assertThat(RbacPersona.TEAM_LEAD.canMutateCatalog()).isFalse();
        assertThat(RbacPersona.READ_ONLY_VIEWER.canMutateCatalog()).isFalse();

        assertThat(RbacPersona.ADMIN.canAssignValidatedSkills()).isTrue();
        assertThat(RbacPersona.ORG_LEADER.canAssignValidatedSkills()).isTrue();
        assertThat(RbacPersona.TEAM_LEAD.canAssignValidatedSkills()).isTrue();
        assertThat(RbacPersona.READ_ONLY_VIEWER.canAssignValidatedSkills()).isFalse();
    }
}
