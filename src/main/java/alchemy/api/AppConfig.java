package alchemy.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public String databasePath() {
        return "jdbc:hsqldb:file:./data/mydb";
    }
}
