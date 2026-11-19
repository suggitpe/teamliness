package com.teamliness.atdd;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/**
 * Step definitions for {@code track_01_platform_foundation.feature}.
 */
public class Track01PlatformFoundationSteps {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private MvcResult latestResult;
    private Integer latestTableRowCount;

    @Given("the PostgreSQL database is migrated via Flyway with migrations {string} and {string}")
    public void thePostgresqlDatabaseIsMigratedViaFlywayWithMigrations(String migrationOne, String migrationTwo) {
        Integer appliedCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM \"flyway_schema_history\" WHERE \"success\" = TRUE AND \"script\" IN (?, ?)",
                Integer.class,
                migrationOne,
                migrationTwo
        );
        assertThat(appliedCount)
                .as("Expected Flyway migrations %s and %s to be applied", migrationOne, migrationTwo)
                .isEqualTo(2);
    }

    @When("an operator queries {string}")
    public void anOperatorQueries(String methodAndPath) throws Exception {
        String[] parts = methodAndPath.split(" ", 2);
        String path = parts.length == 2 ? parts[1] : parts[0];
        this.latestResult = mockMvc.perform(get(path)).andReturn();
    }

    @Then("the response status code is {int}")
    public void theResponseStatusCodeIs(int expectedStatus) {
        assertThat(latestResult.getResponse().getStatus()).isEqualTo(expectedStatus);
    }

    @Then("the system status payload matches:")
    public void theSystemStatusPayloadMatches(DataTable dataTable) throws Exception {
        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
        Map<String, String> expected = rows.getFirst();

        JsonNode json = objectMapper.readTree(latestResult.getResponse().getContentAsString());
        assertThat(json.path("applicationName").asText()).isEqualTo(expected.get("applicationName"));
        assertThat(json.path("databaseStatus").asText()).isEqualTo(expected.get("databaseStatus"));
        assertThat(json.path("schemaVersion").asText()).isEqualTo(expected.get("schemaVersion"));
        assertThat(json.path("uiTheme").asText()).isEqualTo(expected.get("uiTheme"));
        assertThat(String.valueOf(json.path("livingDocsEnabled").asBoolean())).isEqualTo(expected.get("livingDocsEnabled"));
    }

    @Given("the PostgreSQL database is migrated to schema version {string}")
    public void thePostgresqlDatabaseIsMigratedToSchemaVersion(String expectedVersion) {
        String currentVersion = jdbcTemplate.queryForObject(
                "SELECT \"version\" FROM \"flyway_schema_history\" WHERE \"success\" = TRUE ORDER BY \"installed_rank\" DESC LIMIT 1",
                String.class
        );
        assertThat(currentVersion).isEqualTo(expectedVersion);
    }

    @When("the operator inspects the {string} table")
    public void theOperatorInspectsTheTable(String tableName) {
        String sanitizedTable = tableName.replaceAll("[^a-zA-Z0-9_]", "");
        this.latestTableRowCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM " + sanitizedTable,
                Integer.class
        );
    }

    @Then("the table exists and contains at least {int} baseline rows")
    public void theTableExistsAndContainsAtLeastBaselineRows(int minRows) {
        assertThat(latestTableRowCount)
                .as("Expected table row count to be at least %d", minRows)
                .isGreaterThanOrEqualTo(minRows);
    }
}
