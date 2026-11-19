package com.teamliness.workday.adapter.out;

import com.teamliness.workday.domain.WorkdayDirectoryRecord;
import com.teamliness.workday.port.out.WorkdayDirectoryPort;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Seeded mock implementation of {@link WorkdayDirectoryPort} providing realistic hierarchical
 * organisation fixtures for local development and automated ATDD verification.
 */
@Component
@ConditionalOnProperty(name = "teamliness.workday.mode", havingValue = "mock", matchIfMissing = true)
public class MockWorkdayAdapter implements WorkdayDirectoryPort {

    @Override
    public String adapterMode() {
        return "MOCK";
    }

    @Override
    public List<WorkdayDirectoryRecord> fetchDirectoryRecords() {
        return List.of(
                new WorkdayDirectoryRecord(
                        "WD-1001",
                        "Elena Rostova",
                        "elena.rostova@teamliness.io",
                        "Principal Platform Architect",
                        "Cloud & Platform Foundations",
                        "CC-4100",
                        null,
                        "Active"
                ),
                new WorkdayDirectoryRecord(
                        "WD-1002",
                        "Marcus Vance",
                        "marcus.vance@teamliness.io",
                        "Lead Software Engineer",
                        "Digital Payments & Experience",
                        "CC-4200",
                        "WD-1001",
                        "Active"
                ),
                new WorkdayDirectoryRecord(
                        "WD-1003",
                        "Priya Nair",
                        "priya.nair@teamliness.io",
                        "Senior Software Engineer",
                        "Digital Payments & Experience",
                        "CC-4200",
                        "WD-1002",
                        "Active"
                ),
                new WorkdayDirectoryRecord(
                        "WD-1004",
                        "Devon Brooks",
                        "devon.brooks@teamliness.io",
                        "Staff Platform Engineer",
                        "Cloud & Platform Foundations",
                        "CC-4100",
                        "WD-1001",
                        "Active"
                ),
                new WorkdayDirectoryRecord(
                        "WD-1005",
                        "Sofia Lindqvist",
                        "sofia.lindqvist@teamliness.io",
                        "Staff Data Engineer",
                        "Cloud & Platform Foundations",
                        "CC-4100",
                        "WD-1001",
                        "Active"
                ),
                new WorkdayDirectoryRecord(
                        "WD-1006",
                        "Liam O'Connor",
                        "liam.oconnor@teamliness.io",
                        "Senior Frontend Engineer",
                        "Digital Payments & Experience",
                        "CC-4200",
                        "WD-1002",
                        "Active"
                )
        );
    }
}
