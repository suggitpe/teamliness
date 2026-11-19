package com.teamliness.catalog.application;

import com.teamliness.catalog.domain.AssignPersonSkillCommand;
import com.teamliness.catalog.domain.CreateSkillCommand;
import com.teamliness.catalog.domain.RbacPersona;
import com.teamliness.catalog.domain.RoleCatalogEntry;
import com.teamliness.catalog.domain.SkillCatalogEntry;
import com.teamliness.catalog.domain.ValidatedPersonSkill;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

/**
 * Application service enforcing RBAC rules across the Standardized Role & Skill Catalogs
 * and direct validated employee skill assignments.
 */
@Service
public class CatalogAndSkillService {

    private static final Set<String> ALLOWED_PROFICIENCY_LEVELS = Set.of(
            "BEGINNER",
            "INTERMEDIATE",
            "EXPERT"
    );

    private final JdbcTemplate jdbcTemplate;

    /**
     * Constructs the {@link CatalogAndSkillService}.
     *
     * @param jdbcTemplate Spring JDBC template
     */
    public CatalogAndSkillService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Lists all active standardized team roles.
     *
     * @return list of {@link RoleCatalogEntry}
     */
    @Transactional(readOnly = true)
    public List<RoleCatalogEntry> listRoles() {
        return jdbcTemplate.query(
                "SELECT id, code, display_name, description, active FROM roles_catalog ORDER BY display_name ASC",
                (rs, rowNum) -> new RoleCatalogEntry(
                        rs.getString("id"),
                        rs.getString("code"),
                        rs.getString("display_name"),
                        rs.getString("description"),
                        rs.getBoolean("active")
                )
        );
    }

    /**
     * Lists all active standardized skills.
     *
     * @return list of {@link SkillCatalogEntry}
     */
    @Transactional(readOnly = true)
    public List<SkillCatalogEntry> listSkills() {
        return jdbcTemplate.query(
                "SELECT id, code, display_name, category, description, active FROM skills_catalog ORDER BY category ASC, display_name ASC",
                (rs, rowNum) -> new SkillCatalogEntry(
                        rs.getString("id"),
                        rs.getString("code"),
                        rs.getString("display_name"),
                        rs.getString("category"),
                        rs.getString("description"),
                        rs.getBoolean("active")
                )
        );
    }

    /**
     * Creates a new standardized skill in the Skill Catalog. Permitted exclusively for {@link RbacPersona#ADMIN}.
     *
     * @param persona active caller persona
     * @param command skill creation command
     * @return created {@link SkillCatalogEntry}
     */
    @Transactional
    public SkillCatalogEntry createSkill(RbacPersona persona, CreateSkillCommand command) {
        if (!persona.canMutateCatalog()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only ADMIN persona can mutate the Skill Catalog");
        }
        String skillId = "skill-" + command.code().toLowerCase(Locale.ROOT).replace('_', '-');
        jdbcTemplate.update(
                "INSERT INTO skills_catalog (id, code, display_name, category, description, active) VALUES (?, ?, ?, ?, ?, TRUE)",
                skillId,
                command.code(),
                command.displayName(),
                command.category(),
                command.description()
        );
        return new SkillCatalogEntry(
                skillId,
                command.code(),
                command.displayName(),
                command.category(),
                command.description(),
                true
        );
    }

    /**
     * Assigns or updates a directly validated skill proficiency on an employee.
     * Permitted for {@link RbacPersona#ADMIN}, {@link RbacPersona#ORG_LEADER}, and {@link RbacPersona#TEAM_LEAD}.
     *
     * @param persona  active caller persona
     * @param personId target employee surrogate ID
     * @param command  skill assignment command
     * @return persisted {@link ValidatedPersonSkill}
     */
    @Transactional
    public ValidatedPersonSkill assignValidatedSkill(
            RbacPersona persona,
            String personId,
            AssignPersonSkillCommand command
    ) {
        if (!persona.canAssignValidatedSkills()) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "READ_ONLY_VIEWER is not authorized to assign validated skills"
            );
        }

        String normalizedProficiency = command.proficiencyLevel() != null
                ? command.proficiencyLevel().trim().toUpperCase(Locale.ROOT)
                : "";
        if (!ALLOWED_PROFICIENCY_LEVELS.contains(normalizedProficiency)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid proficiencyLevel: must be BEGINNER, INTERMEDIATE, or EXPERT"
            );
        }

        List<String> existingIds = jdbcTemplate.query(
                "SELECT id FROM person_skills WHERE person_id = ? AND skill_id = ?",
                (rs, rowNum) -> rs.getString("id"),
                personId,
                command.skillId()
        );

        String recordId;
        if (!existingIds.isEmpty()) {
            recordId = existingIds.getFirst();
            jdbcTemplate.update(
                    "UPDATE person_skills SET proficiency_level = ?, validated_by_persona = ?, validated_at = CURRENT_TIMESTAMP WHERE id = ?",
                    normalizedProficiency,
                    persona.name(),
                    recordId
            );
        } else {
            recordId = "ps-" + UUID.randomUUID();
            jdbcTemplate.update(
                    "INSERT INTO person_skills (id, person_id, skill_id, proficiency_level, validated_by_persona) VALUES (?, ?, ?, ?, ?)",
                    recordId,
                    personId,
                    command.skillId(),
                    normalizedProficiency,
                    persona.name()
            );
        }

        return new ValidatedPersonSkill(
                recordId,
                personId,
                command.skillId(),
                normalizedProficiency,
                persona.name()
        );
    }

    /**
     * Lists all validated skill assignments for a given employee.
     *
     * @param personId employee surrogate ID
     * @return list of {@link ValidatedPersonSkill}
     */
    @Transactional(readOnly = true)
    public List<ValidatedPersonSkill> listPersonSkills(String personId) {
        return jdbcTemplate.query(
                "SELECT id, person_id, skill_id, proficiency_level, validated_by_persona FROM person_skills WHERE person_id = ? ORDER BY skill_id ASC",
                (rs, rowNum) -> new ValidatedPersonSkill(
                        rs.getString("id"),
                        rs.getString("person_id"),
                        rs.getString("skill_id"),
                        rs.getString("proficiency_level"),
                        rs.getString("validated_by_persona")
                ),
                personId
        );
    }
}
