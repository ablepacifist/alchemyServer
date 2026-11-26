package alchemy.logic;

import alchemy.logic.HoldfastRules.Crop;
import alchemy.object.BuildingConfig;
import alchemy.object.Holdfast;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Builds the ready-to-show parts of a holdfast's status: crop progress, fallow fields and replant
 * cost, food warnings, whether each building is affordable, food-market figures, event tones. The
 * website and the Android app only display these values; neither works anything out itself.
 */
final class HoldfastStatusView {

    private HoldfastStatusView() {
    }

    static Map<String, Object> calendar(Holdfast h) {
        int day = h.getDaysElapsed();
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("day", day);
        out.put("week", day / 7 + 1);
        out.put("weeksElapsed", day / 7);
        out.put("dayOfWeek", day % 7);
        return out;
    }

    static Map<String, Object> food(Holdfast h, int daysOfFood, int nextSpoilIn, int shelfLife) {
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("amount", h.getFood());
        out.put("dailyNeed", HoldfastRules.dailyFoodNeed(h.getPopulation()));
        out.put("daysOfFood", daysOfFood);
        out.put("level", HoldfastRules.foodLevel(h.getFood(), h.getPopulation()));
        out.put("nextSpoilIn", nextSpoilIn);
        out.put("spoilLevel", HoldfastRules.spoilLevel(nextSpoilIn));
        out.put("shelfLife", shelfLife);
        out.put("rationCostPerDay", h.getPopulation() * HoldfastRules.RATION_COST_PER_PERSON);
        return out;
    }

    /** Every resource in display order, with the level to colour it by. */
    static List<Map<String, Object>> resources(Holdfast h) {
        List<Map<String, Object>> out = new ArrayList<>();
        for (String key : HoldfastRules.RESOURCES) {
            Map<String, Object> r = new LinkedHashMap<>();
            r.put("key", key);
            r.put("label", Character.toUpperCase(key.charAt(0)) + key.substring(1));
            if (key.equals("gold")) {
                r.put("amount", Math.round(h.getGold() * 10.0) / 10.0);
                r.put("level", h.getGold() <= 0 ? HoldfastRules.DANGER : HoldfastRules.OK);
            } else if (key.equals("food")) {
                r.put("amount", h.getFood());
                r.put("level", HoldfastRules.foodLevel(h.getFood(), h.getPopulation()));
            } else if (key.equals("grain")) {
                r.put("amount", h.getGrain());
                r.put("level", HoldfastRules.OK);
            } else if (key.equals("silver")) {
                r.put("amount", h.getSilver());
                r.put("level", HoldfastRules.OK);
            } else {
                r.put("amount", HoldfastRules.resourceAmount(h, key));
                r.put("level", HoldfastRules.OK);
            }
            out.add(r);
        }
        return out;
    }

    /** Buildings the holdfast owns at least one of. */
    static List<Map<String, Object>> builtBuildings(Holdfast h) {
        List<Map<String, Object>> out = new ArrayList<>();
        for (String type : BuildingConfig.BUILDING_TYPES.keySet()) {
            int count = h.getBuildingCount(type);
            if (count > 0) {
                Map<String, Object> b = new LinkedHashMap<>();
                b.put("type", type);
                b.put("name", BuildingConfig.getBuildingName(type));
                b.put("count", count);
                out.add(b);
            }
        }
        return out;
    }

    /** Planted crops with each field's progress toward harvest. */
    static List<Map<String, Object>> crops(Holdfast h) {
        List<Map<String, Object>> out = new ArrayList<>();
        for (Crop crop : HoldfastRules.CROPS) {
            List<Integer> plantDays = crop.plantDays().apply(h);
            if (plantDays.isEmpty()) {
                continue;
            }
            Map<String, Object> c = new LinkedHashMap<>();
            c.put("type", crop.type());
            c.put("label", crop.label());
            c.put("annual", crop.annual());
            c.put("planted", plantDays.size());
            c.put("showProgress", crop.showProgress());
            c.put("harvestDays", crop.showProgress() ? crop.harvestDays() : null);
            c.put("harvestFood", crop.harvestFood());
            c.put("harvestGold", crop.harvestGold());
            List<Map<String, Object>> fields = new ArrayList<>();
            if (crop.showProgress()) {
                for (int i = 0; i < plantDays.size(); i++) {
                    fields.add(field(i + 1, plantDays.get(i), h.getDaysElapsed(), crop.harvestDays()));
                }
            }
            c.put("fields", fields);
            out.add(c);
        }
        return out;
    }

