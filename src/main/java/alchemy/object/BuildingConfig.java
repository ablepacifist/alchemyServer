package alchemy.object;

import java.util.*;

public class BuildingConfig {

    public static final Map<String, Map<String, Object>> BUILDING_TYPES = new LinkedHashMap<>();

    static {
        // TIER 1: Early Game (0 pop) — no resource costs, these ARE the resource sources
        BUILDING_TYPES.put("alchemy_garden", b("Alchemy Garden", 80, 20, 2, 8, 8, 1, 0,
                "Produces alchemy supplies and modest silver"));
        // MATERIAL PRODUCER: mine produces 2 stone + 1 iron every 7 days (no daily silver)
        BUILDING_TYPES.put("mine", b("Mine", 120, 30, 2, 0, 12, -5, 0,
                "Produces 2 stone + 1 iron every 7 days",
                "productionDays", 7));
        // MATERIAL PRODUCER: logging_camp produces 3 wood every 7 days
        BUILDING_TYPES.put("logging_camp", b("Logging Camp", 70, 25, 3, 0, 8, -1, 0,
                "Chops timber, produces 3 wood every 7 days",
                "productionDays", 7, "productionAmount", 3, "productionItem", "wood"));
        BUILDING_TYPES.put("tavern", b("Tavern", 60, 30, 3, 10, 10, 5, 0,
                "Produces 3 beer every 7 days, increases happiness",
                "productionDays", 7, "productionAmount", 3, "productionItem", "beer"));
        // Resource cost: guard_tower needs wood to frame
        BUILDING_TYPES.put("guard_tower", b("Guard Tower", 25, 15, null, 0, 17, 0, 0,
                "Increases protection (+10), requires daily wages",
                "protectionBonus", 10,
                "resourceCost", Map.of("wood", 4)));
        // MATERIAL PRODUCER: wheat_field harvests food every 14 days (auto-replants)
        BUILDING_TYPES.put("wheat_field", b("Wheat Field", 50, 15, null, 0, 12, 0, 0,
                "Produces 20 food every 14 days, auto-replants after harvest",
                "harvestDays", 14, "harvestFood", 20));

        // TIER 2: Growing Settlement (30-60 pop)
        // MATERIAL PRODUCER: vegetable_garden harvests food every 10 days
        BUILDING_TYPES.put("vegetable_garden", b("Vegetable Garden", 45, 15, 3, 0, 8, 1, 30,
                "Produces 10 food every 10 days, auto-replants",
                "harvestDays", 10, "harvestFood", 10));
        // MATERIAL PRODUCER: orchard harvests food + small gold every 30 days
        BUILDING_TYPES.put("orchard", b("Orchard", 150, 40, 2, 0, 10, 2, 50,
                "Produces 8 food + 40g every 30 days",
                "harvestDays", 30, "harvestFood", 8, "harvestGold", 40));
        // MATERIAL PRODUCER: vineyard produces wine every 7 days
        BUILDING_TYPES.put("vineyard", b("Vineyard", 180, 50, 2, 0, 12, 4, 60,
                "Produces 2 wine every 7 days, increases happiness",
                "productionDays", 7, "productionAmount", 2, "productionItem", "wine"));
        // MATERIAL PRODUCER: blacksmith produces tools every 14 days
        BUILDING_TYPES.put("blacksmith", b("Blacksmith", 140, 50, 2, 15, 14, 1, 35,
                "Produces 2 tools every 14 days, provides defense bonus",
                "productionDays", 14, "productionAmount", 2, "productionItem", "tools",
                "protectionBonus", 5));
        BUILDING_TYPES.put("carpenter", b("Carpenter Shop", 110, 40, 2, 12, 12, 0, 30,
                "Reduces all building costs by 10%",
                "buildingDiscount", 0.1));

        // TIER 3: Established Town (40-80 pop) — most require stone/wood
        BUILDING_TYPES.put("chapel", b("Chapel", 250, 0, 1, 0, 21, 15, 40,
                "Provides spiritual guidance, major happiness boost",
                "resourceCost", Map.of("stone", 5)));
        BUILDING_TYPES.put("market", b("Market", 320, 150, 2, 20, 18, 8, 60,
                "Trading hub, +10% all gold production",
                "silverMultiplier", 1.1,
                "resourceCost", Map.of("wood", 5, "stone", 5)));
        BUILDING_TYPES.put("festival_ground", b("Festival Ground", 220, 0, 1, 0, 14, 10, 70,
                "Monthly festivals give +80g, boosts happiness",
                "monthlyGold", 80,
                "resourceCost", Map.of("wood", 4, "stone", 3)));
        BUILDING_TYPES.put("library", b("Library", 380, 0, 1, 0, 26, 8, 75,
                "Repository of knowledge, enables advanced research",
                "resourceCost", Map.of("wood", 4, "stone", 3)));
        BUILDING_TYPES.put("hospital", b("Hospital", 480, 0, 1, 0, 31, 12, 90,
                "Heals the sick, major population growth bonus",
                "populationGrowthBonus", 3,
                "resourceCost", Map.of("wood", 4, "iron", 3)));
        BUILDING_TYPES.put("lighthouse", b("Lighthouse", 420, 0, 1, 25, 16, 6, 80,
                "Guides trade ships, steady silver income",
                "resourceCost", Map.of("stone", 5)));

        // TIER 4: Fortifications — heavy stone/iron requirements
        BUILDING_TYPES.put("stone_walls", b("Stone Walls", 1000, 0, 1, 0, 21, -2, 80,
                "Upgrade from wood fort, major protection boost (+30)",
                "protectionBonus", 30, "castleUpgrade", "stone_fort", "requires", "wood_fort",
                "resourceCost", Map.of("stone", 20)));
        BUILDING_TYPES.put("castle_keep", b("Castle Keep", 1800, 0, 1, 0, 36, -4, 150,
                "Ultimate fortification, massive protection (+50)",
                "protectionBonus", 50, "castleUpgrade", "stone_castle", "requires", "stone_fort",
                "resourceCost", Map.of("stone", 12, "iron", 8)));

        // TIER 5: Advanced City (100-130 pop)
        BUILDING_TYPES.put("church", b("Church", 650, 0, 1, 0, 41, 30, 100,
                "Grand place of worship, massive happiness and population growth",
                "populationGrowthBonus", 2, "requires", "chapel",
                "resourceCost", Map.of("stone", 8, "iron", 2)));
        BUILDING_TYPES.put("grand_theater", b("Grand Theater", 550, 0, 1, 30, 24, 12, 110,
                "Entertainment for the masses, significant happiness boost",
                "resourceCost", Map.of("wood", 6, "stone", 4)));
        BUILDING_TYPES.put("university", b("University", 800, 0, 1, 0, 46, 15, 130,
                "Advanced learning center, major happiness and research",
                "requires", "library",
                "resourceCost", Map.of("stone", 5, "iron", 3)));
        BUILDING_TYPES.put("mint", b("Mint", 900, 0, 1, 40, 31, -2, 140,
                "Coins your own currency, substantial silver income",
                "resourceCost", Map.of("iron", 8, "stone", 5)));

        // TIER 6: Infrastructure Megaprojects (150+ pop)
        BUILDING_TYPES.put("aqueduct", b("Aqueduct", 1200, 0, 1, 0, 26, 18, 150,
                "Fresh water supply, enables canal and boosts happiness",
                "populationGrowthBonus", 2,
                "resourceCost", Map.of("stone", 10, "iron", 5)));
        BUILDING_TYPES.put("canal_small", b("Small Canal", 800, 200, 2, 10, 21, 8, 160,
                "Local waterway for trade, good silver income",
                "requires", "aqueduct"));
        BUILDING_TYPES.put("canal_major", b("Major Canal", 2500, 0, 1, 40, 46, 25, 200,
                "Grand canal connecting to Dredge Lake, +15% gold production",
                "silverMultiplier", 1.15, "requires", "canal_small"));
        BUILDING_TYPES.put("harbor", b("Harbor", 1400, 0, 1, 100, 36, 15, 170,
                "Seaport for international trade",
                "resourceCost", Map.of("wood", 8, "stone", 8)));

        // TIER 7: Luxury & Endgame (180+ pop)
        BUILDING_TYPES.put("palace", b("Palace", 3000, 0, 1, 0, 66, 35, 200,
                "Symbol of power and prosperity, enormous happiness"));
        BUILDING_TYPES.put("colosseum", b("Colosseum", 2200, 0, 1, 50, 41, 20, 180,
                "Grand arena for games and spectacles"));
        BUILDING_TYPES.put("museum", b("Museum", 1600, 0, 1, 0, 31, 12, 160,
                "Preserves history and culture"));
    }

