package alchemy.object;

import java.io.Serializable;
import java.util.List;

public class Potion implements IPotion , Serializable {
    private int id;
    private String name;
    private List<IEffect> effects;
    private IIngredient ingredient1;
    private IIngredient ingredient2;
    private double duration;      // Duration in minutes.
    private final String description;
    private int brewLevel;
    private String dice;

    public Potion(int id, String name, List<IEffect> effects, IIngredient ingredient1, IIngredient ingredient2, double duration, String description, String dice) {
        this.id = id;
        this.name = name;
        this.effects = effects;
        this.ingredient1 = ingredient1;
        this.ingredient2 = ingredient2;
        this.description = description;
        this.duration = duration;
        this.dice = dice;

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
    public IIngredient getIngredient1() {
        return ingredient1;
    }
    @Override
    public String getDice() {
        return dice;
    }
    @Override
    public void setDice(String dice) {
        this.dice = dice;
    }

    @Override
    public IIngredient getIngredient2() {
        return ingredient2;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public double getDuration() {
        return duration;
    }
@Override
public void setBrewLevel(int level) {
        this.brewLevel = level;
}
@Override
public int getBrewLevel() {
        return brewLevel;
    }

    @Override
    public String toString() {
        StringBuilder effectsBuilder = new StringBuilder();
        for (IEffect effect : effects) {
            effectsBuilder.append(effect.getTitle()).append(", ");
        }
        if (effectsBuilder.length() > 0) {
            effectsBuilder.setLength(effectsBuilder.length() - 2);
        }

        return "Potion{" +
                "name='" + name + '\'' + "\n" +
                "brewLevel=" + brewLevel + "\n" +
                "effects=[" + effectsBuilder.toString() + "]" + "\n" +
                "ingredient1=" + (ingredient1 != null ? ingredient1.getName() : "null") + "\n" +
                "ingredient2=" + (ingredient2 != null ? ingredient2.getName() : "null") + "\n" +
                "duration=" + duration + " minutes" + "\n" +
                (dice.isEmpty() ? "" : "bonusDice=" + dice + "\n") +
                "description='" + description + '\'' + "\n" +
                '}';
    }

    public String getBonusDice() {
        return this.dice;
    }
}
