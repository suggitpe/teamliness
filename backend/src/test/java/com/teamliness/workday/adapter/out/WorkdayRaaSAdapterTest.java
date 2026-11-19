package com.teamliness.workday.adapter.out;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamliness.workday.domain.WorkdayDirectoryRecord;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/**
 * Unit tests for {@link WorkdayRaaSAdapter}.
 */
class WorkdayRaaSAdapterTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void parsesWorkdayReportEntryJsonAndNormalizesStatuses() {
        String endpoint = "https://workday.example.com/raas/directory?format=json";
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();

        String payload = """
                {
                  "Report_Entry": [
                    {
                      "Employee_ID": "WD-3001",
                      "Full_Name": "Clara Oswald",
                      "Email_Address": "clara.oswald@teamliness.io",
                      "Business_Title": "Staff Platform Engineer",
                      "Supervisory_Organization": "Cloud & Platform Foundations",
                      "Cost_Center": "CC-4100",
                      "Manager_ID": "",
                      "Worker_Status": "Active"
                    },
                    {
                      "Employee_ID": "WD-3002",
                      "Full_Name": "Danny Pink",
                      "Email_Address": "danny.pink@teamliness.io",
                      "Business_Title": "Senior Engineer",
                      "Supervisory_Organization": "Digital Payments & Experience",
                      "Cost_Center": "CC-4200",
                      "Manager_ID": "WD-3001",
                      "Worker_Status": "On Leave - Terminated"
                    },
                    {
                      "Employee_ID": ""
                    }
                  ]
                }
                """;

        server.expect(requestTo(endpoint))
                .andRespond(withSuccess(payload, MediaType.APPLICATION_JSON));

        WorkdayRaaSAdapter adapter = new WorkdayRaaSAdapter(builder.build(), endpoint, objectMapper);
        assertThat(adapter.adapterMode()).isEqualTo("RAAS");

        List<WorkdayDirectoryRecord> records = adapter.fetchDirectoryRecords();
        assertThat(records).hasSize(2);
        assertThat(records.get(0).employeeId()).isEqualTo("WD-3001");
        assertThat(records.get(0).managerEmployeeId()).isNull();
        assertThat(records.get(0).employmentStatus()).isEqualTo("Active");
        assertThat(records.get(1).employeeId()).isEqualTo("WD-3002");
        assertThat(records.get(1).managerEmployeeId()).isEqualTo("WD-3001");
        assertThat(records.get(1).employmentStatus()).isEqualTo("Inactive");
    }

    @Test
    void returnsEmptyListWhenPayloadHasNoReportEntries() {
        String endpoint = "https://workday.example.com/raas/directory?format=json";
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();

        server.expect(requestTo(endpoint))
                .andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));

        WorkdayRaaSAdapter adapter = new WorkdayRaaSAdapter(builder.build(), endpoint, objectMapper);
        assertThat(adapter.fetchDirectoryRecords()).isEmpty();
    }
}
