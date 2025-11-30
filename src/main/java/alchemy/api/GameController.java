package alchemy.api;

import alchemy.logic.GameManagerService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
// these api calls arent used very much yet. but they are still availible for the
// user to fuck around with.
/**
 * Exposes game management endpoints:
 *   - POST /api/game/start
 *   - POST /api/game/end
 *   - GET /api/game/forage/{playerId}
 */
@RestController
@RequestMapping("/api/game")
@CrossOrigin(origins = "*")
public class GameController {

    @Autowired
    private GameManagerService gameManagerService;

    @PostMapping("/start")
    public ResponseEntity<?> startGame() {
        try {
            gameManagerService.startGame();
            return ResponseEntity.ok("Game started.");
        } catch (Exception e) {
            System.err.println("Error in startGame: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error starting game: " + e.getMessage());
        }
    }

    @PostMapping("/end")
    public ResponseEntity<?> endGame() {
        try {
            gameManagerService.endGame();
            return ResponseEntity.ok("Game ended.");
        } catch (Exception e) {
            System.err.println("Error in endGame: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error ending game: " + e.getMessage());
        }
    }

    @GetMapping("/forage/{playerId}")
    public ResponseEntity<?> forage(@PathVariable int playerId) {
        try {
            String ingredientName = gameManagerService.forage(playerId);
            return ResponseEntity.ok("Foraged ingredient: " + ingredientName);
        } catch (Exception e) {
            System.err.println("Error in GameController.forage: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error during foraging: " + e.getMessage());
        }
    }
}
