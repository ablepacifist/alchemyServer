package test.integration;

import static org.junit.Assert.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import data.HSQLDatabase;
import data.IStubDatabase;
import logic.PlayerManager;
import logic.PotionManager;
import object.Effect;
import object.Ingredient;
import object.Inventory;
import object.IIngredient;
import object.IEffect;
import object.IKnowledgeBook;
import object.KnowledgeBook;
import object.Player;
import object.Potion;

/**
 * Comprehensive integration tests that use the real HSQLDB server.
 * These tests cover:
 *  - Creating and logging in a player
 *  - Adding ingredients to a player's inventory
 *  - Consuming an ingredient and updating the knowledge book
 *  - Brewing a potion from two ingredients that share an effect
 *  - Deleting a player and all associated player-specific data
 */
public class RealDatabaseIntegrationTest {

    private IStubDatabase db;
    private PlayerManager playerManager;
    private PotionManager potionManager;
    private int playerId;
    private Player testPlayer;

    @Before
    public void setUp() {
        // Connect to the real database; ensure your HSQLDB server is running on localhost.
        String jdbcUrl = "jdbc:hsqldb:hsql://localhost:9001/mydb";
        try {
            db = new HSQLDatabase(jdbcUrl);
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            fail("Failed to initialize HSQLDatabase: " + e.getMessage());
        }

        playerManager = new PlayerManager(db);
        potionManager = new PotionManager(db);
        
        // Create a test player with a known username prefix (so teardown can clean it up).
        String username = "integrationUserFlow";
        String password = "flowPass";
        playerManager.createPlayer(username, password);
        testPlayer = db.getPlayerByUsername(username);
        assertNotNull("Test player must be created", testPlayer);
        playerId = testPlayer.getId();
    }
    
    @After
    public void tearDown() {
        // Use the deletePlayer method to ensure all related records get removed.
        try {
            // Delete the test player (and associated inventory, knowledge, and potion records).
            db.deletePlayer(playerId);
        } catch (SQLException e) {
            e.printStackTrace();
            fail("Failed to delete test player: " + e.getMessage());
        }
    }

@Test
    public void testCreatePlayerIntegration() {
        // Create a player using a username that indicates an integration test.
        String username = "integrationUser";
        String password = "integrationPass";
        playerManager.createPlayer(username, password);

        // Retrieve the test player from the real database.
        Player player = db.getPlayerByUsername(username);
        assertNotNull("Player should be created in the real DB", player);
        assertEquals("Usernames should match", username, player.getUsername());
        assertEquals("Passwords should match", password, player.getPassword());
    }

    @Test
    public void testLoginPlayerIntegration() {
        String username = "integrationLogin";
        String password = "loginPass";
        playerManager.createPlayer(username, password);

        // Log in with the same credentials.
        Player loggedIn = playerManager.loginPlayer(username, password);
        assertNotNull("Login should succeed for a valid player", loggedIn);
        assertEquals("Usernames should match", username, loggedIn.getUsername());
    }

    @Test
    public void testDeletePlayerFlow() throws SQLException {
        // Create a separate test player to check deletion.
        String username = "integrationDeleteUser";
        String password = "deletePass";
        playerManager.createPlayer(username, password);
        Player tempPlayer = db.getPlayerByUsername(username);
        assertNotNull("Temp player should be created", tempPlayer);
        int tempPlayerId = tempPlayer.getId();
        
        // Add an ingredient to its inventory.
        IEffect effect = new Effect(4000, "Del Effect", "Will be deleted");
        IIngredient ing = new Ingredient(8000, "integrationDeleteIngredient", List.of(effect));
        db.addIngredient(ing);
        db.addIngredientToPlayerInventory(tempPlayerId, ing, 5);
        
        // Now delete the player.
        db.deletePlayer(tempPlayerId);
        
        // Verify that the player record is removed.
        Player deletedPlayer = db.getPlayerByUsername(username);
        assertNull("Player should be deleted", deletedPlayer);
    }





