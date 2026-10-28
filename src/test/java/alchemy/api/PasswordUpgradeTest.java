package alchemy.api;

import alchemy.logic.PlayerManagerService;
import alchemy.object.Inventory;
import alchemy.object.KnowledgeBook;
import alchemy.object.Player;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class PasswordUpgradeTest {

    private final UpgradingPasswordEncoder encoder = new UpgradingPasswordEncoder();

    private Player player(String stored) {
        return new Player(7, "someone", stored, new Inventory(), new KnowledgeBook(new HashMap<>()));
    }

    private DaoAuthenticationProvider provider(PlayerManagerService manager) {
        PlayerDetailsService service = new PlayerDetailsService(manager);
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(service);
        provider.setUserDetailsPasswordService(service);
        provider.setPasswordEncoder(encoder);
        return provider;
    }

    @Test
    void bcryptHashStillWorksAndIsNotRewritten() {
        String hash = new BCryptPasswordEncoder().encode("secret");
        PlayerManagerService manager = mock(PlayerManagerService.class);
        when(manager.getPlayerByUsername("someone")).thenReturn(player(hash));

        Authentication auth = provider(manager)
                .authenticate(new UsernamePasswordAuthenticationToken("someone", "secret"));

        assertTrue(auth.isAuthenticated());
        verify(manager, never()).updatePlayerPassword(anyInt(), anyString());
    }

    @Test
    void legacyPlaintextLogsInOnceAndIsReplacedByBcrypt() {
        PlayerManagerService manager = mock(PlayerManagerService.class);
        when(manager.getPlayerByUsername("someone")).thenReturn(player("plain-old-password"));

        Authentication auth = provider(manager)
                .authenticate(new UsernamePasswordAuthenticationToken("someone", "plain-old-password"));

        assertTrue(auth.isAuthenticated());
        ArgumentCaptor<String> stored = ArgumentCaptor.forClass(String.class);
        verify(manager).updatePlayerPassword(eq(7), stored.capture());
        assertTrue(stored.getValue().startsWith("$2"), "stored value must now be a BCrypt hash");
        assertNotEquals("plain-old-password", stored.getValue());
        assertTrue(new BCryptPasswordEncoder().matches("plain-old-password", stored.getValue()));
    }

    @Test
    void wrongPasswordOnLegacyPlaintextIsRejectedAndNothingIsWritten() {
        PlayerManagerService manager = mock(PlayerManagerService.class);
        when(manager.getPlayerByUsername("someone")).thenReturn(player("plain-old-password"));

        assertThrows(BadCredentialsException.class, () -> provider(manager)
                .authenticate(new UsernamePasswordAuthenticationToken("someone", "wrong")));
        verify(manager, never()).updatePlayerPassword(anyInt(), anyString());
    }

    @Test
    void encoderBehaviour() {
        assertTrue(encoder.upgradeEncoding("plain"));
        assertFalse(encoder.upgradeEncoding(encoder.encode("x")));
        assertTrue(encoder.matches("x", encoder.encode("x")));
        assertFalse(encoder.matches("y", encoder.encode("x")));
        assertTrue(encoder.matches("plain", "plain"));
        assertFalse(encoder.matches("plain", "Plain"));
        assertFalse(encoder.matches(null, "plain"));
        assertFalse(encoder.matches("x", null));
    }
}
