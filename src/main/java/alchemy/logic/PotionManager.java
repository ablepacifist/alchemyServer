package alchemy.logic;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import alchemy.data.IStubDatabase;
import alchemy.object.IIngredient;
import alchemy.object.IEffect;
import alchemy.object.IInventory;
import alchemy.object.Inventory;
import alchemy.object.Player;
import alchemy.object.Potion;

import org.springframework.stereotype.Service;

@Service
public class PotionManager {
    private final IStubDatabase db;

    public PotionManager(IStubDatabase db) {
        this.db = db;
    }

    /**
     * Brew a potion by combining two ingredients from a player's inventory.
     * The potion is created if the two ingredients share at least one effect.
     */
    public Potion brewPotion(int playerId, IIngredient ingredient1, IIngredient ingredient2) {
        // Check that the two ingredients are not the same.
        if (ingredient1.getId() == ingredient2.getId()) {
            System.out.println("Cannot brew a potion with the same ingredient twice.");
            return null;
        }

        // Retrieve the player's inventory.
        IInventory inventory = db.getPlayerInventory(playerId);
        Map<IIngredient, Integer> ingredientsMap = inventory.getIngredients();

        // Determine available quantities by comparing IDs.
        int quantity1 = 0;
        int quantity2 = 0;
        for (Map.Entry<IIngredient, Integer> entry : ingredientsMap.entrySet()) {
            IIngredient ing = entry.getKey();
            if (ing.getId() == ingredient1.getId()) {
                quantity1 = entry.getValue();
            }
            if (ing.getId() == ingredient2.getId()) {
                quantity2 = entry.getValue();
            }
        }

        // Check that both ingredients exist with at least one unit available.
        if (quantity1 < 1 || quantity2 < 1) {
            System.out.println("Missing one or more ingredients. No potion brewed.");
            return null;
        }

        // Retrieve the effects for each ingredient.
        List<IEffect> effects1 = db.getEffectsForIngredient(ingredient1.getId());
        List<IEffect> effects2 = db.getEffectsForIngredient(ingredient2.getId());

        // Determine the shared effects.
        List<IEffect> sharedEffects = new ArrayList<>();
        for (IEffect e1 : effects1) {
            for (IEffect e2 : effects2) {
                if (e1.getId() == e2.getId()) {
                    sharedEffects.add(e1);
                    break;
                }
            }
        }

        // Remove one unit of each ingredient.
        db.removeIngredientFromPlayerInventory(playerId, ingredient1, 1);
        db.removeIngredientFromPlayerInventory(playerId, ingredient2, 1);

        if (sharedEffects.isEmpty()) {
            System.out.println("No shared effects. No potion brewed.");
            return null;
        } else {
            // Retrieve the player's level.
            Player player = db.getPlayer(playerId);
            int playerLevel = player.getLevel();

            // Determine the potion's duration and bonus dice based on player level.
            double durationInMinutes;
            String durationReadable;
            String bonusDice;
            if (playerLevel <= 2) {
                durationInMinutes = 0.5;              // 30 seconds = 0.5 minutes
                durationReadable = "30 seconds";
                bonusDice = "1d4 or 2";
            } else if (playerLevel <= 4) {
                durationInMinutes = 0.75;             // 45 seconds = 0.75 minutes
                durationReadable = "45 seconds";
                bonusDice = "1d6 or 4";
            } else if (playerLevel <= 7) {
                durationInMinutes = 1.0;              // 1 minute
                durationReadable = "1 minute";
                bonusDice = "1d8 or 5";
            } else if (playerLevel <= 9) {
                durationInMinutes = 1.25;             // 1 minute 15 seconds = 1.25 minutes
                durationReadable = "1 minute 15 seconds";
                bonusDice = "1d10 or 6";
            } else { // Level 10 and above
                durationInMinutes = 2.0;              // 2 minutes
                durationReadable = "2 minutes";
                bonusDice = "1d12 or 9";
            }
            // Generate potion name and enhanced description.
            String effectNames = generatePotionName(sharedEffects);
            String potionName = "Potion of " + effectNames;
            String description = "Brewed by a level " + playerLevel + " alchemist using "
                    + ingredient1.getName() + " and " + ingredient2.getName() + ". It harnesses the effects of "
                    + effectNames + " and lasts for " + durationReadable + " minutes.";

            // Create the potion with the new details.
            int potionId = db.getNextPotionId();
            Potion potion = new Potion(potionId, potionName, sharedEffects, ingredient1, ingredient2, durationInMinutes, description, bonusDice);
            potion.setBrewLevel(playerLevel);
            // Insert the potion into the POTIONS table.
            db.addPotion(potion);
            // Add the potion to the player's inventory.
            db.addPotionToPlayerInventory(playerId, potion, 1);
            // Update the player's knowledge book for both ingredients.
            for (IEffect effect : sharedEffects) {
                db.addKnowledgeEntry(playerId, ingredient1, effect);
                db.addKnowledgeEntry(playerId, ingredient2, effect);
            }
            System.out.println("Brewed potion: " + potionName + " for player " + playerId);
            System.out.println("Potion Description: " + description);
            return potion;
        }
    }

    /**
     * Helper method to generate a potion name from shared effects.
     */
    private String generatePotionName(List<IEffect> sharedEffects) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < sharedEffects.size(); i++) {
            sb.append(sharedEffects.get(i).getTitle());
            if (i < sharedEffects.size() - 1) {
                sb.append(" & ");
            }
        }
        return sb.toString();
    }

    /**
     * Forage: Randomly selects an ingredient from the master list and adds one unit
     * of it to the player's inventory.
     *
     * @param playerId the ID of the player for whom to forage
     * @return the name of the foraged ingredient, or an empty string if none available.
     */
    public String forage(int playerId) {
        List<IIngredient> masterIngredients = db.getAllIngredients();
        if (masterIngredients.isEmpty()) {
            System.out.println("No ingredients available to forage.");
            return "";
        }
        Random rand = new Random();
        IIngredient randomIngredient = masterIngredients.get(rand.nextInt(masterIngredients.size()));
        db.addIngredientToPlayerInventory(playerId, randomIngredient, 1);
        System.out.println("Foraged " + randomIngredient.getName() + " for player " + playerId);
        return randomIngredient.getName();
    }
}
