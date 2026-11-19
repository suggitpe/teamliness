package com.teamliness.workday.adapter.web;

import com.teamliness.workday.application.WorkdaySyncService;
import com.teamliness.workday.domain.PersonProjection;
import com.teamliness.workday.domain.WorkdaySyncSummary;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller exposing Workday directory synchronization and read-only employee projections.
 */
@RestController
@RequestMapping("/api/v1")
public class WorkdayDirectoryController {

    private final WorkdaySyncService workdaySyncService;

    /**
     * Constructs the {@link WorkdayDirectoryController}.
     *
     * @param workdaySyncService Workday directory synchronization service
     */
    public WorkdayDirectoryController(WorkdaySyncService workdaySyncService) {
        this.workdaySyncService = workdaySyncService;
    }

    /**
     * Triggers an on-demand Workday directory synchronization run.
     *
     * @param persona active RBAC persona header
     * @return synchronization audit summary
     */
    @PostMapping("/workday/sync")
    public ResponseEntity<WorkdaySyncSummary> triggerWorkdaySync(
            @RequestHeader(name = "X-Teamliness-Persona", defaultValue = "Admin") String persona
    ) {
        return ResponseEntity.ok(workdaySyncService.synchronizeDirectory(persona));
    }

    /**
     * Lists all synchronized Workday person projections.
     *
     * @return HTTP 200 with list of {@link PersonProjection}
     */
    @GetMapping("/persons")
    public ResponseEntity<List<PersonProjection>> listPersons() {
        return ResponseEntity.ok(workdaySyncService.listPersons());
    }
}
