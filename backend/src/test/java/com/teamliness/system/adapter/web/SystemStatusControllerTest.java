package com.teamliness.system.adapter.web;

import com.teamliness.system.application.SystemStatusService;
import com.teamliness.system.domain.SystemStatusSnapshot;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link SystemStatusController}.
 */
class SystemStatusControllerTest {

    @Test
    void getSystemStatusReturnsOkWithSnapshot() {
        SystemStatusService service = mock(SystemStatusService.class);
        SystemStatusSnapshot expected = new SystemStatusSnapshot("Teamliness", "UP", "2", "GLASSMORPHIC", true);
        when(service.getSystemStatus()).thenReturn(expected);

        SystemStatusController controller = new SystemStatusController(service);
        ResponseEntity<SystemStatusSnapshot> response = controller.getSystemStatus();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expected);
    }
}
