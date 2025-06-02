package alchemy.api;

import alchemy.logic.GameManagerService;
import alchemy.object.Potion;
import alchemy.object.IIngredient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Exposes potion-related endpoint:
 *   - POST /api/potion/brew
 */
@RestController
@RequestMapping("/api/potion")
@CrossOrigin(origins = "*")
public class PotionController {

    @Autowired
    private GameManagerService gameManagerService;
    
    @PostMapping("/brew")
    public ResponseEntity<?> brewPotion(@RequestBody Map<String, Object> payload) {
        int playerId = (int) payload.get("playerId");
        int ingredientId1 = (int) payload.get("ingredientId1");
        int ingredientId2 = (int) payload.get("ingredientId2");

        // You must implement a method to retrieve IIngredient objects from your database or logic layer.
        IIngredient ingredient1 = fetchIngredientById(ingredientId1);
        IIngredient ingredient2 = fetchIngredientById(ingredientId2);
        
        if (ingredient1 == null || ingredient2 == null) {
            return ResponseEntity.badRequest().body("Invalid ingredient selection.");
        }

        Potion potion = gameManagerService
                .getPotionManager()
                .brewPotion(playerId, ingredient1, ingredient2);
        if (potion == null) {
            return ResponseEntity.badRequest().body("Potion brewing failed.");
        }
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Potion brewed successfully");
        response.put("potion", potion);
        return ResponseEntity.ok(response);
    }

    // Pseudo-code: Replace this with a real implementation to retrieve an ingredient by ID.
    private IIngredient fetchIngredientById(int ingredientId) {
        // For example: return ingredientService.getIngredientById(ingredientId);
        return null; // <-- Replace with actual lookup
    }
}
