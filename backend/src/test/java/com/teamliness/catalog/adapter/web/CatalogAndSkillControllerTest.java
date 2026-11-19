package com.teamliness.catalog.adapter.web;

import com.teamliness.catalog.application.CatalogAndSkillService;
import com.teamliness.catalog.domain.ValidatedPersonSkill;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link CatalogAndSkillController}.
 */
class CatalogAndSkillControllerTest {

    @Test
    void listPersonSkillsReturnsOkWithValidatedSkills() {
        CatalogAndSkillService service = mock(CatalogAndSkillService.class);
        List<ValidatedPersonSkill> expected = List.of(
                new ValidatedPersonSkill("ps-1", "per-103", "skill-java-spring", "EXPERT", "ORG_LEADER")
        );
        when(service.listPersonSkills("per-103")).thenReturn(expected);

        CatalogAndSkillController controller = new CatalogAndSkillController(service);
        ResponseEntity<List<ValidatedPersonSkill>> response = controller.listPersonSkills("per-103");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expected);
    }
}
