package alchemy.data;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LexiconIdentityClientTest {

    @Test
    void parsesPermissionsFromValidateTokenResponse() throws Exception {
        ILexiconIdentityClient.Identity who = LexiconIdentityClient.parse(
                "{\"valid\":true,\"userId\":1,\"username\":\"zx\",\"displayName\":\"zx\","
                        + "\"role\":\"ADMIN\",\"permissions\":[\"HOLDFAST_ADVANCE_TIME\",\"VOICE_ADMIN\"]}");
        assertNotNull(who);
        assertEquals(1, who.userId());
        assertEquals("ADMIN", who.role());
        assertTrue(who.has("HOLDFAST_ADVANCE_TIME"));
        assertFalse(who.has("HOLDFAST_DELETE"));
    }

    @Test
    void olderLexiconWithoutPermissionsGrantsNothing() throws Exception {
        ILexiconIdentityClient.Identity who = LexiconIdentityClient.parse(
                "{\"valid\":true,\"userId\":5,\"username\":\"p\",\"displayName\":\"p\"}");
        assertNotNull(who);
        assertFalse(who.has("HOLDFAST_ADVANCE_TIME"));
    }

    @Test
    void invalidTokenIsNull() throws Exception {
        assertNull(LexiconIdentityClient.parse("{\"valid\":false,\"error\":\"Invalid or expired token\"}"));
    }

    @Test
    void blankTokenNeverCallsLexicon() {
        // Port 9 (discard) would fail if it were contacted; a blank token must short-circuit.
        LexiconIdentityClient client = new LexiconIdentityClient("http://127.0.0.1:9");
        assertNull(client.validate(null));
        assertNull(client.validate("  "));
    }

    @Test
    void unreachableLexiconFailsClosed() {
        LexiconIdentityClient client = new LexiconIdentityClient("http://127.0.0.1:9");
        assertNull(client.validate("some-token"));
    }
}
