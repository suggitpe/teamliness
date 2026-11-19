package com.teamliness.atdd;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.client.RestClient;

import java.lang.reflect.Method;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

/**
 * Step definitions for {@code track_02_workday_sync.feature}.
 */
public class Track02WorkdaySyncSteps {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private MvcResult syncResult;
    private MvcResult personsResult;
    private String simulatedRaasJson;
    private List<?> parsedRaasRecords;

    @Given("the local person directory contains baseline Workday employees including {string} and an employee {string} who has left the company")
    public void theLocalPersonDirectoryContainsBaselineWorkdayEmployees(String existingEmpId, String leaverEmpId) {
        jdbcTemplate.update(
                "DELETE FROM persons WHERE employee_id = ?",
                leaverEmpId
        );
        jdbcTemplate.update(
                "INSERT INTO persons (id, employee_id, full_name, email, job_title, department, cost_centre, manager_employee_id, employment_status) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                "per-199",
                leaverEmpId,
                "Arthur Pendelton",
                "arthur.pendelton@teamliness.io",
                "Former Staff Engineer",
                "Cloud & Platform Foundations",
                "CC-4100",
                existingEmpId,
                "Active"
        );
    }

    @Given("employee {string} has a locally validated skill {string} at proficiency {string}")
    public void employeeHasLocallyValidatedSkill(String employeeId, String skillId, String proficiency) {
        String personId = jdbcTemplate.queryForObject(
                "SELECT id FROM persons WHERE employee_id = ?",
                String.class,
                employeeId
        );
        jdbcTemplate.update(
                "DELETE FROM person_skills WHERE person_id = ? AND skill_id = ?",
                personId,
                skillId
        );
        jdbcTemplate.update(
                "INSERT INTO person_skills (id, person_id, skill_id, proficiency_level, validated_by_persona) VALUES (?, ?, ?, ?, ?)",
                "ps-track02-1",
                personId,
                skillId,
                proficiency,
                "Org Leader"
        );
    }

    @When("an Admin triggers Workday synchronization via {string} twice consecutively")
    public void anAdminTriggersWorkdaySynchronizationTwiceConsecutively(String methodAndPath) throws Exception {
        String path = methodAndPath.split(" ", 2)[1];
        mockMvc.perform(post(path).header("X-Teamliness-Persona", "Admin")).andReturn();
        this.syncResult = mockMvc.perform(post(path).header("X-Teamliness-Persona", "Admin")).andReturn();
    }

    @Then("the synchronization response status code is {int}")
    public void theSynchronizationResponseStatusCodeIs(int expectedStatus) {
        assertThat(syncResult.getResponse().getStatus()).isEqualTo(expectedStatus);
    }

    @Then("the synchronization summary reports adapterMode {string}, upsertedCount {int}, and deactivatedCount {int}")
    public void theSynchronizationSummaryReports(String expectedMode, int expectedUpserted, int expectedDeactivated) throws Exception {
        JsonNode json = objectMapper.readTree(syncResult.getResponse().getContentAsString());
        assertThat(json.path("adapterMode").asText()).isEqualTo(expectedMode);
        assertThat(json.path("upsertedCount").asInt()).isEqualTo(expectedUpserted);
        assertThat(json.path("deactivatedCount").asInt()).isEqualTo(expectedDeactivated);
    }

    @Then("employee {string} retains the locally validated skill {string} at proficiency {string}")
    public void employeeRetainsLocallyValidatedSkill(String employeeId, String skillId, String expectedProficiency) {
        String actualProficiency = jdbcTemplate.queryForObject(
                "SELECT ps.proficiency_level FROM person_skills ps JOIN persons p ON p.id = ps.person_id WHERE p.employee_id = ? AND ps.skill_id = ?",
                String.class,
                employeeId,
                skillId
        );
        assertThat(actualProficiency).isEqualTo(expectedProficiency);
    }

    @Given("Workday directory synchronization has completed")
    public void workdayDirectorySynchronizationHasCompleted() throws Exception {
        this.syncResult = mockMvc.perform(post("/api/v1/workday/sync").header("X-Teamliness-Persona", "Admin")).andReturn();
        assertThat(syncResult.getResponse().getStatus()).isEqualTo(200);
    }