    @Test
public void testInventoryAddition() throws SQLException {
    // Use an existing seeded ingredient (assumed to be in the database with ID 66).
    IIngredient ingredient = db.getIngredientById(66);
    assertNotNull("Seeded ingredient with ID 66 should exist", ingredient);

    // Add 3 units to the player's inventory.
    db.addIngredientToPlayerInventory(playerId, ingredient, 3);

    Inventory inv = (Inventory) db.getPlayerInventory(playerId);
    Map<IIngredient, Integer> invMap = inv.getIngredients();
    assertTrue("Inventory should contain the added ingredient", invMap.containsKey(ingredient));
    assertEquals("Inventory quantity should be 3", Integer.valueOf(3), invMap.get(ingredient));
}

@Test
public void testConsumeIngredientFlow() throws SQLException {
    // Use an existing seeded ingredient (assumed to be in the database with ID 68).
    IIngredient ingredient = db.getIngredientById(68);
    assertNotNull("Seeded ingredient with ID 68 should exist", ingredient);

    // Add 2 units to the player's inventory.
    db.addIngredientToPlayerInventory(playerId, ingredient, 2);

    // Verify initial quantity.
    Inventory invBefore = (Inventory) db.getPlayerInventory(playerId);
    Integer qtyBefore = invBefore.getIngredients().get(ingredient);
    assertNotNull("Initial quantity should be present", qtyBefore);
    assertEquals("Initial quantity should be 2", Integer.valueOf(2), qtyBefore);

    // Consume one unit using business logic.
    playerManager.consumeIngredient(playerId, ingredient);

    // Check that quantity is decremented.
    Inventory invAfter = (Inventory) db.getPlayerInventory(playerId);
    Integer qtyAfter = invAfter.getIngredients().get(ingredient);
    assertNotNull("Quantity after consumption should be present", qtyAfter);
    assertEquals("Quantity should decrease by one", Integer.valueOf(1), qtyAfter);

    // Verify that the knowledge book was updated with at least one effect.
    IKnowledgeBook kb = db.getKnowledgeBook(playerId);
    assertTrue("KnowledgeBook should include an entry for the consumed ingredient",
               kb.getKnowledge().containsKey(ingredient.getId()));
    List<IEffect> learnedEffects = kb.getKnowledge().get(ingredient.getId());
    assertNotNull("Learned effects should not be null", learnedEffects);
    assertFalse("Learned effects should not be empty", learnedEffects.isEmpty());
}
@Test
    public void testBrewPotionFlow() throws SQLException {
        // Use two existing seeded ingredients that share an effect.
        // Based on your seed data:
        // - Ingredient 66 has effects: 27, 35, 22, 24.
        // - Ingredient 67 has effects: 14, 33, 27, 9.
        // So they share the effect with ID 27.
        IIngredient ing1 = db.getIngredientById(66);
        IIngredient ing2 = db.getIngredientById(67);
        assertNotNull("Seeded ingredient with ID 66 should exist", ing1);
        assertNotNull("Seeded ingredient with ID 67 should exist", ing2);
        
        // Add 2 units of each to player's inventory.
        db.addIngredientToPlayerInventory(playerId, ing1, 2);
        db.addIngredientToPlayerInventory(playerId, ing2, 2);
        
        // Attempt to brew a potion using these ingredients.
        Potion potion = potionManager.brewPotion(playerId, ing1, ing2);
        assertNotNull("Potion should be brewed successfully", potion);
        
        // Use our helper to retrieve the shared effect by ID 27.
        IEffect sharedEffect = getEffectById(27);
        assertNotNull("Shared effect (ID 27) should exist", sharedEffect);
        assertTrue("Potion name should indicate the shared effect",
                   potion.getName().contains(sharedEffect.getTitle()));
        
        // After brewing, inventory should have decreased by 1 unit for each ingredient.
        Inventory inv = (Inventory) db.getPlayerInventory(playerId);
        Map<IIngredient, Integer> invMap = inv.getIngredients();
        assertEquals("Remaining quantity for ing1 should be 1",
                     Integer.valueOf(1), invMap.get(ing1));
        assertEquals("Remaining quantity for ing2 should be 1",
                     Integer.valueOf(1), invMap.get(ing2));
    }
    
    // Helper method to retrieve an effect by its ID.
    private IEffect getEffectById(int effectId) throws SQLException {
        Connection conn = HSQLDatabase.getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(
                "SELECT id, title, description FROM PUBLIC.EFFECTS WHERE id = ?")) {
            pstmt.setInt(1, effectId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Effect(rs.getInt("id"), rs.getString("title"), rs.getString("description"));
                }
            }
        } finally {
            conn.close();
        }
        return null;
    }
}
