package com.teamliness.workday.application;

import com.teamliness.workday.domain.PersonProjection;
import com.teamliness.workday.domain.WorkdayDirectoryRecord;
import com.teamliness.workday.domain.WorkdaySyncSummary;
import com.teamliness.workday.port.out.WorkdayDirectoryPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Application service orchestrating idempotent Workday directory synchronization
 * and soft-deactivation of omitted or terminated leavers.
 */
@Service
public class WorkdaySyncService {

    private final WorkdayDirectoryPort workdayDirectoryPort;
    private final JdbcTemplate jdbcTemplate;

    /**
     * Constructs the {@link WorkdaySyncService}.
     *
     * @param workdayDirectoryPort outbound hexagonal port for Workday directory access
     * @param jdbcTemplate         Spring JDBC template
     */
    public WorkdaySyncService(WorkdayDirectoryPort workdayDirectoryPort, JdbcTemplate jdbcTemplate) {
        this.workdayDirectoryPort = workdayDirectoryPort;
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Executes an idempotent synchronization run against {@link WorkdayDirectoryPort}.
     * Active employees are upserted in place (preserving local skill tags and memberships),
     * while employees omitted from Workday or marked inactive are soft-deactivated to {@code Inactive}.
     *
     * @param triggeredBy persona or system identifier triggering the sync
     * @return synchronization summary
     */
    @Transactional
    public WorkdaySyncSummary synchronizeDirectory(String triggeredBy) {
        List<WorkdayDirectoryRecord> incomingRecords = workdayDirectoryPort.fetchDirectoryRecords();
        Set<String> activeIncomingEmployeeIds = new HashSet<>();

        int upsertedCount = 0;
        int deactivatedCount = 0;

        for (WorkdayDirectoryRecord record : incomingRecords) {
            boolean isActiveInWorkday = "Active".equalsIgnoreCase(record.employmentStatus());
            if (isActiveInWorkday) {
                activeIncomingEmployeeIds.add(record.employeeId());
            }

            List<String> existingIds = jdbcTemplate.query(
                    "SELECT id FROM persons WHERE employee_id = ?",
                    (rs, rowNum) -> rs.getString("id"),
                    record.employeeId()
            );

            String normalizedStatus = isActiveInWorkday ? "Active" : "Inactive";
            if (!existingIds.isEmpty()) {
                jdbcTemplate.update(
                        "UPDATE persons SET full_name = ?, email = ?, job_title = ?, department = ?, "
                                + "cost_centre = ?, manager_employee_id = ?, employment_status = ?, last_synced_at = CURRENT_TIMESTAMP "
                                + "WHERE employee_id = ?",
                        record.fullName(),
                        record.email(),
                        record.jobTitle(),
                        record.department(),
                        record.costCentre(),
                        record.managerEmployeeId(),
                        normalizedStatus,
                        record.employeeId()
                );
            } else {
                String deterministicId = "per-" + record.employeeId().toLowerCase().replace("wd-", "");
                jdbcTemplate.update(
                        "INSERT INTO persons (id, employee_id, full_name, email, job_title, department, cost_centre, manager_employee_id, employment_status) "
                                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                        deterministicId,
                        record.employeeId(),
                        record.fullName(),
                        record.email(),
                        record.jobTitle(),
                        record.department(),
                        record.costCentre(),
                        record.managerEmployeeId(),
                        normalizedStatus
                );
            }

            if (isActiveInWorkday) {
                upsertedCount++;
            } else {
                deactivatedCount++;
            }
        }

        // Soft-deactivate any local person rows omitted from the active Workday snapshot
        List<String> allLocalEmployeeIds = jdbcTemplate.query(
                "SELECT employee_id FROM persons",
                (rs, rowNum) -> rs.getString("employee_id")
        );

        for (String localEmpId : allLocalEmployeeIds) {
            if (!activeIncomingEmployeeIds.contains(localEmpId)) {
                boolean alreadyCountedInIncoming = incomingRecords.stream()
                        .anyMatch(r -> r.employeeId().equals(localEmpId));
                jdbcTemplate.update(
                        "UPDATE persons SET employment_status = 'Inactive', last_synced_at = CURRENT_TIMESTAMP WHERE employee_id = ?",
                        localEmpId
                );
                if (!alreadyCountedInIncoming) {
                    deactivatedCount++;
                }
            }
        }

        String resolvedTrigger = (triggeredBy != null && !triggeredBy.isBlank()) ? triggeredBy : "Admin";
        jdbcTemplate.update(
                "INSERT INTO workday_sync_audit (id, adapter_mode, triggered_by, upserted_count, deactivated_count) VALUES (?, ?, ?, ?, ?)",
                "sync-" + UUID.randomUUID(),
                workdayDirectoryPort.adapterMode(),
                resolvedTrigger,
                upsertedCount,
                deactivatedCount
        );

        return new WorkdaySyncSummary(
                workdayDirectoryPort.adapterMode(),
                resolvedTrigger,
                upsertedCount,
                deactivatedCount,
                Instant.now()
        );
    }

    /**
     * Returns all local Workday employee projections ordered by employee ID.
     *
     * @return list of {@link PersonProjection}
     */
    @Transactional(readOnly = true)
    public List<PersonProjection> listPersons() {
        return jdbcTemplate.query(
                "SELECT id, employee_id, full_name, email, job_title, department, cost_centre, manager_employee_id, employment_status "
                        + "FROM persons ORDER BY employee_id ASC",
                (rs, rowNum) -> new PersonProjection(
                        rs.getString("id"),
                        rs.getString("employee_id"),
                        rs.getString("full_name"),
                        rs.getString("email"),
                        rs.getString("job_title"),
                        rs.getString("department"),
                        rs.getString("cost_centre"),
                        rs.getString("manager_employee_id"),
                        rs.getString("employment_status"),
                        true
                )
        );
    }
}
