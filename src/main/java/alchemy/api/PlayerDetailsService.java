package alchemy.api;

import alchemy.logic.PlayerManagerService;
import alchemy.object.Player;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import org.springframework.security.core.userdetails.*;

@Service
public class PlayerDetailsService implements UserDetailsService, UserDetailsPasswordService {

    private final PlayerManagerService playerManager;

    @Autowired
    public PlayerDetailsService(@Lazy PlayerManagerService playerManager) {
        this.playerManager = playerManager;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Player player = playerManager.getPlayerByUsername(username); // must come first
        if (player == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }

        return new CustomUserDetails(player);
    }

    /**
     * Called by DaoAuthenticationProvider after a successful login when the stored password was a
     * legacy plaintext value (see UpgradingPasswordEncoder). Persists the BCrypt hash.
     */
    @Override
    public UserDetails updatePassword(UserDetails user, String newPassword) {
        if (user instanceof CustomUserDetails details) {
            Player old = details.getPlayer();
            playerManager.updatePlayerPassword(old.getId(), newPassword);
            System.out.println("Upgraded stored password to a BCrypt hash for player id " + old.getId());
            return new CustomUserDetails(new Player(old.getId(), old.getUsername(), newPassword,
                    old.getInventory(), old.getKnowledgeBook(), old.getLevel()));
        }
        return user;
    }
}

