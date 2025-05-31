package test;

import java.sql.SQLException;
import java.util.*;
import data.IStubDatabase;
import object.Player;
import object.IInventory;
import object.IKnowledgeBook;
import object.Inventory;
import object.KnowledgeBook;
import object.IIngredient;
import object.IEffect;
import object.Potion;
import object.IPotion;

public class DummyDatabase implements IStubDatabase {
    // Internal storage for players, effects, ingredients, etc.
    private Map<String, Player> playersByUsername = new HashMap<>();
    private Map<Integer, Player> playersById = new HashMap<>();
    private Map<Integer, IInventory> inventories = new HashMap<>();
    private Map<Integer, IKnowledgeBook> knowledgeBooks = new HashMap<>();
    private Map<Integer, IEffect> effects = new HashMap<>();
    private Map<Integer, IIngredient> ingredients = new HashMap<>();
    private Map<Integer, Potion> potions = new HashMap<>();
    private int nextPlayerId = 1;
    private int nextPotionId =1;
    // Implement all methods from IStubDatabase.
    
    @Override
    public Player getPlayerByUsername(String username) {
        return playersByUsername.get(username);
    }
    
    @Override
    public void addPlayer(Player player) {
        playersByUsername.put(player.getUsername(), player);
        playersById.put(player.getId(), player);
        // Create and store a new inventory and knowledge book for every player.
        inventories.put(player.getId(), player.getInventory());
        knowledgeBooks.put(player.getId(), player.getKnowledgeBook());
    }
    
    @Override
    public int getNextPlayerId() {
        return nextPlayerId++;
    }
    
    @Override
    public Player getPlayer(int playerId) {
        return playersById.get(playerId);
    }
    
    @Override
    public IInventory getPlayerInventory(int playerId) {
        return inventories.get(playerId);
    }
    
    @Override
    public IKnowledgeBook getKnowledgeBook(int playerId) {
        return knowledgeBooks.get(playerId);
    }
    
    @Override
    public List<IIngredient> getAllIngredients() {
        return new ArrayList<>(ingredients.values());
    }
    
    @Override
    public void addIngredientToPlayerInventory(int playerId, IIngredient ingredient, int quantity) {
        // For testing, update the inventory map.
        Inventory inv = (Inventory) inventories.get(playerId);
        inv.addIngredient(ingredient, quantity);
    }
    
    @Override
    public void removeIngredientFromPlayerInventory(int playerId, IIngredient ingredient, int quantity) {
        Inventory inv = (Inventory) inventories.get(playerId);
        inv.removeIngredient(ingredient, quantity);
    }
    
    @Override
    public List<IEffect> getEffectsForIngredient(int ingredientId) {
        IIngredient ingr = ingredients.get(ingredientId);
        if (ingr == null) return new ArrayList<>();
        return ingr.getEffects();
    }
    
    @Override
    public void addKnowledgeEntry(int playerId, IIngredient ingredient, IEffect effect) {
        IKnowledgeBook kb = knowledgeBooks.get(playerId);
        kb.addKnowledge(ingredient, effect);
    }
    
    @Override
    public void removePotionFromPlayerInventory(int playerId, Potion potion, int quantity) {
        Inventory inv = (Inventory) inventories.get(playerId);
        inv.removePotion(potion, quantity);
    }
    
    @Override
    public void updatePlayerLevel(int playerId, int newLevel) {
        Player player = playersById.get(playerId);
        if (player != null) {
            player.setLevel(newLevel);
        }
    }
    
    // Additional helper methods for testing:
    public void addEffect(IEffect effect) {
        effects.put(effect.getId(), effect);
    }
    
    public void addIngredient(IIngredient ingredient) {
        ingredients.put(ingredient.getId(), ingredient);
    }
@Override
    public int getNextPotionId() {
        return nextPotionId++;
    }
    
    @Override
    public IIngredient getIngredientByName(String name) {
        for (IIngredient ingr : ingredients.values()) {
            if (ingr.getName().equalsIgnoreCase(name)) {
                return ingr;
            }
        }
        return null;
    }
    
    @Override
    public Collection<Player> getAllPlayers() {
        return playersById.values();
    }
    
    @Override
    public void addPotionToPlayerInventory(int playerId, Potion potion, int quantity) {
        Inventory inv = (Inventory) inventories.get(playerId);
        inv.addPotion(potion, quantity);
    }
    
    @Override
    public void updateKnowledgeBook(int playerId, IIngredient ingredient) {
        // For our dummy implementation, we'll assume the knowledge book 
        // automatically updates as entries are added (via addKnowledgeEntry).
        // No additional action is needed.
    }
    
    @Override
    public IIngredient getIngredientById(int ingredientId) {
        return ingredients.get(ingredientId);
    }
    
    @Override
    public void addPotion(Potion potion) {
        potions.put(potion.getId(), potion);
    }

    @Override
    public void deletePlayer(int playerId) throws SQLException {
        Player player = playersById.remove(playerId);
        throw new UnsupportedOperationException("Unimplemented method 'deletePlayer'");
    }
    

}