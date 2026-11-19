package com.teamliness.atdd;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

/**
 * Step definitions for {@code track_03_rbac_and_skills.feature}.
 */
public class Track03RbacAndSkillsSteps {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private String currentPersonaRole;
    private String currentActorPersonId;
    private MvcResult rolesCatalogResult;
    private MvcResult skillsCatalogResult;
    private MvcResult createSkillResult;
    private MvcResult assignSkillResult;

    @Given("the database is seeded with standardized roles and skills")
    public void theDatabaseIsSeededWithStandardizedRolesAndSkills() {
        Integer rolesCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM roles_catalog", Integer.class);
        Integer skillsCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM skills_catalog", Integer.class);
        assertThat(rolesCount).isGreaterThanOrEqualTo(5);
        assertThat(skillsCount).isGreaterThanOrEqualTo(6);
    }

    @When("a consumer with persona {string} queries {string} and {string}")
    public void aConsumerWithPersonaQueriesAnd(String personaRole, String rolesEndpoint, String skillsEndpoint) throws Exception {
        String rolesPath = rolesEndpoint.split(" ", 2)[1];
        String skillsPath = skillsEndpoint.split(" ", 2)[1];
        this.rolesCatalogResult = mockMvc.perform(get(rolesPath).header("X-Persona-Role", personaRole)).andReturn();
        this.skillsCatalogResult = mockMvc.perform(get(skillsPath).header("X-Persona-Role", personaRole)).andReturn();
    }

    @Then("both catalog responses return status code {int} with at least {int} roles and {int} skills")
    public void bothCatalogResponsesReturnStatusCodeWithAtLeastRolesAndSkills(int expectedStatus, int minRoles, int minSkills) throws Exception {
        assertThat(rolesCatalogResult.getResponse().getStatus()).isEqualTo(expectedStatus);
        assertThat(skillsCatalogResult.getResponse().getStatus()).isEqualTo(expectedStatus);

        JsonNode rolesArray = objectMapper.readTree(rolesCatalogResult.getResponse().getContentAsString());
        JsonNode skillsArray = objectMapper.readTree(skillsCatalogResult.getResponse().getContentAsString());
        assertThat(rolesArray.size()).isGreaterThanOrEqualTo(minRoles);
        assertThat(skillsArray.size()).isGreaterThanOrEqualTo(minSkills);
    }

    @Given("an authenticated caller with persona {string} and person ID {string}")
    public void anAuthenticatedCallerWithPersonaAndPersonId(String personaRole, String actorPersonId) {
        this.currentPersonaRole = personaRole;
        this.currentActorPersonId = actorPersonId;
    }

    @When("the caller submits a new catalog skill with code {string}, name {string}, and category {string} to {string}")
    public void theCallerSubmitsANewCatalogSkill(String skillCode, String displayName, String category, String methodAndPath) throws Exception {
        String path = methodAndPath.split(" ", 2)[1];
        jdbcTemplate.update("DELETE FROM skills_catalog WHERE code = ?", skillCode);
        String payload = objectMapper.writeValueAsString(Map.of(
                "code", skillCode,
                "displayName", displayName,
                "category", category,
                "description", "Standardized capability for " + displayName
        ));
        this.createSkillResult = mockMvc.perform(post(path)
                        .header("X-Persona-Role", currentPersonaRole)
                        .header("X-Person-Id", currentActorPersonId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andReturn();
    }

    @Then("the catalog creation response status code is {int}")
    public void theCatalogCreationResponseStatusCodeIs(int expectedStatus) {
        assertThat(createSkillResult.getResponse().getStatus()).isEqualTo(expectedStatus);
    }

    @When("the caller assigns skill {string} at proficiency {string} to person {string}")
    public void theCallerAssignsSkillAtProficiencyToPerson(String skillId, String proficiencyLevel, String targetPersonId) throws Exception {
        String payload = objectMapper.writeValueAsString(Map.of(
                "skillId", skillId,
                "proficiencyLevel", proficiencyLevel
        ));
        this.assignSkillResult = mockMvc.perform(post("/api/v1/persons/" + targetPersonId + "/skills")
                        .header("X-Persona-Role", currentPersonaRole)
                        .header("X-Person-Id", currentActorPersonId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andReturn();
    }

    @Then("the skill assignment response status code is {int}")
    public void theSkillAssignmentResponseStatusCodeIs(int expectedStatus) {
        assertThat(assignSkillResult.getResponse().getStatus()).isEqualTo(expectedStatus);
    }

    @Then("if permitted, the validated skill record for {string} and {string} persists proficiency {string} validated by {string}")
    public void ifPermittedTheValidatedSkillRecordPersists(
            String targetPersonId,
            String skillId,
            String expectedProficiency,
            String expectedValidatedBy
    ) throws Exception {
        if (assignSkillResult.getResponse().getStatus() == 200) {
            // Re-submit idempotently to verify in-place update branch of person_skills
            String payload = objectMapper.writeValueAsString(Map.of(
                    "skillId", skillId,
                    "proficiencyLevel", expectedProficiency
            ));
            mockMvc.perform(post("/api/v1/persons/" + targetPersonId + "/skills")
                            .header("X-Persona-Role", currentPersonaRole)
                            .header("X-Person-Id", currentActorPersonId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(payload))
                    .andReturn();

            MvcResult listResult = mockMvc.perform(get("/api/v1/persons/" + targetPersonId + "/skills"))
                    .andReturn();
            assertThat(listResult.getResponse().getStatus()).isEqualTo(200);
            JsonNode skillsArray = objectMapper.readTree(listResult.getResponse().getContentAsString());
            JsonNode matched = null;
            for (JsonNode item : skillsArray) {
                if (skillId.equals(item.path("skillId").asText())) {
                    matched = item;
                    break;
                }
            }
            assertThat(matched).isNotNull();
            assertThat(matched.path("proficiencyLevel").asText()).isEqualTo(expectedProficiency);
            assertThat(matched.path("validatedByPersona").asText()).isEqualTo(expectedValidatedBy);
        }
    }
}
