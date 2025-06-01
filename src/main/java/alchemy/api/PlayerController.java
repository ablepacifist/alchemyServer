package alchemy.api;

import alchemy.object.Player;
import alchemy.logic.PlayerManagerService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Exposes player management endpoints:
 *   - GET /api/player/{id}
 *   - GET /api/player/username/{username}
 *   - GET /api/player/all
 */
@RestController
@RequestMapping("/api/player")
@CrossOrigin(origins = {"http://45.44.165.5:8080", "http://www.mypublicdomain.com"})
public class PlayerController {

    @Autowired
    private PlayerManagerService playerManagerService;

    @GetMapping("/{id}")
    public ResponseEntity<?> getPlayerById(@PathVariable int id) {
        Player player = playerManagerService.getPlayerById(id);
        if (player == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(player);
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<?> getPlayerByUsername(@PathVariable String username) {
        Player player = playerManagerService.getPlayerByUsername(username);
        if (player == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(player);
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllPlayers() {
        List<Player> players = (List<Player>) playerManagerService.getAllPlayers();
        return ResponseEntity.ok(players);
    }
}
