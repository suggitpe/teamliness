package com.teamliness.workday.domain;

import java.time.Instant;

/**
 * Audit summary returned after a Workday directory synchronization run.
 *
 * @param adapterMode      active Workday adapter identifier ("MOCK" or "RAAS")
 * @param triggeredBy      persona or scheduler identifier that triggered synchronization
 * @param upsertedCount    number of active employee records inserted or updated
 * @param deactivatedCount number of omitted or terminated employee records soft-deactivated to "Inactive"
 * @param executedAt       UTC timestamp of the synchronization run
 */
public record WorkdaySyncSummary(
        String adapterMode,
        String triggeredBy,
        int upsertedCount,
        int deactivatedCount,
        Instant executedAt
) {
}
