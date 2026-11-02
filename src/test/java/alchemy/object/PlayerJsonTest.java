package alchemy.object;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlayerJsonTest {

    @Test
    void playerJsonNeverContainsThePassword() throws Exception {
        Player p = new Player(5, "tester", "$2a$10$abcdefghijklmnopqrstuv", null, null, 1);
        String json = new ObjectMapper().writeValueAsString(p);
        assertFalse(json.toLowerCase().contains("password"), json);
        assertFalse(json.contains("$2a$"), json);
        assertTrue(json.contains("tester"));
    }
}
