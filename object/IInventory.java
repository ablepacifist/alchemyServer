package object;
import java.util.Map;

public interface IInventory {
    // Ingredient methods
    void addIngredient(IIngredient ingredient, int quantity);
    void removeIngredient(IIngredient ingredient, int quantity);
    Map<IIngredient, Integer> getIngredients();
    boolean containsIngredient(IIngredient ingredient);

    // Potion methods
    void addPotion(IPotion potion, int quantity);

    Map<IPotion, Integer> getPotions();

    void removePotion(IPotion potion, int i);

    boolean containsPotion(IPotion potion);
}


