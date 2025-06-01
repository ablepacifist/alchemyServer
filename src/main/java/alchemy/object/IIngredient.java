package alchemy.object;
import java.util.List;

public interface IIngredient {
    int getId();

    /**
     * Gets the name of the ingredient.
     *
     * @return the ingredient name
     */
    String getName();

    /**
     * Gets the array of effects associated with the ingredient.
     * Some effects may be null if not yet discovered.
     *
     * @return an array of up to four effects
     */
    List<IEffect> getEffects();

    void learnEffect(IEffect effect);

    boolean hasEffect(IEffect effect);
}

