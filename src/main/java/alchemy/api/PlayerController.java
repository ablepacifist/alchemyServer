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
    // New Endpoint for Inventory Retrieval
    // -----------------------------------------------
    @GetMapping("/inventory/{playerId}")
public ResponseEntity<?> getInventory(@PathVariable int playerId) {
    IInventory inventory = playerManagerService.getInventory(playerId);
    if (inventory == null) {
        return ResponseEntity.notFound().build();
    }
    
    // Get the player's knowledge book
    IKnowledgeBook kb = playerManagerService.getKnowledgeBook(playerId);
    
    List<Map<String, Object>> ingredientsList = new ArrayList<>();
    inventory.getIngredients().forEach((ingredient, quantity) -> {
        Map<String, Object> ingData = new HashMap<>();
        ingData.put("id", ingredient.getId());
        ingData.put("name", ingredient.getName());
        
        // Instead of taking the full master list, only add effects the user knows.
        List<Map<String, Object>> knownEffects = new ArrayList<>();
        for (IEffect effect : ingredient.getEffects()) {
            // Only include this effect if the knowledge book indicates it is known.
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
            potionsList.add(potData);
        });
    }
    
    Map<String, Object> response = new HashMap<>();
    response.put("ingredients", ingredientsList);
    response.put("potions", potionsList);
    
    return ResponseEntity.ok(response);
}

    // -----------------------------------------------
    // New Endpoint for Foraging
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
}
