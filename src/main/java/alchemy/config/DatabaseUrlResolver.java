package alchemy.config;

/**
 * Single place that resolves the HSQLDB JDBC URL, so no other class in this
 * module hard-codes a host/port for the database.
 *
 * Lookup order:
 *   1. JVM system property "DATABASE_URL" (e.g. -DDATABASE_URL=...). This is
 *      also where {@link DatabaseUrlEnvironmentPostProcessor} copies Spring's
 *      fully layered value (module .env < root .env < real env var) before
 *      the ApplicationContext is refreshed, so by the time any bean's static
 *      initializer runs, tier 1 already reflects that layering.
 *   2. The real process environment variable DATABASE_URL (fallback for code
 *      paths that run outside Spring, e.g. plain unit tests).
 *   3. The standalone default below - the ONLY hard-coded destination literal
 *      for the database in this module.
 */
public final class DatabaseUrlResolver {

    /** Standalone default so the module runs on its own when cloned by itself. */
    public static final String DEFAULT_DATABASE_URL = "jdbc:hsqldb:hsql://localhost:9002/mydb";

    private DatabaseUrlResolver() {
    }

    public static String resolve() {
        String value = System.getProperty("DATABASE_URL");
        if (isSet(value)) {
            return value;
        }
        value = System.getenv("DATABASE_URL");
        if (isSet(value)) {
            return value;
        }
        return DEFAULT_DATABASE_URL;
    }

    private static boolean isSet(String value) {
        return value != null && !value.isBlank();
    }
}
