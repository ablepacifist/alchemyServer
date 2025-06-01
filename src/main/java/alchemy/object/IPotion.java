package alchemy.object;
import java.util.List;

public interface IPotion {
    int getId();

    /**
     * Gets the name of the potion.
     * @return the potion name
     */
    String getName();

    /**
     * Gets the array of effects of the potion.
     *
     * @return an array of effects
     */
    List<IEffect> getEffects();

    /**
     * Gets the first ingredient used in the potion.
     * @return the first ingredient
     */
    IIngredient getIngredient1();

    String getDice();

    void setDice(String dice);

    /**
     * Gets the second ingredient used in the potion.
     * @return the second ingredient
     */
    IIngredient getIngredient2();

    String getDescription();
    double getDuration();

    void setBrewLevel(int level);

    int getBrewLevel();
}
