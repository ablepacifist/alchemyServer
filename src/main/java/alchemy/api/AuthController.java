package alchemy.api;

import alchemy.object.Player;
import alchemy.logic.PlayerManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Exposes authentication endpoints:
 *   - POST /api/auth/register
 *   - POST /api/auth/login
 */
@RestController@CrossOrigin(origins = {"http://45.44.165.5:8080", "http://www.mypublicdomain.com"})
@RequestMapping("/api/auth")

public class AuthController {

    @Autowired
    private PlayerManagerService playerManagerService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> payload) {
        String username = payload.get("username");
        String password = payload.get("password");

        // Check if username already exists
        if (playerManagerService.getPlayerByUsername(username) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                                 .body("Username is already taken.");
        }
        // add second password screen
        // Delegate registration to the logic layer.
        playerManagerService.registerPlayer(username, password, password);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Registration successful");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> payload) {
        String username = payload.get("username");
        String password = payload.get("password");

        // Delegate login to the logic layer.
        Player player = playerManagerService.loginPlayer(username, password);
        if (player == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                 .body("Invalid username or password");
        }

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Login successful");
        // Return player information (for example, the player's unique id)
        response.put("playerId", player.getId());
        return ResponseEntity.ok(response);
    }
}
