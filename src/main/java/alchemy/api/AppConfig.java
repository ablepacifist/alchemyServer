package alchemy.api;
//50191
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
// acts kinda like the app folder in soft eng
// this "bean"  is the same as a string that the spring boot will recognize
@Configuration
public class AppConfig {

    @Bean
    public String databasePath() {
        return "jdbc:hsqldb:file:./data/mydb";
    }
}
