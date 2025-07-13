package alchemy.api;

import alchemy.logic.PlayerManagerService;
import alchemy.object.Player;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import org.springframework.security.core.userdetails.*;

@Service
public class PlayerDetailsService implements UserDetailsService {

    private final PlayerManagerService playerManager;

    @Autowired
    public PlayerDetailsService(@Lazy PlayerManagerService playerManager) {
        this.playerManager = playerManager;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("Attempting to load user: " + username);

        Player player = playerManager.getPlayerByUsername(username); // must come first
        if (player == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }

        System.out.println("Fetched password: " + player.getPassword());

        return new CustomUserDetails(player);
    }
}

