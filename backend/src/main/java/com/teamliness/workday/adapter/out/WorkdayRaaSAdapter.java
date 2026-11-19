package com.teamliness.workday.adapter.out;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamliness.workday.domain.WorkdayDirectoryRecord;
import com.teamliness.workday.port.out.WorkdayDirectoryPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Workday Report-as-a-Service (RaaS) REST/JSON adapter implementing {@link WorkdayDirectoryPort}.
 */
@Component
@ConditionalOnProperty(name = "teamliness.workday.mode", havingValue = "raas")
public class WorkdayRaaSAdapter implements WorkdayDirectoryPort {

    private final RestClient restClient;
    private final String raasEndpointUrl;
    private final ObjectMapper objectMapper;

    /**
     * Spring constructor creating a default {@link RestClient}.
     *
     * @param raasEndpointUrl configured Workday RaaS JSON URL
     * @param objectMapper    Jackson object mapper
     */
    @Autowired
    public WorkdayRaaSAdapter(
            @Value("${teamliness.workday.raas-url:https://workday.example.com/raas/directory?format=json}") String raasEndpointUrl,
            ObjectMapper objectMapper
    ) {
        this(RestClient.create(), raasEndpointUrl, objectMapper);
    }

    /**
     * Testable constructor accepting a pre-configured {@link RestClient}.
     *
     * @param restClient      Spring REST client
     * @param raasEndpointUrl Workday RaaS endpoint URL
     * @param objectMapper    Jackson object mapper
     */
    public WorkdayRaaSAdapter(RestClient restClient, String raasEndpointUrl, ObjectMapper objectMapper) {
        this.restClient = restClient;
        this.raasEndpointUrl = raasEndpointUrl;
        this.objectMapper = objectMapper;
    }

    @Override
    public String adapterMode() {
        return "RAAS";
    }

    @Override
    public List<WorkdayDirectoryRecord> fetchDirectoryRecords() {
        String rawJson = restClient.get()
                .uri(raasEndpointUrl)
                .retrieve()
                .body(String.class);

        if (rawJson == null || rawJson.isBlank()) {
            return List.of();
        }

        try {
            JsonNode root = objectMapper.readTree(rawJson);
            JsonNode entries = root.path("Report_Entry");
            if (!entries.isArray()) {
                return List.of();
            }

            List<WorkdayDirectoryRecord> records = new ArrayList<>();
            for (JsonNode entry : entries) {
                String employeeId = entry.path("Employee_ID").asText();
                if (employeeId.isBlank()) {
                    continue;
                }
                String rawStatus = entry.path("Worker_Status").asText("Active");
                String normalizedStatus = "Active".equalsIgnoreCase(rawStatus) ? "Active" : "Inactive";
                String managerId = entry.path("Manager_ID").asText(null);
                if (managerId != null && managerId.isBlank()) {
                    managerId = null;
                }

                records.add(new WorkdayDirectoryRecord(
                        employeeId,
                        entry.path("Full_Name").asText(""),
                        entry.path("Email_Address").asText(""),
                        entry.path("Business_Title").asText(""),
                        entry.path("Supervisory_Organization").asText(""),
                        entry.path("Cost_Center").asText(""),
                        managerId,
                        normalizedStatus
                ));
            }
            return records;
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to parse Workday RaaS JSON response", ex);
        }
    }
}
