package alchemy.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.config.ConfigDataEnvironmentPostProcessor;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.StandardEnvironment;

/**
 * Proves the destination-registry layering that application.properties wires
 * via spring.config.import (module .env < root .env < real env var/system
 * property) and the ordering in {@link DatabaseUrlResolver}.
 *
 * This deliberately never builds a SpringApplication / ApplicationContext:
 * it drives {@link ConfigDataEnvironmentPostProcessor} directly against
 * throwaway StandardEnvironments and temp files. No bean is ever created
 * (in particular alchemy.data.HSQLDatabase, whose constructor opens a
 * database connection, is never instantiated), and no web server is
 * started - this test touches neither the database nor the network.
 *
 * "spring.config.name" is pinned to a name that has no matching file, so the
 * real classpath application.properties (and therefore the real ./.env /
 * ../.env it references) is never consulted - only the two explicit temp
 * files each test wires up via spring.config.import are in play.
 */
class LayeredDestinationConfigTest {

    @AfterEach
    void clearSystemProperty() {
        System.clearProperty("DATABASE_URL");
    }

    @Test
    void moduleEnvAloneIsUsedWhenNoMasterPresent() throws IOException {
        Path moduleEnv = writeEnv("DATABASE_URL=jdbc:hsqldb:hsql://module-only:9002/mydb");

        StandardEnvironment env = layeredEnvironment(moduleEnv, missingFile());

        assertEquals("jdbc:hsqldb:hsql://module-only:9002/mydb", env.getProperty("DATABASE_URL"));
    }

    @Test
    void masterEnvOverridesModuleEnv() throws IOException {
        Path moduleEnv = writeEnv("DATABASE_URL=jdbc:hsqldb:hsql://module-only:9002/mydb");
        Path masterEnv = writeEnv("DATABASE_URL=jdbc:hsqldb:hsql://master-wins:9002/mydb");

        StandardEnvironment env = layeredEnvironment(moduleEnv, masterEnv);

        assertEquals("jdbc:hsqldb:hsql://master-wins:9002/mydb", env.getProperty("DATABASE_URL"));
    }

    @Test
    void realEnvVarBeatsMasterEnv() throws IOException {
        Path moduleEnv = writeEnv("DATABASE_URL=jdbc:hsqldb:hsql://module-only:9002/mydb");
        Path masterEnv = writeEnv("DATABASE_URL=jdbc:hsqldb:hsql://master-wins:9002/mydb");
        // System.getenv() can't be mutated in-process, so a JVM system
        // property stands in for "a real process env var" here: both
        // systemProperties and systemEnvironment sit above anything
        // spring.config.import brings in, at the same precedence tier.
        System.setProperty("DATABASE_URL", "jdbc:hsqldb:hsql://real-env-var:9002/mydb");

        StandardEnvironment env = layeredEnvironment(moduleEnv, masterEnv);

        assertEquals("jdbc:hsqldb:hsql://real-env-var:9002/mydb", env.getProperty("DATABASE_URL"));
    }

    @Test
    void absentFilesDoNotFailAndLeaveKeyUnset() {
        StandardEnvironment env = layeredEnvironment(missingFile(), missingFile());

        // Neither optional file exists; the app must still start, and no
        // fabricated value should appear from the import mechanism itself.
        assertEquals(System.getenv("DATABASE_URL"), env.getProperty("DATABASE_URL"));
    }

    @Test
    void resolverPrefersSystemPropertyOverEnvVarOverDefault() {
        System.clearProperty("DATABASE_URL");
        if (System.getenv("DATABASE_URL") == null) {
            assertEquals(DatabaseUrlResolver.DEFAULT_DATABASE_URL, DatabaseUrlResolver.resolve());
        }

        System.setProperty("DATABASE_URL", "jdbc:hsqldb:hsql://from-system-property:9002/mydb");
        assertEquals("jdbc:hsqldb:hsql://from-system-property:9002/mydb", DatabaseUrlResolver.resolve());
    }

    @Test
    void postProcessorCopiesResolvedValueIntoSystemPropertyBeforeContextStartup() throws IOException {
        Path masterEnv = writeEnv("DATABASE_URL=jdbc:hsqldb:hsql://from-postprocessor:9002/mydb");
        StandardEnvironment env = layeredEnvironment(missingFile(), masterEnv);

        // This is what runs during environment preparation, before the
        // ApplicationContext (and therefore any bean's static initializer,
        // e.g. HSQLDatabase's) is created.
        new DatabaseUrlEnvironmentPostProcessor().postProcessEnvironment(env, null);

        assertEquals("jdbc:hsqldb:hsql://from-postprocessor:9002/mydb", System.getProperty("DATABASE_URL"));
        // What alchemy.data.HSQLDatabase's static initializer would actually see:
        assertEquals("jdbc:hsqldb:hsql://from-postprocessor:9002/mydb", DatabaseUrlResolver.resolve());
    }

    private StandardEnvironment layeredEnvironment(Path moduleEnv, Path masterEnv) {
        String importValue = "optional:file:" + toImportPath(moduleEnv) + "[.properties],"
                + "optional:file:" + toImportPath(masterEnv) + "[.properties]";
        StandardEnvironment environment = new StandardEnvironment();
        environment.getPropertySources().addLast(new MapPropertySource("testSpringConfigImport",
                Map.<String, Object>of(
                        "spring.config.name", "alchemy-layering-test-no-such-application-file",
                        "spring.config.import", importValue)));
        ConfigDataEnvironmentPostProcessor.applyTo(environment);
        return environment;
    }

    private static String toImportPath(Path path) {
        return path.toAbsolutePath().toString().replace('\\', '/');
    }

    private Path missingFile() {
        return Path.of(System.getProperty("java.io.tmpdir"), "alchemy-layering-test-does-not-exist.env");
    }

    private Path writeEnv(String contents) throws IOException {
        Path file = Files.createTempFile("alchemy-layering-test-", ".env");
        file.toFile().deleteOnExit();
        Files.writeString(file, contents + System.lineSeparator());
        return file;
    }
}
