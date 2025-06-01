package alchemy.object;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class Ingredient implements IIngredient, Serializable {
    private int id;
    private String name;
    private List<IEffect> effects;

    public Ingredient(int id, String name, List<IEffect> effects) {
        this.id = id;
        this.name = name;
        this.effects = effects;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<IEffect> getEffects() {
        return effects;
    }

    @Override
    public void learnEffect(IEffect effect) {

    }

    @Override
    public boolean hasEffect(IEffect effect) {
        return false;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Ingredient)) return false;
        Ingredient other = (Ingredient) obj;
        return id == other.id; // Only compare IDs
    }

    @Override
    public int hashCode() {
        return Objects.hash(id); // Hash based on ID
    }

    @Override
    public String toString() {
        return name; // Return the name of the ingredient.
    }

}

