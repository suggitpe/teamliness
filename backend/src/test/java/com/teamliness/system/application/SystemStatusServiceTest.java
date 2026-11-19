package com.teamliness.system.application;

import com.teamliness.system.domain.SystemStatusSnapshot;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link SystemStatusService}.
 */
class SystemStatusServiceTest {

    @Test
    void returnsUpStatusAndLatestFlywaySchemaVersionWhenDatabaseResponds() {
        JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);
        when(jdbcTemplate.queryForObject(anyString(), eq(String.class))).thenReturn("2");

        SystemStatusService service = new SystemStatusService(jdbcTemplate, "Teamliness", "GLASSMORPHIC", true);
        SystemStatusSnapshot snapshot = service.getSystemStatus();

        assertThat(snapshot.applicationName()).isEqualTo("Teamliness");
        assertThat(snapshot.databaseStatus()).isEqualTo("UP");
        assertThat(snapshot.schemaVersion()).isEqualTo("2");
        assertThat(snapshot.uiTheme()).isEqualTo("GLASSMORPHIC");
        assertThat(snapshot.livingDocsEnabled()).isTrue();
    }

    @Test
    void returnsZeroVersionWhenFlywayTableIsEmpty() {
        JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);
        when(jdbcTemplate.queryForObject(anyString(), eq(String.class))).thenReturn("");

        SystemStatusService service = new SystemStatusService(jdbcTemplate, "Teamliness", "GLASSMORPHIC", true);
        SystemStatusSnapshot snapshot = service.getSystemStatus();

        assertThat(snapshot.databaseStatus()).isEqualTo("UP");
        assertThat(snapshot.schemaVersion()).isEqualTo("0");
    }

    @Test
    void returnsDownStatusWhenDatabaseQueryThrowsException() {
        JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);
        when(jdbcTemplate.queryForObject(anyString(), eq(String.class)))
                .thenThrow(new RuntimeException("Connection refused"));

        SystemStatusService service = new SystemStatusService(jdbcTemplate, "Teamliness", "GLASSMORPHIC", true);
        SystemStatusSnapshot snapshot = service.getSystemStatus();

        assertThat(snapshot.databaseStatus()).isEqualTo("DOWN");
        assertThat(snapshot.schemaVersion()).isEqualTo("UNKNOWN");
    }
}
