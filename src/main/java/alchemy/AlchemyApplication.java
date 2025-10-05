package alchemy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot Application for Alchemy Game Server
 * Provides REST API endpoints for the alchemy game
 */
@SpringBootApplication
public class AlchemyApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(AlchemyApplication.class, args);
    }
}