package alchemy.api;

import alchemy.object.Player;
import alchemy.logic.PlayerManagerService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import alchemy.logic.PlayerManagerService;
import alchemy.object.IEffect;
import alchemy.object.IIngredient;
import alchemy.object.IPotion;
import alchemy.object.Player;
import alchemy.object.IInventory;
import alchemy.object.IKnowledgeBook;

@RestController
@RequestMapping("/api/player")
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

    // -----------------------------------------------
    //Endpoint for Inventory Retrieval
    // -----------------------------------------------
 @GetMapping("/inventory/{playerId}")
public ResponseEntity<?> getInventory(@PathVariable int playerId) {
    IInventory inventory = playerManagerService.getInventory(playerId);
    if (inventory == null) {
        return ResponseEntity.notFound().build();
    }

    // Get the player's knowledge book (used for filtering ingredient effects)
    IKnowledgeBook kb = playerManagerService.getKnowledgeBook(playerId);

    List<Map<String, Object>> ingredientsList = new ArrayList<>();
    inventory.getIngredients().forEach((ingredient, quantity) -> {
        Map<String, Object> ingData = new HashMap<>();
        ingData.put("id", ingredient.getId());
        ingData.put("name", ingredient.getName());

        // Only include effects the user knows.
        List<Map<String, Object>> knownEffects = new ArrayList<>();
        for (IEffect effect : ingredient.getEffects()) {
            if (kb.hasKnowledge(ingredient, effect)) {
                Map<String, Object> effectData = new HashMap<>();
                effectData.put("id", effect.getId());
                effectData.put("title", effect.getTitle());
                effectData.put("description", effect.getDescription());
                knownEffects.add(effectData);
            }
        }
        ingData.put("effects", knownEffects);
        ingData.put("quantity", quantity);
        ingredientsList.add(ingData);
    });

    List<Map<String, Object>> potionsList = new ArrayList<>();
    if (inventory.getPotions() != null) {
        inventory.getPotions().forEach((potion, quantity) -> {
            Map<String, Object> potData = new HashMap<>();
            potData.put("id", potion.getId());
            potData.put("name", potion.getName());
            potData.put("quantity", quantity);
            // Adding additional potion details:
            potData.put("description", potion.getDescription());
            potData.put("duration", potion.getDuration());
            potData.put("brewLevel", potion.getBrewLevel());
            potData.put("dice", (potion.getDice() != null && !potion.getDice().isEmpty()) ? potion.getDice() : "None");
            // If you want to show the full list of effects from the potion (the master list,
            // since potions usually don’t need filtering by knowledge), include them:
            List<Map<String, Object>> effectsList = new ArrayList<>();
            if (potion.getEffects() != null) {
                for (IEffect effect : potion.getEffects()) {
                    Map<String, Object> effectData = new HashMap<>();
                    effectData.put("id", effect.getId());
                    effectData.put("title", effect.getTitle());
                    effectData.put("description", effect.getDescription());
                    effectsList.add(effectData);
                }
            }
            potData.put("effects", effectsList);
            potionsList.add(potData);
        });
    }

    Map<String, Object> response = new HashMap<>();
    response.put("ingredients", ingredientsList);
    response.put("potions", potionsList);

    return ResponseEntity.ok(response);
}


    // -----------------------------------------------
    // Endpoint for Foraging
    // -----------------------------------------------
    @GetMapping("/forage/{playerId}")
    public ResponseEntity<?> forage(@PathVariable int playerId) {
        String foragedIngredient = playerManagerService.forage(playerId);
        if (foragedIngredient.isEmpty()) {
            return ResponseEntity.badRequest().body("No ingredient available to forage.");
        }
        Map<String, String> response = new HashMap<>();
        response.put("forage", foragedIngredient);
        return ResponseEntity.ok(response);
    }

    /**
     * Consume an ingredient from the player's inventory.
     * Expected JSON payload: { "playerId": <number>, "ingredientId": <number> }
     */
    @PostMapping("/ingredient/consume")
    public ResponseEntity<?> consumeIngredient(@RequestBody Map<String, Object> payload) {
        try {
            int playerId = (Integer) payload.get("playerId");
            int ingredientId = (Integer) payload.get("ingredientId");
            // Use the helper method to get the ingredient from the player's inventory.
            IIngredient ingredient = playerManagerService.getIngredientFromInventory(playerId, ingredientId);
            if (ingredient == null) {
                return ResponseEntity.badRequest().body("Ingredient not found in inventory.");
            }
            playerManagerService.consumeIngredient(playerId, ingredient);
            return ResponseEntity.ok("Ingredient consumed successfully.");
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    /**
     * Consume a potion from the player's inventory.
     * Expected JSON payload: { "playerId": <number>, "potionId": <number> }
     */
    @PostMapping("/potion/consume")
    public ResponseEntity<?> consumePotion(@RequestBody Map<String, Object> payload) {
        try {
            int playerId = (Integer) payload.get("playerId");
            int potionId = (Integer) payload.get("potionId");
            // Use the helper method to get the potion from the player's inventory.
            IPotion potion = playerManagerService.getPotionFromInventory(playerId, potionId);
            if (potion == null) {
                return ResponseEntity.badRequest().body("Potion not found in inventory.");
            }
            playerManagerService.consumePotion(playerId, potion);
            return ResponseEntity.ok("Potion consumed successfully.");
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

@PostMapping("/levelup")
public ResponseEntity<?> levelUpPlayer(@RequestBody Map<String, Object> payload) {
    try {
        int playerId = (Integer) payload.get("playerId");
        String secretPassword = (String) payload.get("secretPassword");
        Player player = playerManagerService.getPlayerById(playerId);
        if (player == null) {
            return ResponseEntity.badRequest().body("Player not found.");
        }
        boolean leveledUp = playerManagerService.levelUpPlayer(player, secretPassword);
        if (!leveledUp) {
            return ResponseEntity.badRequest().body("Level up failed: either maximum level reached or incorrect password.");
        }
        // Return the updated player object (or just a success message as you prefer).
        return ResponseEntity.ok(player);
    } catch (Exception ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}


@GetMapping("/knowledge/{playerId}")
public ResponseEntity<?> getKnowledgeBook(@PathVariable int playerId) {
    IKnowledgeBook kb = playerManagerService.getKnowledgeBook(playerId);
    IInventory inventory = playerManagerService.getInventory(playerId); // Get inventory for ingredient names
    
    if (kb == null || inventory == null) {
        return ResponseEntity.notFound().build();
    }
    
    Map<Integer, List<IEffect>> knowledgeMap = kb.toMap();
    List<Map<String, Object>> knowledgeList = new ArrayList<>();

    for (Map.Entry<Integer, List<IEffect>> entry : knowledgeMap.entrySet()) {
        int ingredientId = entry.getKey();
        List<IEffect> effects = entry.getValue();

        Map<String, Object> kbEntry = new HashMap<>();
        kbEntry.put("ingredientId", ingredientId);

        // Find the ingredient in the player's inventory to get its name
        String ingredientName = "Unknown Ingredient";
        for (IIngredient ingredient : inventory.getIngredients().keySet()) {
            if (ingredient.getId() == ingredientId) {
                ingredientName = ingredient.getName();
                break;
            }
        }
        kbEntry.put("ingredientName", ingredientName); // Now correctly stores the name
        
        List<Map<String, Object>> effectsList = new ArrayList<>();
        for (IEffect effect : effects) {
            Map<String, Object> effData = new HashMap<>();
            effData.put("id", effect.getId());
            effData.put("title", effect.getTitle());
            effData.put("description", effect.getDescription());
            effectsList.add(effData);
        }
        kbEntry.put("effects", effectsList);
        knowledgeList.add(kbEntry);
    }
    
    return ResponseEntity.ok(knowledgeList);
}






    
}
