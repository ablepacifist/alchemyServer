package logic;


import java.util.Collection;

import object.IIngredient;
import object.IInventory;
import object.IKnowledgeBook;
import object.IPotion;
import object.Player;
import object.Potion;

/**
 * The PlayerManagerService interface declares operations related to player management
 * including player creation, login, inventory manipulation, knowledge updates,
 * and potion brewing/consumption.
 */
public interface PlayerManagerService {

    /**
     * Retrieves a player by their unique id.
     *
     * @param playerId the player's id
     * @return the Player, or null if not found
     */
    Player getPlayerById(int playerId);

    /**
     * Retrieves a player by their username.
     *
     * @param username the player's username
     * @return the Player object, or null if not found
     */
    Player getPlayerByUsername(String username);

    /**
     * Retrieves all players.
     *
     * @return a collection of all players
     */
    Collection<Player> getAllPlayers();

    /**
     * Creates a new player with an empty inventory and knowledge book.
     *
     * @param username the new player's username
     * @param password the new player's password
     */
    void createPlayer(String username, String password);

    /**
     * Registers a new player. If the username is already taken, the call may simply return.
     *
     * @param username the new player's username
     * @param password the new player's password
     */
    void registerPlayer(String username, String password);

    /**
     * Logins a player with the supplied credentials.
     *
     * @param username the username
     * @param password the password
     * @return the Player if credentials are valid; otherwise, null
     */
    Player loginPlayer(String username, String password);

    /**
     * Adds a specified quantity of an ingredient to a player's inventory.
     *
     * @param playerId   the player's id
     * @param ingredient the ingredient to add
     * @param quantity   the quantity to add
     */
    void addIngredientToPlayer(int playerId, IIngredient ingredient, int quantity);

    /**
     * Removes a specified quantity of an ingredient from a player's inventory.
     *
     * @param playerId   the player's id
     * @param ingredient the ingredient to remove
     * @param quantity   the quantity to remove
     */
    void removeIngredientFromPlayer(int playerId, IIngredient ingredient, int quantity);

    /**
     * Performs a forage action: randomly selects an ingredient from the master list and
     * adds one unit of it to the player's inventory.
     *
     * @param playerId the player's id
     * @return the name of the foraged ingredient, or an empty string if none available
     */
    String forage(int playerId);

    /**
     * Retrieves the player's inventory.
     *
     * @param playerId the player's id
     * @return the player's inventory
     */
    IInventory getInventory(int playerId);

    /**
     * Retrieves the player's knowledge book.
     *
     * @param playerId the player's id
     * @return the player's knowledge book
     */
    IKnowledgeBook getKnowledgeBook(int playerId);

    /**
     * Brews a potion by combining two ingredients from the player's inventory. If the two
     * ingredients share at least one effect, a new potion is created and added to the player's
     * potion inventory. One unit of each ingredient is removed.
     *
     * @param playerId   the player's id
     * @param ingredient1 the first ingredient
     * @param ingredient2 the second ingredient
     * @return the newly brewed Potion if successful; otherwise, null
     */
    Potion brewPotion(int playerId, IIngredient ingredient1, IIngredient ingredient2);

    /**
     * Consumes a potion by removing one unit of it from the player's inventory.
     *
     * @param playerId the player's id
     * @param potion   the potion to consume
     */
    void consumePotion(int playerId, IPotion potion);

    /**
     * Consumes an ingredient by removing one unit from the player's inventory. If there is any
     * master effect for that ingredient that the player has not learned yet, one such effect is
     * added to the player's knowledge book.
     *
     * @param playerId   the player's id
     * @param ingredient the ingredient to consume
     */
    void consumeIngredient(int playerId, IIngredient ingredient);
}
