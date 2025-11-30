// src/main/java/alchemy/api/AuthController.java
package alchemy.api;

import alchemy.logic.PlayerManagerService;
import alchemy.object.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private PlayerManagerService playerManagerService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> payload, HttpServletRequest req) {
    String username = payload.get("username");
    String password = payload.get("password");

    try {
        var authToken = new UsernamePasswordAuthenticationToken(username, password);
        var auth = authManager.authenticate(authToken);

        SecurityContextHolder.getContext().setAuthentication(auth);
        req.getSession().setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());

        CustomUserDetails user = (CustomUserDetails) auth.getPrincipal();
        Map<String, Object> body = Map.of(
            "playerId", user.getPlayer().getId(),
            "username", user.getPlayer().getUsername()
        );
        return ResponseEntity.ok(body);

    } catch (AuthenticationException ex) {
        System.err.println("Authentication failed for user: " + username);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
    } catch (Exception ex) {
        System.err.println("Error in login: " + ex.getMessage());
        ex.printStackTrace();
        return ResponseEntity.status(500).body("Error during login: " + ex.getMessage());
    }
}


    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> payload) {
        try {
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
        } catch (Exception e) {
            System.err.println("Error in register: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error during registration: " + e.getMessage());
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        try {
            // Spring Security will inject Authentication; we can pull the principal from SecurityContext
            var auth = org.springframework.security.core.context.SecurityContextHolder
                           .getContext()
                           .getAuthentication();

            if (auth == null || !auth.isAuthenticated() ||
                auth.getPrincipal().equals("anonymousUser")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
            Player player = userDetails.getPlayer();
            return ResponseEntity.ok(player);
        } catch (Exception e) {
            System.err.println("Error in getCurrentUser: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error retrieving current user: " + e.getMessage());
        }
    }
}
