package object;

import object.IIngredient;
import object.IEffect;

import java.util.List;
import java.util.Map;

import java.util.List;
import java.util.Map;

/**
 * Interface for the KnowledgeBook functionality.
 * Provides methods for managing and querying a player's learned knowledge of ingredients and their effects.
 */
public interface IKnowledgeBook {

    /**
     * Adds a learned effect for a given ingredient to the knowledge book.
     *
     * @param ingredient the ingredient whose knowledge is being updated
     * @param effect the effect to add
     */
    void addKnowledge(IIngredient ingredient, IEffect effect);

    /**
     * Checks whether a specific effect has been learned for an ingredient.
     *
     * @param ingredient the ingredient to check for
     * @param effect the effect to look for
     * @return true if the effect is already learned for the ingredient; false otherwise
     */
    boolean hasKnowledge(IIngredient ingredient, IEffect effect);

    /**
     * Retrieves all learned effects for a specific ingredient.
     *
     * @param ingredient the ingredient whose learned effects are needed
     * @return a list of effects. If no effects are learned yet, an empty list is returned.
     */
    List<IEffect> getEffectsForIngredient(IIngredient ingredient);

    /**
     * Retrieves the entire knowledge map as an unmodifiable map.
     *
     * @return an immutable map of ingredient IDs to lists of effects
     */
    Map<Integer, List<IEffect>> getKnowledge();
    Map<Integer, List<IEffect>> toMap();

}
