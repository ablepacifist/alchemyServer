package object;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Inventory implements IInventory , Serializable {
    private Map<IIngredient, Integer> ingredientQuantities;
    private Map<IPotion, Integer> potionQuantities;

    public Inventory() {
        this.ingredientQuantities = new HashMap<>();
        this.potionQuantities = new HashMap<>();
    }

    // Ingredient methods
    @Override
    public void addIngredient(IIngredient ingredient, int quantity) {
        ingredientQuantities.put(ingredient, ingredientQuantities.getOrDefault(ingredient, 0) + quantity);
    }

    @Override
    public void removeIngredient(IIngredient ingredient, int quantity) {
        int currentQuantity = ingredientQuantities.getOrDefault(ingredient, 0);
        if (currentQuantity > quantity) {
            ingredientQuantities.put(ingredient, currentQuantity - quantity);
        } else {
            ingredientQuantities.remove(ingredient);
        }
    }

    @Override
    public Map<IIngredient, Integer> getIngredients() {
        return new HashMap<>(ingredientQuantities);
    }

    @Override
    public boolean containsIngredient(IIngredient ingredient) {
        return ingredientQuantities.containsKey(ingredient);
    }

    // Potion methods
    @Override
    public void addPotion(IPotion potion, int quantity) {
        potionQuantities.put(potion, potionQuantities.getOrDefault(potion, 0) + quantity);
    }

    @Override
    public void removePotion(IPotion potion, int quantity) {
        int currentQuantity = potionQuantities.getOrDefault(potion, 0);
        if (currentQuantity > quantity) {
            potionQuantities.put(potion, currentQuantity - quantity);
        } else {
            potionQuantities.remove(potion);
        }
    }

    @Override
    public Map<IPotion, Integer> getPotions() {
        return new HashMap<>(potionQuantities);
    }

    @Override
    public boolean containsPotion(IPotion potion) {
        return potionQuantities.containsKey(potion);
    }


}
