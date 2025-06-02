package test;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import alchemy.data.IStubDatabase;
import alchemy.logic.PotionManager;
import alchemy.object.Effect;
import alchemy.object.Ingredient;
import alchemy.object.Inventory;
import alchemy.object.IIngredient;
import alchemy.object.IEffect;
import alchemy.object.KnowledgeBook;
import alchemy.object.Player;
import alchemy.object.Potion;
import alchemy.object.IKnowledgeBook;

public class PotionManagerTest {

    private IStubDatabase db;
    private PotionManager potionManager;
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
        // Create a fresh dummy database instance.
        db = new DummyDatabase();
        potionManager = new PotionManager(db);

        // Create sample effects.
        sharedEffect = new Effect(100, "Spark", "Shared spark effect.");
        uniqueEffect1 = new Effect(101, "Glow", "Unique glow effect.");
        uniqueEffect2 = new Effect(102, "Shine", "Unique shine effect.");

        // Add effects to the database.
        db.addEffect(sharedEffect);
        db.addEffect(uniqueEffect1);
        db.addEffect(uniqueEffect2);

        // Create ingredients.
        // Two ingredients that share the "Spark" effect plus an extra unique effect each.
        ingWithShared1 = new Ingredient(200, "HerbA", Arrays.asList(sharedEffect, uniqueEffect1));
        ingWithShared2 = new Ingredient(201, "HerbB", Arrays.asList(sharedEffect, uniqueEffect2));
        // An ingredient that does not share any effect with ingWithShared1.
        ingNoShared = new Ingredient(202, "HerbC", Arrays.asList(uniqueEffect2));

        // Add ingredients to the database.
        db.addIngredient(ingWithShared1);
        db.addIngredient(ingWithShared2);
        db.addIngredient(ingNoShared);

        // Create and add a test player with a new, empty Inventory and an empty KnowledgeBook.
        playerId = db.getNextPlayerId();
        IKnowledgeBook kb = new KnowledgeBook(new HashMap<>());
        testPlayer = new Player(playerId, "potionTester", "testpass", new Inventory(), kb);
        db.addPlayer(testPlayer);
    }

    @Test
    public void testBrewPotionSuccess() {
        // Add the two ingredients (that share 'Spark') to the player's inventory.
        Inventory inv = (Inventory) db.getPlayerInventory(playerId);
        inv.addIngredient(ingWithShared1, 2);
        inv.addIngredient(ingWithShared2, 2);

        // Attempt to brew a potion.
        Potion potion = potionManager.brewPotion(playerId, ingWithShared1, ingWithShared2);
        assertNotNull("Potion should be brewed when ingredients share an effect", potion);
        // Expect the potion's name to include "Spark".
        assertTrue("Potion's name should contain the shared effect 'Spark'", potion.getName().contains("Spark"));

        // Verify that each ingredient's quantity is decremented by one.
        Map<IIngredient, Integer> ingMap = inv.getIngredients();
        assertEquals("Remaining quantity of ingWithShared1 should be 1", 1, (int) ingMap.get(ingWithShared1));
        assertEquals("Remaining quantity of ingWithShared2 should be 1", 1, (int) ingMap.get(ingWithShared2));
    }

    @Test
    public void testBrewPotionMissingIngredient() {
        // Only add one of the required ingredients.
        Inventory inv = (Inventory) db.getPlayerInventory(playerId);
        inv.addIngredient(ingWithShared1, 2);
        // Not adding ingWithShared2.
        Potion potion = potionManager.brewPotion(playerId, ingWithShared1, ingWithShared2);
        assertNull("Potion should not be brewed if one required ingredient is missing", potion);
    }

    @Test
    public void testBrewPotionNoSharedEffects() {
        // Add two ingredients that do not share any common effect.
        Inventory inv = (Inventory) db.getPlayerInventory(playerId);
        inv.addIngredient(ingWithShared1, 2);
        inv.addIngredient(ingNoShared, 2);

        Potion potion = potionManager.brewPotion(playerId, ingWithShared1, ingNoShared);
        assertNull("Potion should not be brewed if ingredients do not share any effect", potion);

        // Even when brewing fails, one unit from each ingredient is removed.
        assertEquals("Remaining quantity of ingWithShared1 should be 1", 1, (int) inv.getIngredients().get(ingWithShared1));
        assertEquals("Remaining quantity of ingNoShared should be 1", 1, (int) inv.getIngredients().get(ingNoShared));
    }

    @Test
    public void testBrewPotionSameIngredient() {
        // Add a single ingredient entry.
        Inventory inv = (Inventory) db.getPlayerInventory(playerId);
        inv.addIngredient(ingWithShared1, 2);

        // Attempt to brew a potion using the same ingredient twice.
        Potion potion = potionManager.brewPotion(playerId, ingWithShared1, ingWithShared1);
        assertNull("Potion should not be brewed when both ingredients are the same", potion);
    }
}
