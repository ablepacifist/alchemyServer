package alchemy.api;

import alchemy.logic.GameManagerService;
import alchemy.object.Potion;
import alchemy.object.IIngredient;
import alchemy.object.IInventory;

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

        // Retrieve the ingredients from the player's inventory.
        IIngredient ingredient1 = fetchIngredientById(playerId, ingredientId1);
        IIngredient ingredient2 = fetchIngredientById(playerId, ingredientId2);
        
        
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

private IIngredient fetchIngredientById(int playerId, int ingredientId) {
        // Get the player's inventory.
        IInventory inventory = gameManagerService.getPlayerManager().getInventory(playerId);
        if (inventory == null) {
            return null; 
        }
        
        //this might be a place for failiure later
        //TODO: check to see if this is actually working
        for (IIngredient ingredient : inventory.getIngredients().keySet()) {
            if (ingredient.getId() == ingredientId) { 
                return ingredient;
            }
        }
        return null;
    }
}
