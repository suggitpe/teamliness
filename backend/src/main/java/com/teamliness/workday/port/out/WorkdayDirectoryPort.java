package com.teamliness.workday.port.out;

import com.teamliness.workday.domain.WorkdayDirectoryRecord;

import java.util.List;

/**
 * Hexagonal outbound port isolating all interactions with the Workday HR System of Record.
 */
public interface WorkdayDirectoryPort {

    /**
     * Returns the upper-case identifier of the active adapter ("MOCK" or "RAAS").
     *
     * @return adapter mode identifier
     */
    String adapterMode();

    /**
     * Fetches the authoritative snapshot of employee directory records from Workday.
     *
     * @return list of canonical Workday employee records
     */
    List<WorkdayDirectoryRecord> fetchDirectoryRecords();
}
