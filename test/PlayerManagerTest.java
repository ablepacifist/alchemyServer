package test;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import data.IStubDatabase;
import logic.PlayerManager;
import object.Player;
import object.Inventory;
import object.IKnowledgeBook;
import object.IIngredient;
import object.IEffect;
import object.Potion;
import object.IPotion;
import object.KnowledgeBook;
import object.Effect;
import object.Ingredient;

public class PlayerManagerTest {
    private IStubDatabase db;
    private PlayerManager playerManager;
    private Player testPlayer;
    private int playerId;

    // Effects used in tests.
    private IEffect sharedEffect;
    private IEffect uniqueEffect1;
    private IEffect uniqueEffect2;

    // Ingredients for different scenarios.
    private IIngredient ingWithShared1;
    private IIngredient ingWithShared2;
    private IIngredient ingNoShared;

    @Before
    public void setUp() {
        // Create a fresh dummy database instance for every test.
        db = new DummyDatabase();
        playerManager = new PlayerManager(db);

        // Create effects.
        sharedEffect = new Effect(100, "Spark", "Shared spark effect.");
        uniqueEffect1 = new Effect(101, "Glow", "Unique glow effect.");
        uniqueEffect2 = new Effect(102, "Shine", "Unique shine effect.");

        // Add effects to the database.
        db.addEffect(sharedEffect);
        db.addEffect(uniqueEffect1);
        db.addEffect(uniqueEffect2);

        // Create ingredients.
        // Two ingredients that share "Spark" plus one extra unique effect.
        ingWithShared1 = new Ingredient(200, "HerbA", Arrays.asList(sharedEffect, uniqueEffect1));
        ingWithShared2 = new Ingredient(201, "HerbB", Arrays.asList(sharedEffect, uniqueEffect2));
        // An ingredient that does not share any effect with ingWithShared1.
        ingNoShared = new Ingredient(202, "HerbC", Arrays.asList(uniqueEffect2));

        // Add ingredients to the database.
        db.addIngredient(ingWithShared1);
        db.addIngredient(ingWithShared2);
        db.addIngredient(ingNoShared);

        // Create and add a test player with a new, empty inventory and an empty knowledge book.
        playerId = db.getNextPlayerId();
        testPlayer = new Player(playerId, "potionTester", "testpass",
                new Inventory(), new KnowledgeBook(new HashMap<>()));
        db.addPlayer(testPlayer);
    }

    // ----- Existing Tests -----
    @Test
    public void testCreatePlayer() {
        String username = "TestUser";
        String password = "TestPass";
        playerManager.createPlayer(username, password);

        // Because createPlayer() calls db.getNextPlayerId() internally,
        // the expected player ID is the last generated one.
        int expectedPlayerId = db.getNextPlayerId() - 1;
        Player player = db.getPlayer(expectedPlayerId);

        assertNotNull("Player should be created", player);
        assertEquals("Username should match", username, player.getUsername());
        assertEquals("Password should match", password, player.getPassword());
    }

    @Test
    public void testGetPlayerById() {
        playerManager.createPlayer("User1", "Pass1");
        int createdId = db.getNextPlayerId() - 1;
        Player player = playerManager.getPlayerById(createdId);
        assertNotNull("Player should be retrieved by ID", player);
        assertEquals("Username should match", "User1", player.getUsername());
    }

    @Test
    public void testGetPlayerByUsername() {
        playerManager.createPlayer("Alice", "secret");
        Player player = playerManager.getPlayerByUsername("Alice");
        assertNotNull("Player should be retrieved by username", player);
        assertEquals("Password should match", "secret", player.getPassword());
    }

    @Test
    public void testGetInventory() {
        playerManager.createPlayer("Bob", "pass");
        int createdId = db.getNextPlayerId() - 1;
        Inventory inv = (Inventory) playerManager.getInventory(createdId);
        assertNotNull("Inventory should not be null", inv);
        // New inventory should be empty.
        assertTrue("Inventory should be empty initially", inv.getIngredients().isEmpty());
    }

