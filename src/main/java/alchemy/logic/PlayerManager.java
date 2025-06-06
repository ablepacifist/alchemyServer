package alchemy.logic;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import alchemy.data.IStubDatabase;
import alchemy.object.Inventory;
import alchemy.object.IIngredient;
import alchemy.object.IInventory;
import alchemy.object.IKnowledgeBook;
import alchemy.object.IEffect;
import alchemy.object.IPotion;
import alchemy.object.KnowledgeBook;
import alchemy.object.Player;
import alchemy.object.Potion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
@Component
@Service
public class PlayerManager  implements PlayerManagerService{
    private final IStubDatabase db;

    @Autowired
    public PlayerManager(IStubDatabase db) {
        this.db = db;
    }
    public Player loginPlayer(String username, String password) {
        Player player = db.getPlayerByUsername(username);
        if (player == null) {
            throw new IllegalArgumentException("Invalid username. No such player exists.");
        }

        if (!player.getPassword().equals(password)) {
            throw new IllegalArgumentException("Invalid password. Please try again.");
        }

        return player;
    }

    public void registerPlayer(String username, String password, String confirmPassword) {
        if (!password.equals(confirmPassword)) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        // Check if a player with this username already exists.
        Player existingPlayer = db.getPlayerByUsername(username);
        if (existingPlayer != null) {
            throw new IllegalArgumentException("Username already taken. Please choose a different username.");
        }

        // Create the player.
        createPlayer(username, password);
    }

    /**
     * Create a new player with an empty inventory and knowledge book.
     */
    public void createPlayer(String username, String password) {
        int playerId = db.getNextPlayerId();
        Inventory inventory = new Inventory();
        IKnowledgeBook knowledgeBook = new KnowledgeBook(new HashMap<>());
        Player newPlayer = new Player(playerId, username, password, inventory, knowledgeBook); // Level starts at 1
        db.addPlayer(newPlayer);
        System.out.println("Player created: " + newPlayer);
    }



    public Player getPlayerById(int playerId) {
        return db.getPlayer(playerId);
    }

    public Player getPlayerByUsername(String username) {
        return db.getPlayerByUsername(username);
    }

    /**
     * Get the player's inventory.
     */
    public IInventory getInventory(int playerId) {
        return db.getPlayerInventory(playerId);
    }
    public IKnowledgeBook getKnowledgeBook(int playerId) {
        return db.getKnowledgeBook(playerId);
    }

    /**
     * Forage: Randomly selects an ingredient from the master list and adds one unit
     * of it to the player's inventory.
     *
     * @param playerId the ID of the player for whom to forage
     * @return the name of the foraged ingredient, or an empty string if none available.
     */
    public String forage(int playerId) {
        // Retrieve the player to check their level.
        Player player = db.getPlayer(playerId);
        int level = player.getLevel();

        // Roll a 4-sided dice to determine the quantity of ingredients.
        Random rand = new Random();
        int baseQuantity = rand.nextInt(4) + 1; // 1d4 roll (1 to 4)

        // At level 3 or above, the player gets double the rolled quantity.
        int quantity = (level >= 3 ? baseQuantity * 2 : baseQuantity);

        List<IIngredient> masterIngredients = db.getAllIngredients();
        if (masterIngredients.isEmpty()) {
            System.out.println("No ingredients available to forage.");
            return "";
        }
        IIngredient randomIngredient = masterIngredients.get(rand.nextInt(masterIngredients.size()));

        // Add the determined quantity of the ingredient.
        db.addIngredientToPlayerInventory(playerId, randomIngredient, quantity);
        System.out.println("Foraged " + quantity + " unit(s) of "
                + randomIngredient.getName() + " for player " + playerId);
        return randomIngredient.getName();
    }

    /**
     * Consumes one unit of the specified ingredient from the player's inventory.
     * After consumption, if there is an effect on that ingredient that the player hasn't learned yet,
     * that effect is added to the player's KnowledgeBook.
     *
     * @param playerId   the ID of the player
     * @param ingredient the ingredient to consume
     */
    public void consumeIngredient(int playerId, IIngredient ingredient) {
        // Remove one unit of the ingredient.
        db.removeIngredientFromPlayerInventory(playerId, ingredient, 1);
        System.out.println("Consumed one unit of " + ingredient.getName() + " for player " + playerId);

        // Retrieve master effects for the given ingredient.
        List<IEffect> masterEffects = db.getEffectsForIngredient(ingredient.getId());

        // Retrieve the player's current knowledge from the knowledge book.
        IKnowledgeBook kb = db.getKnowledgeBook(playerId);

        // Look for the first effect that is not already known.
        IEffect effectToLearn = null;
        for (IEffect effect : masterEffects) {
            if (!kb.hasKnowledge(ingredient, effect)) {
                effectToLearn = effect;
                break;
            }
        }

        if (effectToLearn != null) {
            // Use the public API on KnowledgeBook to add the new effect.
            kb.addKnowledge(ingredient, effectToLearn);
            // Also update through the database if needed.
            db.addKnowledgeEntry(playerId, ingredient, effectToLearn);
            System.out.println("Player " + playerId + " learned new effect: " + effectToLearn.getTitle());
        } else {
            System.out.println("Player " + playerId + " already knows all effects for " + ingredient.getName());
        }
    }


    /**
     * Consumes one unit of the specified potion from the player's inventory.
     * Consuming a potion might, for example, apply the potion's effect to the player.
     * Here, we'll simply remove one unit from inventory.
     *
     * @param playerId the ID of the player
     * @param potion   the potion to consume
     */
    public void consumePotion(int playerId, IPotion potion) {
        // Remove one unit of the potion from the player's inventory.
        db.removePotionFromPlayerInventory(playerId, (Potion) potion, 1);
        System.out.println("Player " + playerId + " consumed potion: " + potion.getName());

        // Optional: trigger additional logic here (e.g., apply the potion's effects)
    }

    public boolean levelUpPlayer(Player player, String secretPassword) {
        // Check if the secret password is correct.
        if (!"az1209lm".equals(secretPassword)) {
            throw new IllegalArgumentException("Incorrect password for leveling up.");
        }

        // Check if already at maximum level.
        if (player.getLevel() >= 10) {
            return false;
        }

        // Increase level and update persistent storage.
        if (player.levelUp()) {
            db.updatePlayerLevel(player.getId(), player.getLevel());
            return true;
        }
        return false;
    }

    
    @Override
    public List<Player> getAllPlayers() {
        return (List<Player>) db.getAllPlayers();
    }


/**
 * Look up an ingredient by id in the player's inventory.
 */
public IIngredient getIngredientFromInventory(int playerId, int ingredientId) {
    IInventory inventory = db.getPlayerInventory(playerId);
    // The inventory returns a Map<IIngredient, Integer>
    for (IIngredient ingredient : inventory.getIngredients().keySet()) {
        if (ingredient.getId() == ingredientId) {
            return ingredient;
        }
    }
    return null;
}

/**
 * Look up a potion by id in the player's inventory.
 */
public IPotion getPotionFromInventory(int playerId, int potionId) {
    IInventory inventory = db.getPlayerInventory(playerId);
    // The inventory returns a Map<IPotion, Integer>
    for (IPotion potion : inventory.getPotions().keySet()) {
        if (potion.getId() == potionId) {
            return potion;
        }
    }
    return null;
}







}
