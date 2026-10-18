package alchemy.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * Bridges Spring's fully layered DATABASE_URL (resolved from
 * spring.config.import: module .env, then root .env, then real env vars -
 * see application.properties) into a JVM system property, so that classes
 * built during ApplicationContext refresh (e.g. HSQLDatabase's static
 * initializer, via {@link DatabaseUrlResolver}) can see it even though they
 * have no Spring Environment reference of their own.
 *
 * EnvironmentPostProcessors run in order during environment preparation,
 * which happens before the ApplicationContext (and therefore any bean, and
 * any static initializer triggered by loading a bean's class) is created.
 * Spring Boot's own ConfigDataEnvironmentPostProcessor - which resolves
 * spring.config.import - runs at Ordered.HIGHEST_PRECEDENCE + 10. This class
 * declares no order (defaults to lowest precedence), so it always runs after
 * ConfigDataEnvironmentPostProcessor: environment.getProperty("DATABASE_URL")
 * below already reflects the fully-imported/layered value. That guarantees
 * the system property is populated before any bean - and therefore any
 * static initializer - is constructed at context startup.
 */
public class DatabaseUrlEnvironmentPostProcessor implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        // An explicit -DDATABASE_URL=... already wins in DatabaseUrlResolver; don't overwrite it.
        if (System.getProperty("DATABASE_URL") != null) {
            return;
        }
        String resolved = environment.getProperty("DATABASE_URL");
        if (resolved != null && !resolved.isBlank()) {
            System.setProperty("DATABASE_URL", resolved);
        }
    }
}
