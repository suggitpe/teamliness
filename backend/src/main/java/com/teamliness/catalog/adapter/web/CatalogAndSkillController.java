package com.teamliness.catalog.adapter.web;

import com.teamliness.catalog.application.CatalogAndSkillService;
import com.teamliness.catalog.domain.AssignPersonSkillCommand;
import com.teamliness.catalog.domain.CreateSkillCommand;
import com.teamliness.catalog.domain.RbacPersona;
import com.teamliness.catalog.domain.RoleCatalogEntry;
import com.teamliness.catalog.domain.SkillCatalogEntry;
import com.teamliness.catalog.domain.ValidatedPersonSkill;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST adapter exposing Standardized Role & Skill Catalogs and persona-guarded direct skill validation.
 */
@RestController
@RequestMapping("/api/v1")
public class CatalogAndSkillController {

    private final CatalogAndSkillService catalogAndSkillService;

    /**
     * Constructs the {@link CatalogAndSkillController}.
     *
     * @param catalogAndSkillService catalog and validated skill application service
     */
    public CatalogAndSkillController(CatalogAndSkillService catalogAndSkillService) {
        this.catalogAndSkillService = catalogAndSkillService;
    }

    /**
     * Lists all standardized roles in the Role Catalog.
     *
     * @return HTTP 200 with list of {@link RoleCatalogEntry}
     */
    @GetMapping("/catalog/roles")
    public ResponseEntity<List<RoleCatalogEntry>> listRoles() {
        return ResponseEntity.ok(catalogAndSkillService.listRoles());
    }

    /**
     * Lists all standardized skills in the Skill Catalog.
     *
     * @return HTTP 200 with list of {@link SkillCatalogEntry}
     */
    @GetMapping("/catalog/skills")
    public ResponseEntity<List<SkillCatalogEntry>> listSkills() {
        return ResponseEntity.ok(catalogAndSkillService.listSkills());
    }

    /**
     * Creates a new standardized skill in the Skill Catalog (ADMIN only).
     *
     * @param personaHeader caller persona header
     * @param command       skill creation request payload
     * @return HTTP 201 with created {@link SkillCatalogEntry}, or HTTP 403 if unauthorized
     */
    @PostMapping("/catalog/skills")
    public ResponseEntity<SkillCatalogEntry> createSkill(
            @RequestHeader(name = "X-Persona-Role", defaultValue = "READ_ONLY_VIEWER") String personaHeader,
            @RequestBody CreateSkillCommand command
    ) {
        RbacPersona persona = RbacPersona.fromHeader(personaHeader);
        SkillCatalogEntry created = catalogAndSkillService.createSkill(persona, command);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Assigns a direct validated skill proficiency to an employee (ADMIN, ORG_LEADER, TEAM_LEAD).
     *
     * @param personaHeader caller persona header
     * @param personId      target employee ID
     * @param command       skill assignment request payload
     * @return HTTP 200 with {@link ValidatedPersonSkill}, or HTTP 403 if unauthorized
     */
    @PostMapping("/persons/{personId}/skills")
    public ResponseEntity<ValidatedPersonSkill> assignPersonSkill(
            @RequestHeader(name = "X-Persona-Role", defaultValue = "READ_ONLY_VIEWER") String personaHeader,
            @PathVariable String personId,
            @RequestBody AssignPersonSkillCommand command
    ) {
        RbacPersona persona = RbacPersona.fromHeader(personaHeader);
        ValidatedPersonSkill validated = catalogAndSkillService.assignValidatedSkill(persona, personId, command);
        return ResponseEntity.ok(validated);
    }

    /**
     * Lists all validated skill proficiencies for an employee.
     *
     * @param personId target employee ID
     * @return HTTP 200 with list of {@link ValidatedPersonSkill}
     */
    @GetMapping("/persons/{personId}/skills")
    public ResponseEntity<List<ValidatedPersonSkill>> listPersonSkills(@PathVariable String personId) {
        return ResponseEntity.ok(catalogAndSkillService.listPersonSkills(personId));
    }
}
