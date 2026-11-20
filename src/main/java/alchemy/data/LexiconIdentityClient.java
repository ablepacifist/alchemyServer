package alchemy.data;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

/**
 * HTTP implementation of {@link ILexiconIdentityClient}: posts the token to Lexicon's
 * {@code /api/auth/sso/validate-token} on the local machine.
 *
 * <p>Any failure (Lexicon down, bad JSON, token rejected) returns {@code null}, which the
 * logic layer treats as "not signed in". Failing closed is deliberate: a permission check
 * that cannot be answered must not let the action through.
 */
@Repository
public class LexiconIdentityClient implements ILexiconIdentityClient {

    private static final ObjectMapper JSON = new ObjectMapper();

    private final String validateUrl;
    private final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(3))
            .build();

    public LexiconIdentityClient(
            @Value("${LEXICON_INTERNAL_URL:http://127.0.0.1:${LEXICON_PORT:36568}}") String lexiconUrl) {
        String base = lexiconUrl.endsWith("/") ? lexiconUrl.substring(0, lexiconUrl.length() - 1) : lexiconUrl;
        this.validateUrl = base + "/api/auth/sso/validate-token";
    }

    @Override
    public Identity validate(String ssoToken) {
        if (ssoToken == null || ssoToken.isBlank()) {
            return null;
        }
        try {
            String body = JSON.writeValueAsString(java.util.Map.of("token", ssoToken.trim()));
            HttpRequest request = HttpRequest.newBuilder(URI.create(validateUrl))
                    .timeout(Duration.ofSeconds(5))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                    .build();
            HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                return null;
            }
            return parse(response.body());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        } catch (Exception e) {
            System.err.println("Lexicon identity check failed: " + e.getMessage());
            return null;
        }
    }

    /** Package-private so the JSON shape can be tested without a running Lexicon. */
    static Identity parse(String json) throws java.io.IOException {
        JsonNode node = JSON.readTree(json);
        if (node == null || !node.path("valid").asBoolean(false)) {
            return null;
        }
        Set<String> permissions = new HashSet<>();
        node.path("permissions").forEach(p -> permissions.add(p.asText()));
        return new Identity(
                node.path("userId").asInt(),
                node.path("username").asText(null),
                node.path("role").asText("USER"),
                Set.copyOf(permissions));
    }
}
