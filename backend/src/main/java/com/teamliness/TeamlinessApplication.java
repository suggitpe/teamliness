package com.teamliness;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the Teamliness Hexagonal Modular Monolith backend service.
 */
@SpringBootApplication
public class TeamlinessApplication {

    /**
     * Boots the Spring application context.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(TeamlinessApplication.class, args);
    }
}
