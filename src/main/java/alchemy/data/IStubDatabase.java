package data;

import java.util.List;
import java.util.Map;
import java.sql.SQLException;
import java.util.Collection; 

import object.IEffect;
import object.IIngredient;
import object.IInventory;
import object.IKnowledgeBook;
import object.Player;
import object.Potion;

public interface IStubDatabase {

    int getNextPotionId();

    int getNextPlayerId();

    IIngredient getIngredientByName(String name);

    List<IIngredient> getAllIngredients();

    void addPlayer(Player player);

    Player getPlayer(int playerId);

    Player getPlayerByUsername(String username);

    Collection<Player> getAllPlayers();

    void addPotionToPlayerInventory(int playerId, Potion potion, int quantity);

    void addIngredientToPlayerInventory(int playerId, IIngredient ingredient, int quantity);

    void removeIngredientFromPlayerInventory(int playerId, IIngredient ingredient, int quantity);

    IInventory getPlayerInventory(int playerId);

    void addKnowledgeEntry(int playerId, IIngredient ingredient, IEffect effect);

    void updateKnowledgeBook(int playerId, IIngredient ingredient);

    public IKnowledgeBook getKnowledgeBook(int playerId);
    List<IEffect> getEffectsForIngredient(int ingredientId);

    void addIngredient(IIngredient ingWithShared1);

    void addEffect(IEffect uniqueEffect2);
    void removePotionFromPlayerInventory(int playerId, Potion potion, int quantity);
    IIngredient getIngredientById(int ingredientId);
    void addPotion(Potion potion);

    void updatePlayerLevel(int id, int level);
    //void resetDatabase() throws SQLException;
    void deletePlayer(int playerId) throws SQLException;
}
