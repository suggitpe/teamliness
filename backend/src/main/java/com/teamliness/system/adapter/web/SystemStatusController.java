package com.teamliness.system.adapter.web;

import com.teamliness.system.application.SystemStatusService;
import com.teamliness.system.domain.SystemStatusSnapshot;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST adapter exposing platform readiness, schema migration version, and UI theme metadata.
 */
@RestController
@RequestMapping("/api/v1/system")
public class SystemStatusController {

    private final SystemStatusService systemStatusService;

    /**
     * Constructs the {@link SystemStatusController}.
     *
     * @param systemStatusService system status application service
     */
    public SystemStatusController(SystemStatusService systemStatusService) {
        this.systemStatusService = systemStatusService;
    }

    /**
     * Returns the current platform health and schema readiness snapshot.
     *
     * @return HTTP 200 with {@link SystemStatusSnapshot}
     */
    @GetMapping("/status")
    public ResponseEntity<SystemStatusSnapshot> getSystemStatus() {
        return ResponseEntity.ok(systemStatusService.getSystemStatus());
    }
}