    static Map<String, Object> field(int number, int plantDay, int today, int harvestDays) {
        int grown = Math.max(0, Math.min(harvestDays, today - plantDay));
        int percent = harvestDays <= 0 ? 100 : (int) Math.round(grown * 100.0 / harvestDays);
        Map<String, Object> f = new LinkedHashMap<>();
        f.put("field", number);
        f.put("plantDay", plantDay);
        f.put("grownDays", grown);
        f.put("harvestDays", harvestDays);
        f.put("percent", percent);
        f.put("ready", grown >= harvestDays);
        return f;
    }

    /** Annual fields that were harvested and wait to be replanted, with the seed cost. */
    static List<Map<String, Object>> fallowFields(Holdfast h) {
        List<Map<String, Object>> out = new ArrayList<>();
        for (Crop crop : HoldfastRules.CROPS) {
            int fallow = HoldfastRules.fallowCount(h, crop);
            if (fallow <= 0) {
                continue;
            }
            int cost = fallow * crop.seedCost();
            Map<String, Object> f = new LinkedHashMap<>();
            f.put("type", crop.type());
            f.put("label", crop.label());
            f.put("fallow", fallow);
            f.put("seedCost", crop.seedCost());
            f.put("replantCost", cost);
            f.put("canAfford", h.getGold() >= cost);
            out.add(f);
        }
        return out;
    }

    static Map<String, Object> foodMarket(Holdfast h) {
        int markets = h.getBuildingCount("food_market");
        int cap = HoldfastRules.foodMarketDailyCap(markets);
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("available", markets > 0);
        out.put("markets", markets);
        out.put("enabled", h.isFoodMarketEnabled());
        out.put("dailyCap", cap);
        out.put("dailyGold", Math.round(cap * HoldfastRules.FOOD_MARKET_PRICE * 10.0) / 10.0);
        out.put("pricePerFood", HoldfastRules.FOOD_MARKET_PRICE);
        out.put("reserve", HoldfastRules.foodMarketReserve(h.getPopulation()));
        out.put("reserveDays", HoldfastRules.FOOD_MARKET_RESERVE_DAYS);
        out.put("sellableToday", HoldfastRules.foodMarketSellable(h));
        return out;
    }

    /**
     * Adds affordability to one building-menu entry: each resource needed against what the
     * holdfast has, whether the gold covers it, and whether the Build button should be enabled.
     */
    static void addAffordability(Map<String, Object> item, Holdfast h, Map<String, Integer> resourceCost, int cost) {
        List<Map<String, Object>> requirements = new ArrayList<>();
        boolean resourcesOk = true;
        if (resourceCost != null) {
            for (Map.Entry<String, Integer> e : resourceCost.entrySet()) {
                int have = HoldfastRules.resourceAmount(h, e.getKey());
                boolean enough = have >= e.getValue();
                resourcesOk &= enough;
                Map<String, Object> r = new LinkedHashMap<>();
                r.put("resource", e.getKey());
                r.put("needed", e.getValue());
                r.put("have", have);
                r.put("enough", enough);
                requirements.add(r);
            }
        }
        boolean goldOk = h.getGold() >= cost;
        item.put("resourceRequirements", requirements);
        item.put("canAffordResources", resourcesOk);
        item.put("canAffordGold", goldOk);
        item.put("canBuild", "available".equals(item.get("status")) && resourcesOk && goldOk);
    }

    /** An event with the tone the clients colour it by. */
    static Map<String, Object> event(String message) {
        Map<String, Object> e = new LinkedHashMap<>();
        e.put("message", message);
        e.put("tone", HoldfastRules.eventTone(message));
        return e;
    }
}
