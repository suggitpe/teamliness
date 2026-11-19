package com.teamliness.system.domain;

/**
 * Immutable domain snapshot representing the platform health, schema readiness,
 * and UI/living-documentation configuration contract.
 *
 * @param applicationName   canonical application name
 * @param databaseStatus    relational database readiness status ("UP" or "DOWN")
 * @param schemaVersion     highest applied Flyway migration version
 * @param uiTheme           active UI theme identifier ("GLASSMORPHIC")
 * @param livingDocsEnabled whether ATDD HTML Living Documentation generation is enabled
 */
public record SystemStatusSnapshot(
        String applicationName,
        String databaseStatus,
        String schemaVersion,
        String uiTheme,
        boolean livingDocsEnabled
) {
}
