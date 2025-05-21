package object;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.Collections;

/**
 * Represents a player's knowledge book.
 * <p>
 * This class maintains a mapping between an ingredient's ID (the master list id)
 * and the list of effects that the player has learned about that ingredient.
 * </p>
 */
public class KnowledgeBook implements IKnowledgeBook {

    // The key is the ingredient ID; the value is a list of learned effects.
    private final Map<Integer, List<IEffect>> knowledge;

    /**
     * Constructs an empty KnowledgeBook.
     */
    public KnowledgeBook() {
        this.knowledge = new HashMap<>();
    }

    /**
     * Constructs a KnowledgeBook with the given map.
     * A deep copy (of the lists) is performed.
     *
     * @param knowledgeMap a map of ingredient IDs to lists of effects
     */
    public KnowledgeBook(Map<Integer, List<IEffect>> knowledgeMap) {
        this.knowledge = new HashMap<>();
        for (Map.Entry<Integer, List<IEffect>> entry : knowledgeMap.entrySet()) {
            // Create a new list from the existing effects
            this.knowledge.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }
    }

    /**
     * Adds an effect for a given ingredient to the knowledge book.
     * If the ingredient is not yet present, it will be initialized.
     * Duplicate effects for the same ingredient are prevented.
     *
     * @param ingredient the ingredient whose knowledge is being updated
     * @param effect the effect to add
     */
    public void addKnowledge(IIngredient ingredient, IEffect effect) {
        int ingredientId = ingredient.getId();
        // Initialize the list if not already present
        if (!knowledge.containsKey(ingredientId)) {
            knowledge.put(ingredientId, new ArrayList<>());
        }
        // Add the effect if it is not already stored
        if (!knowledge.get(ingredientId).contains(effect)) {
            knowledge.get(ingredientId).add(effect);
        }
    }

    /**
     * Checks whether a player already has learned a specific effect for an ingredient.
     *
     * @param ingredient the ingredient to check for
     * @param effect the effect to look for
     * @return true if the effect is already learned for the ingredient; false otherwise
     */
    public boolean hasKnowledge(IIngredient ingredient, IEffect effect) {
        int ingredientId = ingredient.getId();
        return knowledge.containsKey(ingredientId) && knowledge.get(ingredientId).contains(effect);
    }

    /**
     * Retrieves all learned effects for a given ingredient.
     *
     * @param ingredient the ingredient whose learned effects are needed
     * @return a list of effects. If no effects are learned yet, an empty list is returned.
     */
    public List<IEffect> getEffectsForIngredient(IIngredient ingredient) {
        return new ArrayList<>(knowledge.getOrDefault(ingredient.getId(), new ArrayList<>()));
    }

    /**
     * Provides an unmodifiable view of the entire knowledge map.
     *
     * @return an immutable map of ingredient IDs to lists of effects
     */
    public Map<Integer, List<IEffect>> getKnowledge() {
        Map<Integer, List<IEffect>> unmodifiableMap = new HashMap<>();
        for (Map.Entry<Integer, List<IEffect>> entry : knowledge.entrySet()) {
            unmodifiableMap.put(entry.getKey(), Collections.unmodifiableList(entry.getValue()));
        }
        return Collections.unmodifiableMap(unmodifiableMap);
    }


    @Override
    public String toString() {
        return "KnowledgeBook{" + "knowledge=" + knowledge + '}';
    }
// for testing
    @Override
    public Map<Integer, List<IEffect>> toMap() {
        // Return a deep copy of the knowledge map to ensure data encapsulation
        Map<Integer, List<IEffect>> copy = new HashMap<>();
        for (Map.Entry<Integer, List<IEffect>> entry : knowledge.entrySet()) {
            copy.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }
        return copy;
    }

}
