package test;

import static org.junit.Assert.*;

import java.util.Arrays;
import org.junit.Before;
import org.junit.Test;
import java.util.Map;

import alchemy.object.Effect;
import alchemy.alchemy.object.Ingredient;
import alchemy.object.Inventory;
import alchemy.object.IPotion;
import alchemy.object.Potion;

public class InventoryTest {

    private Inventory inventory;

    private object.IIngredient ingredient1;
    private object.IIngredient ingredient2;
    private IPotion potion1;

    @Before
    public void setUp() {
        inventory = new Inventory();

        // Create sample effects.
        Effect effectHealing = new Effect(1, "Healing", "");
        Effect effectPoison = new Effect(2, "Poison", "");

        // Create ingredients with one effect each.
        ingredient1 = new Ingredient(1, "Red Herb", Arrays.asList(effectHealing));
        ingredient2 = new Ingredient(2, "Blue Mushroom", Arrays.asList(effectPoison));

        // Create a potion that uses effectHealing from ingredient1 and ingredient2.
        potion1 = new Potion(1, "Healing Potion", Arrays.asList(effectHealing), ingredient1, ingredient2, 1, "test description", "1D100");
    }

    @Test
    public void testAddIngredient() {
        inventory.addIngredient(ingredient1, 3);
        assertTrue("Inventory should contain the ingredient after adding", inventory.containsIngredient(ingredient1));
        assertEquals("Quantity should be 3", Integer.valueOf(3), inventory.getIngredients().get(ingredient1));
    }

    @Test
    public void testRemoveIngredient() {
        inventory.addIngredient(ingredient1, 3);
        inventory.removeIngredient(ingredient1, 2);
        assertTrue("Ingredient should still be in inventory", inventory.containsIngredient(ingredient1));
        assertEquals("Remaining quantity should be 1", Integer.valueOf(1), inventory.getIngredients().get(ingredient1));

        // Remove the last unit.
        inventory.removeIngredient(ingredient1, 1);
        assertFalse("Ingredient should no longer be in inventory", inventory.containsIngredient(ingredient1));
    }

    @Test
    public void testRemoveIngredient_NotInInventory() {
        // Attempt to remove an ingredient not in inventory.
        inventory.removeIngredient(ingredient1, 1);
        assertFalse("Inventory should not contain the ingredient", inventory.containsIngredient(ingredient1));
    }

    @Test
    public void testRemoveIngredient_ExceedingQuantity() {
        inventory.addIngredient(ingredient1, 2);
        // Removing more than available should remove the ingredient entirely.
        inventory.removeIngredient(ingredient1, 5);
        assertFalse("Inventory should not contain the ingredient after over-removal", inventory.containsIngredient(ingredient1));
    }

    @Test
    public void testAddPotion() {
        inventory.addPotion(potion1, 2);
        assertTrue("Inventory should contain the potion", inventory.containsPotion(potion1));
        assertEquals("Potion quantity should be 2", Integer.valueOf(2), inventory.getPotions().get(potion1));
    }

    @Test
    public void testRemovePotion() {
        inventory.addPotion(potion1, 2);
        inventory.removePotion(potion1, 1);
        assertTrue("Potion should still be present after removal", inventory.containsPotion(potion1));
        assertEquals("Remaining potion quantity should be 1", Integer.valueOf(1), inventory.getPotions().get(potion1));

        // Remove the final unit.
        inventory.removePotion(potion1, 1);
        assertFalse("Potion should no longer be in inventory", inventory.containsPotion(potion1));
    }

    @Test
    public void testRemovePotion_NotInInventory() {
        // Attempt to remove a potion that is not in the inventory.
        inventory.removePotion(potion1, 1);
        assertFalse("Inventory should not contain the potion", inventory.containsPotion(potion1));
    }

    @Test
    public void testRemovePotion_ExceedingQuantity() {
        inventory.addPotion(potion1, 1);
        inventory.removePotion(potion1, 3);
        assertFalse("Inventory should not contain the potion after excessive removal", inventory.containsPotion(potion1));
    }
}
