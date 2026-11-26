package alchemy.logic;

import alchemy.data.ILexiconIdentityClient;
import alchemy.data.ILexiconIdentityClient.Identity;
import alchemy.data.IStubDatabase;
import alchemy.object.Holdfast;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/** Only callers Lexicon says hold the permission may advance time or delete a holdfast. */
class HoldfastPermissionTest {

    private static final Identity ADMIN = new Identity(1, "zx", "ADMIN",
            Set.of("HOLDFAST_ADVANCE_TIME", "HOLDFAST_DELETE"));
    private static final Identity PLAYER = new Identity(2, "someone", "USER", Set.of());

    private IStubDatabase db;
    private ILexiconIdentityClient identity;
    private HoldfastManager manager;
    private Holdfast holdfast;

    @BeforeEach
    void setUp() {
        db = mock(IStubDatabase.class);
        identity = mock(ILexiconIdentityClient.class);
        when(identity.validate("admin-token")).thenReturn(ADMIN);
        when(identity.validate("player-token")).thenReturn(PLAYER);
        holdfast = new Holdfast("party", "Lockwood");
        when(db.getHoldfast("party")).thenReturn(holdfast);
        manager = new HoldfastManager(db, identity);
    }

    @Test
    void advanceWithoutTokenIsUnauthorizedAndChangesNothing() {
        Map<String, Object> result = manager.advanceTimeAs(null, "party", 3);
        assertEquals(401, result.get("status"));
        assertEquals(0, holdfast.getDaysElapsed());
        verify(db, never()).updateHoldfast(any());
    }

    @Test
    void advanceAsPlayerIsForbiddenAndChangesNothing() {
        Map<String, Object> result = manager.advanceTimeAs("player-token", "party", 3);
        assertEquals(403, result.get("status"));
        assertEquals(false, result.get("success"));
        assertEquals(0, holdfast.getDaysElapsed());
        verify(db, never()).updateHoldfast(any());
    }

    @Test
    void advanceAsAdminMovesTheClock() {
        Map<String, Object> result = manager.advanceTimeAs("admin-token", "party", 3);
        assertEquals(200, result.get("status"));
        assertEquals(3, holdfast.getDaysElapsed());
        assertNotNull(result.get("events"));
    }

    @Test
    void advanceAsAdminOnMissingHoldfastIsNotFound() {
        Map<String, Object> result = manager.advanceTimeAs("admin-token", "nobody", 3);
        assertEquals(404, result.get("status"));
    }

    @Test
    void deleteAsPlayerIsForbidden() {
        Map<String, Object> result = manager.deleteHoldfastAs("player-token", "party");
        assertEquals(403, result.get("status"));
        verify(db, never()).deleteHoldfast(anyString());
    }

    @Test
    void deleteWithUnknownTokenIsUnauthorized() {
        Map<String, Object> result = manager.deleteHoldfastAs("expired", "party");
        assertEquals(401, result.get("status"));
        verify(db, never()).deleteHoldfast(anyString());
    }

    @Test
    void deleteAsAdminRemovesIt() {
        Map<String, Object> result = manager.deleteHoldfastAs("admin-token", "party");
        assertEquals(200, result.get("status"));
        verify(db).deleteHoldfast("party");
    }

    @Test
    void deleteAsAdminOnMissingHoldfastIsNotFound() {
        Map<String, Object> result = manager.deleteHoldfastAs("admin-token", "nobody");
        assertEquals(404, result.get("status"));
        verify(db, never()).deleteHoldfast(anyString());
    }
}
