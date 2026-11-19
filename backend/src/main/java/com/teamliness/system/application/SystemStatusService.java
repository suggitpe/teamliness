package com.teamliness.system.application;

import com.teamliness.system.domain.SystemStatusSnapshot;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

/**
 * Application service that inspects database connectivity and Flyway schema history
 * to report current platform readiness.
 */
@Service
public class SystemStatusService {

    private final JdbcTemplate jdbcTemplate;
    private final String applicationName;
    private final String uiTheme;
    private final boolean livingDocsEnabled;

    /**
     * Constructs the {@link SystemStatusService}.
     *
     * @param jdbcTemplate      Spring JDBC template for querying Flyway metadata
     * @param applicationName   configured application name
     * @param uiTheme           configured UI theme contract
     * @param livingDocsEnabled configured living documentation flag
     */
    public SystemStatusService(
            JdbcTemplate jdbcTemplate,
            @Value("${spring.application.name:Teamliness}") String applicationName,
            @Value("${teamliness.ui-theme:GLASSMORPHIC}") String uiTheme,
            @Value("${teamliness.living-docs-enabled:true}") boolean livingDocsEnabled
    ) {
        this.jdbcTemplate = jdbcTemplate;
        this.applicationName = applicationName;
        this.uiTheme = uiTheme;
        this.livingDocsEnabled = livingDocsEnabled;
    }

    /**
     * Inspects the relational database and returns a {@link SystemStatusSnapshot}.
     *
     * @return current system status snapshot
     */
    public SystemStatusSnapshot getSystemStatus() {
        try {
            String version = jdbcTemplate.queryForObject(
                    "SELECT \"version\" FROM \"flyway_schema_history\" WHERE \"success\" = TRUE ORDER BY \"installed_rank\" DESC LIMIT 1",
                    String.class
            );
            String resolvedVersion = (version != null && !version.isBlank()) ? version : "0";
            return new SystemStatusSnapshot(
                    applicationName,
                    "UP",
                    resolvedVersion,
                    uiTheme,
                    livingDocsEnabled
            );
        } catch (Exception ex) {
            return new SystemStatusSnapshot(
                    applicationName,
                    "DOWN",
                    "UNKNOWN",
                    uiTheme,
                    livingDocsEnabled
            );
        }
    }
}
