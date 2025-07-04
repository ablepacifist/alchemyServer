package alchemy.api;

import alchemy.object.Player;
import alchemy.logic.PlayerManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession; // <-- Added to manage session

import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://96.37.95.22:3000", allowCredentials = "true") // <-- Enforce correct CORS behavior
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private PlayerManagerService playerManagerService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> payload) {
        String username = payload.get("username");
        String password = payload.get("password");

        if (playerManagerService.getPlayerByUsername(username) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                                 .body("Username is already taken.");
        }

        playerManagerService.registerPlayer(username, password, password);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Registration successful");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> payload, HttpSession session) { // <-- Added session arg
        String username = payload.get("username");
        String password = payload.get("password");

        Player player = playerManagerService.loginPlayer(username, password);
        if (player == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                 .body("Invalid username or password");
        }

        // Store player in session
        session.setAttribute("user", player); // <-- Store user in session

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Login successful");
        response.put("playerId", player.getId());
        response.put("username", player.getUsername()); // <-- Added username to match frontend
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me") // <-- New endpoint for session restoration
    public ResponseEntity<?> getCurrentUser(HttpSession session) {
        Object user = session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(user);
    }
}