    // ----- Tests for Login and Registration -----
    @Test
    public void testLoginPlayerSuccess() {
        playerManager.createPlayer("TestLogin", "Password123");
        int newPlayerId = db.getNextPlayerId() - 1;
        Player created = playerManager.getPlayerById(newPlayerId);
        Player loggedIn = playerManager.loginPlayer("TestLogin", "Password123");
        assertNotNull("Player should be logged in with correct credentials", loggedIn);
        assertEquals("Username should match", "TestLogin", loggedIn.getUsername());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLoginPlayerInvalidUsername() {
        playerManager.loginPlayer("NonExistent", "Whatever");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLoginPlayerInvalidPassword() {
        playerManager.createPlayer("TestUser", "CorrectPass");
        playerManager.loginPlayer("TestUser", "WrongPass");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRegisterPlayerPasswordMismatch() {
        playerManager.registerPlayer("MismatchUser", "abc", "def");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRegisterPlayerUsernameAlreadyTaken() {
        playerManager.createPlayer("ExistingUser", "pass1");
        playerManager.registerPlayer("ExistingUser", "pass2", "pass2");
    }

    @Test
    public void testRegisterPlayerSuccess() {
        playerManager.registerPlayer("NewUser", "newpass", "newpass");
        Player player = playerManager.getPlayerByUsername("NewUser");
        assertNotNull("Player should be registered successfully", player);
        assertEquals("Username should match", "NewUser", player.getUsername());
    }

    // ----- Tests for Foraging -----
    @Test
    public void testForageSuccess() {
        // Ensure that there is at least one ingredient in the master list.
        if (db.getAllIngredients().isEmpty()) {
            db.addIngredient(ingWithShared1);
        }
        String ingredientName = playerManager.forage(testPlayer.getId());
        assertNotNull("Forage should return the name of a foraged ingredient", ingredientName);
        assertFalse("Foraged ingredient name should not be empty", ingredientName.isEmpty());
        // Check that the player's inventory contains that ingredient.
        Inventory inv = (Inventory) db.getPlayerInventory(testPlayer.getId());
        boolean found = false;
        for (Integer qty : inv.getIngredients().values()) {
            if (qty > 0) {
                found = true;
                break;
            }
        }
        assertTrue("Player's inventory should have at least one unit of a foraged ingredient", found);
    }

    // ----- Tests for Consuming Ingredients and Potions -----

    @Test
    public void testConsumeIngredientLearnsNewEffect() {
        // Give the player one ingredient with quantity 2.
        Inventory inv = (Inventory) db.getPlayerInventory(testPlayer.getId());
        inv.addIngredient(ingWithShared1, 2);

        // Ensure that knowledge for this ingredient is initially empty.
        IKnowledgeBook initialKb = playerManager.getKnowledgeBook(testPlayer.getId());
        assertFalse("Knowledge for the ingredient should be empty initially",
                initialKb.getKnowledge().containsKey(ingWithShared1.getId()));

        // Consume the ingredient.
        playerManager.consumeIngredient(testPlayer.getId(), ingWithShared1);

        // Verify that inventory quantity is decremented.
        assertEquals("Remaining quantity should be 1 after consumption",
                1, (int) inv.getIngredients().get(ingWithShared1));

        // Verify that a new effect was learned.
        IKnowledgeBook kbAfter = playerManager.getKnowledgeBook(testPlayer.getId());
        List<IEffect> learnedEffects = kbAfter.getKnowledge().get(ingWithShared1.getId());
        assertNotNull("Knowledge for the ingredient should not be null", learnedEffects);
        assertFalse("Knowledge for the ingredient should not be empty", learnedEffects.isEmpty());
    }

    @Test
    public void testConsumeIngredientAlreadyKnown() {
        Inventory inv = (Inventory) db.getPlayerInventory(testPlayer.getId());
        inv.addIngredient(ingWithShared1, 2);
        
        // Pre-populate the knowledge book with all effects for this ingredient.
        for (IEffect effect : ingWithShared1.getEffects()) {
            db.addKnowledgeEntry(testPlayer.getId(), ingWithShared1, effect);
        }
        
        // Consume the ingredient.
        playerManager.consumeIngredient(testPlayer.getId(), ingWithShared1);
        
        // Verify that the inventory is decremented.
        assertEquals("Remaining quantity should be 1 after consumption", 1, (int) inv.getIngredients().get(ingWithShared1));
        
        // Verify that the knowledge remains unchanged.
        List<IEffect> learnedEffects = playerManager.getKnowledgeBook(testPlayer.getId()).getKnowledge().get(ingWithShared1.getId());
        assertNotNull("Knowledge for the ingredient should not be null", learnedEffects);
        assertEquals("Knowledge list size should remain unchanged", ingWithShared1.getEffects().size(), learnedEffects.size());
    }

    @Test
    public void testConsumePotion() {
        // Create a test potion.
        Potion testPotion = new Potion(300, "Test Potion", Arrays.asList(sharedEffect), ingWithShared1, ingWithShared2, testPlayer.getId(), "test descrption", "8d100");
        
        // Add the potion to the player's inventory (assuming Inventory supports potions).
        Inventory inv = (Inventory) db.getPlayerInventory(testPlayer.getId());
        inv.addPotion(testPotion, 2);
        
        // Consume the potion.
        playerManager.consumePotion(testPlayer.getId(), testPotion);
        
        // Verify that the potion's quantity is decremented.
        Map<IPotion, Integer> potionMap = inv.getPotions();
        assertEquals("Potion quantity should be decremented to 1", 1, (int) potionMap.get(testPotion));
    }
}