    @When("a consumer queries {string}")
    public void aConsumerQueries(String methodAndPath) throws Exception {
        String path = methodAndPath.split(" ", 2)[1];
        this.personsResult = mockMvc.perform(get(path)).andReturn();
    }

    @Then("the directory contains employee {string} named {string} with title {string}, department {string}, and status {string}")
    public void theDirectoryContainsEmployee(
            String employeeId,
            String fullName,
            String jobTitle,
            String department,
            String employmentStatus
    ) throws Exception {
        assertThat(personsResult.getResponse().getStatus()).isEqualTo(200);
        JsonNode array = objectMapper.readTree(personsResult.getResponse().getContentAsString());
        JsonNode matched = null;
        for (JsonNode node : array) {
            if (employeeId.equals(node.path("employeeId").asText())) {
                matched = node;
                break;
            }
        }
        assertThat(matched)
                .as("Expected employee %s in GET /api/v1/persons response", employeeId)
                .isNotNull();
        assertThat(matched.path("fullName").asText()).isEqualTo(fullName);
        assertThat(matched.path("jobTitle").asText()).isEqualTo(jobTitle);
        assertThat(matched.path("department").asText()).isEqualTo(department);
        assertThat(matched.path("employmentStatus").asText()).isEqualTo(employmentStatus);
    }

    @Given("a Workday RaaS JSON payload containing Report_Entry rows for {string} \\(Active) and {string} \\(Terminated)")
    public void aWorkdayRaasJsonPayloadContainingReportEntryRows(String activeEmpId, String terminatedEmpId) {
        this.simulatedRaasJson = """
                {
                  "Report_Entry": [
                    {
                      "Employee_ID": "%s",
                      "Full_Name": "Nadia Kovač",
                      "Email_Address": "nadia.kovac@teamliness.io",
                      "Business_Title": "Staff Security Engineer",
                      "Supervisory_Organization": "Cloud & Platform Foundations",
                      "Cost_Center": "CC-4100",
                      "Manager_ID": "WD-1001",
                      "Worker_Status": "Active"
                    },
                    {
                      "Employee_ID": "%s",
                      "Full_Name": "Julian Mercer",
                      "Email_Address": "julian.mercer@teamliness.io",
                      "Business_Title": "Senior Backend Engineer",
                      "Supervisory_Organization": "Digital Payments & Experience",
                      "Cost_Center": "CC-4200",
                      "Manager_ID": "WD-1002",
                      "Worker_Status": "Terminated"
                    }
                  ]
                }
                """.formatted(activeEmpId, terminatedEmpId);
    }

    @When("the WorkdayRaaSAdapter fetches directory records from the configured RaaS endpoint")
    public void theWorkdayRaasAdapterFetchesDirectoryRecords() throws Exception {
        String endpoint = "https://workday.teamliness.test/raas/directory?format=json";
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        server.expect(requestTo(endpoint))
                .andRespond(withSuccess(simulatedRaasJson, MediaType.APPLICATION_JSON));

        Class<?> adapterClass = Class.forName("com.teamliness.workday.adapter.out.WorkdayRaaSAdapter");
        Object adapter = adapterClass
                .getDeclaredConstructor(RestClient.class, String.class, ObjectMapper.class)
                .newInstance(builder.build(), endpoint, objectMapper);
        Method fetchMethod = adapterClass.getMethod("fetchDirectoryRecords");
        this.parsedRaasRecords = (List<?>) fetchMethod.invoke(adapter);
        server.verify();
    }

    @Then("the adapter returns {int} canonical Workday records with employment statuses {string} and {string}")
    public void theAdapterReturnsCanonicalWorkdayRecordsWithStatuses(int expectedCount, String firstStatus, String secondStatus) throws Exception {
        assertThat(parsedRaasRecords).hasSize(expectedCount);
        Object first = parsedRaasRecords.get(0);
        Object second = parsedRaasRecords.get(1);
        Method statusMethod = first.getClass().getMethod("employmentStatus");
        assertThat(statusMethod.invoke(first)).isEqualTo(firstStatus);
        assertThat(statusMethod.invoke(second)).isEqualTo(secondStatus);
    }
}
