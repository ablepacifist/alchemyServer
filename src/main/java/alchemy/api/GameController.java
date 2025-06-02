package alchemy.api;

import alchemy.logic.GameManagerService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        gameManagerService.startGame();
        return ResponseEntity.ok("Game started.");
    }

    @PostMapping("/end")
    public ResponseEntity<?> endGame() {
        gameManagerService.endGame();
        return ResponseEntity.ok("Game ended.");
    }

    @GetMapping("/forage/{playerId}")
    public ResponseEntity<?> forage(@PathVariable int playerId) {
        String ingredientName = gameManagerService.forage(playerId);
        return ResponseEntity.ok("Foraged ingredient: " + ingredientName);
    }
}