    private static Map<String, Object> b(String name, int baseCost, int costIncrease, Integer maxCount,
                                          int dailySilver, int dailyUpkeep, int happiness, int minPopulation,
                                          String description, Object... extras) {
        Map<String, Object> m = new HashMap<>();
        m.put("name", name);
        m.put("baseCost", baseCost);
        m.put("costIncrease", costIncrease);
        m.put("maxCount", maxCount);
        m.put("dailySilver", dailySilver);
        m.put("dailyUpkeep", dailyUpkeep);
        m.put("happiness", happiness);
        m.put("minPopulation", minPopulation);
        m.put("description", description);
        // Optional key-value pairs
        for (int i = 0; i < extras.length - 1; i += 2) {
            m.put((String) extras[i], extras[i + 1]);
        }
        return m;
    }

    public static int getCost(String type, int currentCount) {
        Map<String, Object> info = BUILDING_TYPES.get(type);
        if (info == null) return 0;
        int baseCost = (int) info.get("baseCost");
        int costIncrease = (int) info.get("costIncrease");
        return baseCost + costIncrease * currentCount;
    }

    public static int getMaxCount(String type, int population) {
        Map<String, Object> info = BUILDING_TYPES.get(type);
        if (info == null) return 0;
        Integer baseMax = (Integer) info.get("maxCount");
        if (baseMax == null) return 999;

        int bonus = 0;
        switch (type) {
            case "alchemy_garden":
            case "tavern":
                if (population >= 100) bonus = 2;
                break;
            case "vegetable_garden":
                if (population >= 120) bonus = 3;
                break;
            case "mine":
                if (population >= 150) bonus = 1;
                break;
            case "orchard":
            case "blacksmith":
                if (population >= 150) bonus = 2;
                break;
            case "vineyard":
                if (population >= 120) bonus = 1;
                break;
            case "market":
                if (population >= 180) bonus = 1;
                break;
        }
        return baseMax + bonus;
    }

    public static String getBuildingName(String type) {
        Map<String, Object> info = BUILDING_TYPES.get(type);
        return info != null ? (String) info.get("name") : type;
    }
}
