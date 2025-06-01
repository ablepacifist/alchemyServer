package alchemy.logic;


import java.util.Collection;
import java.util.List;

import alchemy.object.IIngredient;
import alchemy.object.IInventory;
import alchemy.object.IKnowledgeBook;
import alchemy.object.IPotion;
import alchemy.object.Player;
import alchemy.object.Potion;

/**
 * The PlayerManagerService interface declares operations related to player management
 * including player creation, login, inventory manipulation, knowledge updates,
 * and potion brewing/consumption.
 */

import alchemy.object.IIngredient;
import alchemy.object.IInventory;
import alchemy.object.IKnowledgeBook;
import alchemy.object.IPotion;
import alchemy.object.Player;

public interface PlayerManagerService {

    /**
     * Logins a player by verifying the username and password.
     *
     * @param username the player's username
     * @param password the player's password
     * @return the Player if successfully authenticated
     * @throws IllegalArgumentException if login fails
     */
    Player loginPlayer(String username, String password);

    /**
     * Registers a new player after checking the password confirmation.
     *
     * @param username       the player's username
     * @param password       the player's password
     * @param confirmPassword confirmation of the password
     * @throws IllegalArgumentException if passwords do not match or username is taken
     */
    void registerPlayer(String username, String password, String confirmPassword);

    /**
     * Creates a new player.
     *
     * @param username the player's username
     * @param password the player's password
     */
    void createPlayer(String username, String password);

    /**
     * Retrieves a player by their unique ID.
     *
     * @param playerId the player's ID
     * @return the Player
     */
    Player getPlayerById(int playerId);

    /**
     * Retrieves a player by their username.
     *
     * @param username the player's username
     * @return the Player
     */
    Player getPlayerByUsername(String username);

    /**
     * Retrieves the inventory of the player.
     *
     * @param playerId the player's ID
     * @return the player's inventory
     */
    IInventory getInventory(int playerId);

    /**
     * Retrieves the knowledge book of the player.
     *
     * @param playerId the player's ID
     * @return the knowledge book
     */
    IKnowledgeBook getKnowledgeBook(int playerId);

    /**
     * Performs a forage operation and returns the name of the foraged ingredient.
     *
     * @param playerId the player's ID
     * @return the name of the foraged ingredient or an empty string if none available
     */
    String forage(int playerId);

    /**
     * Consumes one unit of the specified ingredient from the player's inventory.
     *
     * @param playerId   the player's ID
     * @param ingredient the ingredient to consume
     */
    void consumeIngredient(int playerId, IIngredient ingredient);

    /**
     * Consumes one unit of the specified potion from the player's inventory.
     *
     * @param playerId the player's ID
     * @param potion   the potion to consume
     */
    void consumePotion(int playerId, IPotion potion);

    /**
     * Levels up the player if the secret password is correct.
     *
     * @param player         the player to level up
     * @param secretPassword the secret password required for leveling up
     * @return true if the player leveled up, false otherwise
     * @throws IllegalArgumentException if the secret password is incorrect
     */
    boolean levelUpPlayer(Player player, String secretPassword);

    List<Player> getAllPlayers();
}
