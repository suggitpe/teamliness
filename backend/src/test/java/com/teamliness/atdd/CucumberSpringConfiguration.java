package com.teamliness.atdd;

import com.teamliness.TeamlinessApplication;
import com.teamliness.config.PostgresTestcontainersConfig;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

/**
 * Cucumber-Spring context configuration binding ATDD step definitions to the Spring Boot test context.
 */
@CucumberContextConfiguration
@SpringBootTest(classes = TeamlinessApplication.class, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(PostgresTestcontainersConfig.class)
public class CucumberSpringConfiguration {
}
