package alchemy.logic;

import alchemy.object.BuildingConfig;
import alchemy.object.Holdfast;

import java.util.List;
import java.util.Locale;
import java.util.function.Function;

/**
 * The Holdfast game rules that both the day-by-day simulation ({@link HoldfastManager}) and the
 * status view ({@link HoldfastStatusView}) need. Keeping them in one place means the numbers a
 * player is shown are the numbers the simulation actually uses; no client keeps its own copy.
 */
final class HoldfastRules {

    private HoldfastRules() {
    }

    /** Food each person eats per day. */
    static final double FOOD_PER_PERSON = 0.2;
    /** Gold per person per day for emergency rations when the food runs out. */
    static final double RATION_COST_PER_PERSON = 2.0;
    static final int BASE_FOOD_SHELF_LIFE = 30;
    static final int SHELF_LIFE_PER_GRANARY = 15;
    /** Below this many days of food the settlement is warned (and the event log says "low!"). */
    static final int LOW_FOOD_DAYS = 14;
    /** The food market keeps this many days of food back and never sells it. */
    static final int FOOD_MARKET_RESERVE_DAYS = 14;
    static final int FOOD_MARKET_SALES_PER_MARKET = 5;
    static final double FOOD_MARKET_PRICE = 0.8;
    /** Each carpenter takes this fraction off every build cost. */
    static final double CARPENTER_DISCOUNT = 0.1;

    /** Display order of the resource list. */
    static final List<String> RESOURCES = List.of("gold", "food", "wood", "stone", "iron", "beer", "wine", "tools", "grain", "silver");

    /**
     * One kind of field. The simulation harvests them in this order.
     *
     * @param seedCost     gold per field to replant; {@code null} for perennials, which replant themselves
     * @param showProgress false for vineyards: their 90-day "harvest" yields nothing (the wine comes
     *                     from weekly production), so a progress bar would count down to nothing
     */
    record Crop(String type, String label, int harvestDays, int harvestFood, int harvestGold,
                Integer seedCost, boolean showProgress, Function<Holdfast, List<Integer>> plantDays) {
        boolean annual() {
            return seedCost != null;
        }
    }

    static final List<Crop> CROPS = List.of(
            new Crop("wheat_field", "Wheat Fields", 14, 20, 0, 10, true, Holdfast::getWheatFieldPlantDays),
            new Crop("rye_field", "Rye Fields", 28, 40, 0, 12, true, Holdfast::getRyeFieldPlantDays),
            new Crop("vegetable_garden", "Vegetable Gardens", 10, 10, 0, 8, true, Holdfast::getVegetableGardenPlantDays),
            new Crop("orchard", "Orchards", 30, 8, 40, null, true, Holdfast::getOrchardPlantDays),
            new Crop("vineyard", "Vineyards", 90, 0, 0, null, false, Holdfast::getVineyardPlantDays),
            new Crop("berry_patch", "Berry Patches", 7, 8, 0, null, true, Holdfast::getBerryPatchPlantDays),
            new Crop("mushroom_cave", "Mushroom Caves", 21, 25, 0, null, true, Holdfast::getMushroomCavePlantDays)
    );

    /** The crop for a building type, or {@code null} if that building is not a field. */
    static Crop crop(String type) {
        for (Crop c : CROPS) {
            if (c.type().equals(type)) {
                return c;
            }
        }
        return null;
    }

    static int dailyFoodNeed(int population) {
        return (int) Math.ceil(population * FOOD_PER_PERSON);
    }

    static int foodShelfLife(Holdfast h) {
        return BASE_FOOD_SHELF_LIFE + h.getBuildingCount("granary") * SHELF_LIFE_PER_GRANARY;
    }

    /** Food the market keeps back. Uses the raw per-person rate, as the market always has. */
    static int foodMarketReserve(int population) {
        return (int) (population * FOOD_PER_PERSON * FOOD_MARKET_RESERVE_DAYS);
    }

    static int foodMarketDailyCap(int markets) {
        return markets * FOOD_MARKET_SALES_PER_MARKET;
    }

    /** Food the market would sell today. */
    static int foodMarketSellable(Holdfast h) {
        int canSell = Math.max(0, h.getFood() - foodMarketReserve(h.getPopulation()));
        return Math.min(canSell, foodMarketDailyCap(h.getBuildingCount("food_market")));
    }

    /** Built fields of this crop that are not planted. Only annuals can lie fallow. */
    static int fallowCount(Holdfast h, Crop crop) {
        if (!crop.annual()) {
            return 0;
        }
        return Math.max(0, h.getBuildingCount(crop.type()) - crop.plantDays().apply(h).size());
    }

    /** Gold cost of the next building of this type, after the carpenter discount. */
    static int buildCost(Holdfast h, String type) {
        int baseCost = BuildingConfig.getCost(type, h.getBuildingCount(type));
        return (int) (baseCost * (1 - h.getBuildingCount("carpenter") * CARPENTER_DISCOUNT));
    }

    /** How much of a build-cost resource the holdfast has. Unknown resources count as none. */
    static int resourceAmount(Holdfast h, String resource) {
        switch (resource) {
            case "wood":  return h.getWood();
            case "stone": return h.getStone();
            case "iron":  return h.getIron();
            case "food":  return h.getFood();
            case "beer":  return h.getBeer();
            case "wine":  return h.getWine();
            case "tools": return h.getTools();
            default:      return 0;
        }
    }

    // ---- levels the clients colour by: "ok", "warn" or "danger" ----

    static final String OK = "ok";
    static final String WARN = "warn";
    static final String DANGER = "danger";

    static String foodLevel(int food, int population) {
        if (food <= 0) {
            return DANGER;
        }
        return food < dailyFoodNeed(population) * LOW_FOOD_DAYS ? WARN : OK;
    }

    static String spoilLevel(int nextSpoilIn) {
        if (nextSpoilIn < 0) {
            return OK;
        }
        if (nextSpoilIn <= 3) {
            return DANGER;
        }
        return nextSpoilIn <= 7 ? WARN : OK;
    }

    static String happinessLevel(double happiness) {
        if (happiness < 25) {
            return DANGER;
        }
        return happiness < 50 ? WARN : OK;
    }

    /** "rising", "falling", "steady", or {@code null} when there is not enough history yet. */
    static String populationTrend(double avgDailyGrowth, int historySize) {
        if (historySize < 2) {
            return null;
        }
        if (avgDailyGrowth > 0.05) {
            return "rising";
        }
        return avgDailyGrowth < -0.05 ? "falling" : "steady";
    }

    /** How an event line should be shown: "danger", "warn", "success" or "normal". */
    static String eventTone(String message) {
        String lower = message == null ? "" : message.toLowerCase(Locale.ROOT);
        if (lower.contains("raid") || lower.contains("famine") || lower.contains("destroyed")
                || lower.contains("casualties")) {
            return DANGER;
        }
        if (lower.contains("no food") || lower.contains("spoiled") || lower.contains("low!")) {
            return WARN;
        }
        if (lower.contains("harvested") || lower.contains("produced") || lower.contains("grew")
                || lower.contains("sold")) {
            return "success";
        }
        return "normal";
    }

    static String castleName(String castleType) {
        if (castleType == null || castleType.isEmpty()) {
            return "";
        }
        StringBuilder out = new StringBuilder();
        for (String word : castleType.split("_")) {
            if (word.isEmpty()) {
                continue;
            }
            if (out.length() > 0) {
                out.append(' ');
            }
            out.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1));
        }
        return out.toString();
    }
}
