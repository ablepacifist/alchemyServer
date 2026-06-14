package alchemy.logic;

import alchemy.data.IStubDatabase;
import alchemy.object.BuildingConfig;
import alchemy.object.Holdfast;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class HoldfastManager implements HoldfastManagerService {

    private final IStubDatabase db;
    private static final double HAPPINESS_CHANGE_RATE = 0.5;
    private final Random random = new Random();

    @Autowired
    public HoldfastManager(IStubDatabase db) {
        this.db = db;
    }

    @Override
    public Holdfast createHoldfast(String groupName, String holdfastName) {
        Holdfast h = new Holdfast(groupName, holdfastName);
        db.addHoldfast(h);
        return h;
    }

    @Override
    public Holdfast getHoldfast(String groupName) {
        return db.getHoldfast(groupName);
    }

    @Override
    public List<Holdfast> getAllHoldfasts() {
        return db.getAllHoldfasts();
    }

    @Override
    public void deleteHoldfast(String groupName) {
        db.deleteHoldfast(groupName);
    }

    @Override
    public Holdfast importHoldfast(Holdfast holdfast) {
        for (String type : alchemy.object.BuildingConfig.BUILDING_TYPES.keySet()) {
            holdfast.getBuildings().putIfAbsent(type, 0);
        }
        db.addHoldfast(holdfast);
        return db.getHoldfast(holdfast.getGroupName());
    }

    @Override
    public List<Map<String, Object>> getEvents(String groupName) {
        Holdfast h = db.getHoldfast(groupName);
        if (h == null) return null;
        return db.getHoldfastEvents(h.getId());
    }

    private void logEvent(List<String> events, Holdfast h, String message) {
        events.add(message);
        db.addHoldfastEvent(h.getId(), h.getDaysElapsed(), message);
    }

    @Override
    public Map<String, Object> getStatus(String groupName) {
        Holdfast h = db.getHoldfast(groupName);
        if (h == null) return null;
        Map<String, Object> status = new HashMap<>();
        status.put("holdfast", h);
        status.put("dailyIncome", round2(calculateDailyGoldProduction(h)));
        status.put("dailyUpkeep", round2(calculateDailyUpkeep(h)));
        status.put("netDailyGold", round2(calculateDailyGoldProduction(h) - calculateDailyUpkeep(h)));
        status.put("protection", round2(calculateProtection(h)));
        status.put("raidChance", round2(calculateRaidChance(h)));
        status.put("targetHappiness", calculateTargetHappiness(h));
        status.put("buildingMenu", buildBuildingMenu(h));
        return status;
    }

    @Override
    public Map<String, Object> advanceTime(String groupName, int days) {
        Holdfast h = db.getHoldfast(groupName);
        if (h == null) {
            Map<String, Object> err = new HashMap<>();
            err.put("success", false);
            err.put("message", "Holdfast not found");
            return err;
        }

        List<String> events = new ArrayList<>();
        logEvent(events, h, "Advancing " + days + " day(s) from day " + h.getDaysElapsed() + "...");

        for (int day = 0; day < days; day++) {
            h.setDaysElapsed(h.getDaysElapsed() + 1);

            // Daily gold
            double income = calculateDailyGoldProduction(h);
            double upkeep = calculateDailyUpkeep(h);
            double net = income - upkeep;
            h.setGold(h.getGold() + net);

            // Daily food consumption: 0.2 food per person
            int foodNeeded = (int) Math.ceil(h.getPopulation() * 0.2);
            if (h.getFood() >= foodNeeded) {
                h.setFood(h.getFood() - foodNeeded);
                if (h.getDaysElapsed() % 7 == 0 && h.getFood() < foodNeeded * 14) {
                    logEvent(events, h, "DAY " + h.getDaysElapsed() + " - Food supplies low! " +
                            h.getFood() + " remaining (~" + (h.getFood() / Math.max(1, foodNeeded)) + " days)");
                }
            } else {
                h.setFood(0);
                double rationCost = h.getPopulation() * 2.0;
                if (h.getGold() >= rationCost) {
                    h.setGold(h.getGold() - rationCost);
                    if (h.getDaysElapsed() % 7 == 0) {
                        logEvent(events, h, "DAY " + h.getDaysElapsed() + " - No food! Spent " +
                                String.format("%.0f", rationCost) + "g on emergency rations (" +
                                h.getPopulation() + " people × 2g). Build wheat fields!");
                    }
                } else {
                    h.setGold(0);
                    h.setHappiness(Math.max(0, h.getHappiness() - 3));
                    if (h.getDaysElapsed() % 7 == 0) {
                        logEvent(events, h, "DAY " + h.getDaysElapsed() + " - FAMINE! No food and no gold for rations. Happiness -3/day!");
                    }
                }
            }

            // Update happiness
            updateHappiness(h);

            // Daily raid check
            Map<String, Object> raidResult = executeBanditRaid(h);
            if ((boolean) raidResult.get("success")) {
                logEvent(events, h, "DAY " + h.getDaysElapsed() + " - BANDIT RAID! Bandits stole " +
                        String.format("%.1f", raidResult.get("goldStolen")) + "g!");
                @SuppressWarnings("unchecked")
                List<String> destroyed = (List<String>) raidResult.get("buildingsDestroyed");
                if (!destroyed.isEmpty()) {
                    logEvent(events, h, "  Buildings destroyed: " + String.join(", ", destroyed));
                }
                int casualties = (int) raidResult.get("casualties");
                if (casualties > 0) {
                    logEvent(events, h, "  Population casualties: " + casualties);
                }
            }

            // Population growth every 7 days
            if (h.getDaysElapsed() % 7 == 0) {
                String growthMsg = checkPopulationGrowth(h);
                if (growthMsg != null) logEvent(events, h, "DAY " + h.getDaysElapsed() + " - " + growthMsg);
            }

            // Periodic production
            List<String> production = checkProduction(h);
            for (String msg : production) {
                logEvent(events, h, "DAY " + h.getDaysElapsed() + " - " + msg);
            }

            // Summary every 10 days
            if ((day + 1) % 10 == 0 || day == days - 1) {
                logEvent(events, h, String.format("Day %d: Net %+.1fg | Total: %.1fg",
                        h.getDaysElapsed(), net, h.getGold()));
            }
        }

        db.updateHoldfast(h);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("events", events);
        result.put("holdfast", h);
        result.put("dailyIncome", round2(calculateDailyGoldProduction(h)));
        result.put("dailyUpkeep", round2(calculateDailyUpkeep(h)));
        return result;
    }

    @Override
    public Map<String, Object> build(String groupName, String buildingType) {
        Map<String, Object> result = new HashMap<>();
        Holdfast h = db.getHoldfast(groupName);
        if (h == null) {
            result.put("success", false);
            result.put("message", "Holdfast not found");
            return result;
        }

        Map<String, Object> info = BuildingConfig.BUILDING_TYPES.get(buildingType);
        if (info == null) {
            result.put("success", false);
            result.put("message", "Unknown building type: " + buildingType);
            return result;
        }

        int currentCount = h.getBuildingCount(buildingType);
        int minPop = (int) info.get("minPopulation");
        if (h.getPopulation() < minPop) {
            result.put("success", false);
            result.put("message", info.get("name") + " requires at least " + minPop + " population (current: " + h.getPopulation() + ")");
            return result;
        }

        // Check building requirements
        String requires = (String) info.get("requires");
        if (requires != null) {
            if (requires.equals("chapel") && h.getBuildingCount("chapel") == 0) {
                result.put("success", false);
                result.put("message", info.get("name") + " requires a Chapel first");
                return result;
            }
            if (requires.equals("wood_fort") && !h.getCastleType().equals("wood_fort")) {
                result.put("success", false);
                result.put("message", info.get("name") + " can only be built on a wood fort");
                return result;
            }
            if (requires.equals("stone_fort") && !h.getCastleType().equals("stone_fort")) {
                result.put("success", false);
                result.put("message", info.get("name") + " requires a stone fort first");
                return result;
            }
            if (requires.equals("aqueduct") && h.getBuildingCount("aqueduct") == 0) {
                result.put("success", false);
                result.put("message", info.get("name") + " requires an Aqueduct first");
                return result;
            }
            if (requires.equals("canal_small") && h.getBuildingCount("canal_small") == 0) {
                result.put("success", false);
                result.put("message", info.get("name") + " requires a Small Canal first");
                return result;
            }
            if (requires.equals("library") && h.getBuildingCount("library") == 0) {
                result.put("success", false);
                result.put("message", info.get("name") + " requires a Library first");
                return result;
            }
        }

        // Check max count
        int maxAllowed = BuildingConfig.getMaxCount(buildingType, h.getPopulation());
        if (maxAllowed != 999 && currentCount >= maxAllowed) {
            result.put("success", false);
            result.put("message", "Maximum " + info.get("name") + "s reached (" + maxAllowed + ")");
            return result;
        }

        // Calculate cost with carpenter discount
        int baseCost = BuildingConfig.getCost(buildingType, currentCount);
        int carpenters = h.getBuildingCount("carpenter");
        double discount = carpenters * 0.1;
        int cost = (int) (baseCost * (1 - discount));

        if (h.getGold() < cost) {
            double shortfall = cost - h.getGold();
            double net = calculateDailyGoldProduction(h) - calculateDailyUpkeep(h);
            int daysNeeded = net > 0 ? (int) Math.ceil(shortfall / net) : 9999;
            result.put("success", false);
            result.put("message", "Insufficient funds. Need: " + cost + "g, Have: " +
                    String.format("%.1f", h.getGold()) + "g. Advance ~" + daysNeeded + " days.");
            return result;
        }

        // Check resource costs
        @SuppressWarnings("unchecked")
        Map<String, Integer> resourceCost = (Map<String, Integer>) info.get("resourceCost");
        if (resourceCost != null) {
            for (Map.Entry<String, Integer> e : resourceCost.entrySet()) {
                int have = getResource(h, e.getKey());
                if (have < e.getValue()) {
                    result.put("success", false);
                    result.put("message", "Need " + e.getValue() + " " + e.getKey() +
                            " to build " + info.get("name") + " (have " + have + ")");
                    return result;
                }
            }
            for (Map.Entry<String, Integer> e : resourceCost.entrySet()) {
                deductResource(h, e.getKey(), e.getValue());
            }
        }

        // Build it
        h.setGold(h.getGold() - cost);
        h.setBuildingCount(buildingType, currentCount + 1);

        // Castle upgrade
        String castleUpgrade = (String) info.get("castleUpgrade");
        if (castleUpgrade != null) {
            h.setCastleType(castleUpgrade);
        }

        // Track plant days for fields
        switch (buildingType) {
            case "wheat_field": h.getWheatFieldPlantDays().add(h.getDaysElapsed()); break;
            case "vegetable_garden": h.getVegetableGardenPlantDays().add(h.getDaysElapsed()); break;
            case "orchard": h.getOrchardPlantDays().add(h.getDaysElapsed()); break;
            case "vineyard": h.getVineyardPlantDays().add(h.getDaysElapsed()); break;
        }

        db.updateHoldfast(h);

        String discountText = discount > 0 ? " (discounted from " + baseCost + "g)" : "";
        result.put("success", true);
        result.put("message", "Built " + info.get("name") + " for " + cost + "g" + discountText);
        result.put("holdfast", h);
        result.put("cost", cost);
        return result;
    }

    @Override
    public boolean withdraw(String groupName, double gold, int beer, int wine, int grain, int tools) {
        Holdfast h = db.getHoldfast(groupName);
        if (h == null) return false;
        if (gold > h.getGold() || beer > h.getBeer() || wine > h.getWine() ||
                grain > h.getGrain() || tools > h.getTools()) {
            return false;
        }
        h.setGold(h.getGold() - gold);
        h.setBeer(h.getBeer() - beer);
        h.setWine(h.getWine() - wine);
        h.setGrain(h.getGrain() - grain);
        h.setTools(h.getTools() - tools);
        db.updateHoldfast(h);
        return true;
    }

    @Override
    public Holdfast deposit(String groupName, double gold) {
        Holdfast h = db.getHoldfast(groupName);
        if (h == null) return null;
        h.setGold(h.getGold() + gold);
        db.updateHoldfast(h);
        return h;
    }

    // ---- Game mechanics (ported from Python) ----

    private double calculateDailyGoldProduction(Holdfast h) {
        double gold = h.getBaseGoldPerDay() + (h.getPopulation() / 10.0);
        double dailySilver = 0;

        for (Map.Entry<String, Integer> entry : h.getBuildings().entrySet()) {
            int count = entry.getValue();
            if (count == 0) continue;
            Map<String, Object> info = BuildingConfig.BUILDING_TYPES.get(entry.getKey());
            if (info == null) continue;
            int silver = (int) info.get("dailySilver");
            dailySilver += silver * count;
        }

        int markets = h.getBuildingCount("market");
        if (markets > 0) dailySilver *= (1 + 0.1 * markets);

        if (h.getBuildingCount("canal_major") > 0) dailySilver *= 1.15;

        // Buildings produce 15% less gold than base values
        dailySilver *= 0.85;

        gold += dailySilver / 10.0;
        return gold;
    }

    private double calculateDailyUpkeep(Holdfast h) {
        double upkeepSilver = 0;
        for (Map.Entry<String, Integer> entry : h.getBuildings().entrySet()) {
            int count = entry.getValue();
            if (count == 0) continue;
            Map<String, Object> info = BuildingConfig.BUILDING_TYPES.get(entry.getKey());
            if (info == null) continue;
            int upkeep = (int) info.get("dailyUpkeep");
            upkeepSilver += upkeep * count;
        }
        return upkeepSilver / 10.0;
    }

    private double calculateProtection(Holdfast h) {
        double base = 50;
        double castleBonus = 0;
        switch (h.getCastleType()) {
            case "stone_fort": castleBonus = 30; break;
            case "stone_castle": castleBonus = 80; break;
        }
        double gtBonus = h.getBuildingCount("guard_tower") * 10;
        double smithBonus = h.getBuildingCount("blacksmith") * 5;
        double wallBonus = h.getBuildingCount("stone_walls") * 30;
        double keepBonus = h.getBuildingCount("castle_keep") * 50;
        double popPenalty = Math.max(0, (h.getPopulation() - 50) / 5.0);
        double wealthPenalty = calculateDailyGoldProduction(h) / 20.0;
        return Math.max(0, base + castleBonus + gtBonus + smithBonus + wallBonus + keepBonus - popPenalty - wealthPenalty);
    }

    private int calculateTargetHappiness(Holdfast h) {
        int buildingHappiness = 0;
        for (Map.Entry<String, Integer> entry : h.getBuildings().entrySet()) {
            int count = entry.getValue();
            if (count == 0) continue;
            Map<String, Object> info = BuildingConfig.BUILDING_TYPES.get(entry.getKey());
            if (info == null) continue;
            buildingHappiness += (int) info.get("happiness") * count;
        }
        // Exponential crowding penalty: each person above 40 compounds at 4%/person
        double crowdingPenalty = Math.pow(1.04, Math.max(0, h.getPopulation() - 40));
        int happiness = (int) Math.round(75 + buildingHappiness - crowdingPenalty);
        return Math.max(0, Math.min(100, happiness));
    }

    private void updateHappiness(Holdfast h) {
        int target = calculateTargetHappiness(h);
        h.setTargetHappiness(target);
        double current = h.getHappiness();
        if (current < target) {
            h.setHappiness(Math.min(target, current + HAPPINESS_CHANGE_RATE));
        } else if (current > target) {
            h.setHappiness(Math.max(target, current - HAPPINESS_CHANGE_RATE));
        }
        h.setHappiness(Math.round(h.getHappiness() * 10.0) / 10.0);
    }

    private double calculateRaidChance(Holdfast h) {
        double protection = calculateProtection(h);
        double chance = Math.max(0.5, 8 - (protection * 0.075));
        return Math.min(8, chance);
    }

    private Map<String, Object> executeBanditRaid(Holdfast h) {
        Map<String, Object> result = new HashMap<>();
        double raidChance = calculateRaidChance(h);
        if (random.nextDouble() * 100 > raidChance) {
            result.put("success", false);
            return result;
        }
        result.put("success", true);

        // Steal 30-40% of gold
        double stolenPercent = 0.30 + random.nextDouble() * 0.10;
        double goldStolen = (int) (h.getGold() * stolenPercent);
        h.setGold(Math.max(0, h.getGold() - goldStolen));
        result.put("goldStolen", goldStolen);

        // Destroy 1-3 buildings
        List<String> destructible = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : h.getBuildings().entrySet()) {
            String type = entry.getKey();
            if (entry.getValue() > 0 && !type.equals("stone_walls") && !type.equals("castle_keep")) {
                for (int i = 0; i < entry.getValue(); i++) destructible.add(type);
            }
        }
        List<String> destroyedNames = new ArrayList<>();
        if (!destructible.isEmpty()) {
            int numDestroy = Math.min(1 + random.nextInt(3), destructible.size());
            Collections.shuffle(destructible, random);
            for (int i = 0; i < numDestroy; i++) {
                String type = destructible.get(i);
                h.setBuildingCount(type, h.getBuildingCount(type) - 1);
                destroyedNames.add(BuildingConfig.getBuildingName(type));
                // Remove a plant day if it's a field
                switch (type) {
                    case "wheat_field": if (!h.getWheatFieldPlantDays().isEmpty()) h.getWheatFieldPlantDays().remove(h.getWheatFieldPlantDays().size() - 1); break;
                    case "vegetable_garden": if (!h.getVegetableGardenPlantDays().isEmpty()) h.getVegetableGardenPlantDays().remove(h.getVegetableGardenPlantDays().size() - 1); break;
                    case "orchard": if (!h.getOrchardPlantDays().isEmpty()) h.getOrchardPlantDays().remove(h.getOrchardPlantDays().size() - 1); break;
                    case "vineyard": if (!h.getVineyardPlantDays().isEmpty()) h.getVineyardPlantDays().remove(h.getVineyardPlantDays().size() - 1); break;
                }
            }
        }
        result.put("buildingsDestroyed", destroyedNames);

        // Casualties 5-15%
        int casualties = (int) (h.getPopulation() * (0.05 + random.nextDouble() * 0.10));
        h.setPopulation(Math.max(20, h.getPopulation() - casualties));
        result.put("casualties", casualties);
        h.setRaidsSurvived(h.getRaidsSurvived() + 1);

        return result;
    }

    private String checkPopulationGrowth(Holdfast h) {
        double happiness = h.getHappiness();
        int weeklyGrowth = 0;
        if (happiness >= 65) {
            double growthChance = (happiness - 65) / 100.0 + 0.10;
            int growthBonus = 0;
            growthBonus += h.getBuildingCount("church") * 2;
            growthBonus += h.getBuildingCount("hospital") * 3;
            growthBonus += h.getBuildingCount("aqueduct") * 2;
            if (random.nextDouble() < growthChance) {
                weeklyGrowth = 1 + random.nextInt(3) + growthBonus;
                h.setPopulation(h.getPopulation() + weeklyGrowth);
            }
        }
        List<Integer> history = h.getPopulationGrowthHistory();
        history.add(weeklyGrowth);
        if (history.size() > 8) history.remove(0);

        return weeklyGrowth > 0 ? "Population grew by " + weeklyGrowth + "! New population: " + h.getPopulation() : null;
    }

    private List<String> checkProduction(Holdfast h) {
        List<String> events = new ArrayList<>();
        int day = h.getDaysElapsed();

        // Alchemy gardens every 10 days (flavor, no stored resource)
        if (day % 10 == 0 && h.getBuildingCount("alchemy_garden") > 0) {
            int count = h.getBuildingCount("alchemy_garden");
            events.add("Alchemy Gardens produced " + (count * (1 + random.nextInt(8))) + " alchemy supplies");
        }

        // Mines every 7 days: 2 stone + 1 iron per mine
        if (day % 7 == 0 && h.getBuildingCount("mine") > 0) {
            int mines = h.getBuildingCount("mine");
            int stone = mines * 2;
            int iron = mines * 1;
            h.setStone(h.getStone() + stone);
            h.setIron(h.getIron() + iron);
            events.add("Mines produced " + stone + " stone + " + iron + " iron (Stone: " +
                    h.getStone() + ", Iron: " + h.getIron() + ")");
        }

        // Logging Camps every 7 days: 3 wood per camp
        if (day % 7 == 0 && h.getBuildingCount("logging_camp") > 0) {
            int camps = h.getBuildingCount("logging_camp");
            int wood = camps * 3;
            h.setWood(h.getWood() + wood);
            events.add("Logging Camps produced " + wood + " wood (Total: " + h.getWood() + ")");
        }

        // Taverns every 7 days (beer)
        if (day % 7 == 0 && h.getBuildingCount("tavern") > 0) {
            int produced = h.getBuildingCount("tavern") * 3;
            h.setBeer(h.getBeer() + produced);
            events.add("Taverns produced " + produced + " beer (Total: " + h.getBeer() + ")");
        }

        // Blacksmiths every 14 days (tools)
        if (day % 14 == 0 && h.getBuildingCount("blacksmith") > 0) {
            int produced = h.getBuildingCount("blacksmith") * 2;
            h.setTools(h.getTools() + produced);
            events.add("Blacksmiths produced " + produced + " tools (Total: " + h.getTools() + ")");
        }

        // Vineyards every 7 days (wine)
        if (day % 7 == 0 && h.getBuildingCount("vineyard") > 0) {
            int produced = h.getBuildingCount("vineyard") * 2;
            h.setWine(h.getWine() + produced);
            events.add("Vineyards produced " + produced + " wine (Total: " + h.getWine() + ")");
        }

        // Festival Ground every 30 days
        if (day % 30 == 0 && h.getBuildingCount("festival_ground") > 0) {
            h.setGold(h.getGold() + 80);
            events.add("Festival Ground hosted celebration! +80g");
        }

        // Harvests — fields produce food (auto-replant after harvest), orchards give food + gold
        events.addAll(checkHarvests(h, "wheat_field", 14, 20, 0, h.getWheatFieldPlantDays()));
        events.addAll(checkHarvests(h, "vegetable_garden", 10, 10, 0, h.getVegetableGardenPlantDays()));
        events.addAll(checkHarvests(h, "orchard", 30, 8, 40, h.getOrchardPlantDays()));
        events.addAll(checkHarvests(h, "vineyard", 90, 0, 0, h.getVineyardPlantDays()));

        return events;
    }

    // harvestFood=0 means no food; harvestGold=0 means no gold bonus; auto-replants after harvest
    private List<String> checkHarvests(Holdfast h, String fieldType, int harvestDays,
                                        int harvestFood, int harvestGold, List<Integer> plantDaysList) {
        List<String> events = new ArrayList<>();
        List<Integer> toHarvest = new ArrayList<>();
        for (int i = 0; i < plantDaysList.size(); i++) {
            if (h.getDaysElapsed() - plantDaysList.get(i) >= harvestDays) {
                toHarvest.add(i);
            }
        }
        List<Integer> replantDays = new ArrayList<>();
        for (int i = toHarvest.size() - 1; i >= 0; i--) {
            int idx = toHarvest.get(i);
            plantDaysList.remove(idx);
            replantDays.add(h.getDaysElapsed()); // auto-replant
            if (harvestFood > 0) h.setFood(h.getFood() + harvestFood);
            if (harvestGold > 0) h.setGold(h.getGold() + harvestGold);
            StringBuilder msg = new StringBuilder(fieldType.replace("_", " ") + " harvested!");
            if (harvestFood > 0) msg.append(" +").append(harvestFood).append(" food");
            if (harvestGold > 0) msg.append(" +").append(harvestGold).append("g");
            msg.append(" (Food: ").append(h.getFood()).append(")");
            events.add(msg.toString());
        }
        plantDaysList.addAll(replantDays);
        return events;
    }

    private List<Map<String, Object>> buildBuildingMenu(Holdfast h) {
        List<Map<String, Object>> menu = new ArrayList<>();
        for (Map.Entry<String, Map<String, Object>> entry : BuildingConfig.BUILDING_TYPES.entrySet()) {
            String type = entry.getKey();
            Map<String, Object> info = entry.getValue();
            int current = h.getBuildingCount(type);
            int minPop = (int) info.get("minPopulation");
            int maxCount = BuildingConfig.getMaxCount(type, h.getPopulation());

            Map<String, Object> item = new LinkedHashMap<>();
            item.put("type", type);
            item.put("name", info.get("name"));
            item.put("description", info.get("description"));
            item.put("current", current);
            item.put("max", maxCount == 999 ? null : maxCount);

            // Determine status
            String status = "available";
            String lockReason = null;
            if (h.getPopulation() < minPop) {
                status = "locked";
                lockReason = "Requires " + minPop + " population";
            } else {
                String requires = (String) info.get("requires");
                if (requires != null) {
                    boolean met = checkRequirement(h, requires);
                    if (!met) {
                        status = "locked";
                        lockReason = "Requires " + requires.replace("_", " ");
                    }
                }
            }
            if (status.equals("available") && maxCount != 999 && current >= maxCount) {
                status = "maxed";
            }

            item.put("status", status);
            item.put("lockReason", lockReason);

            int carpenters = h.getBuildingCount("carpenter");
            int baseCost = BuildingConfig.getCost(type, current);
            int cost = (int) (baseCost * (1 - carpenters * 0.1));
            item.put("cost", cost);
            item.put("baseCost", baseCost);
            item.put("dailySilver", info.get("dailySilver"));
            item.put("dailyUpkeep", info.get("dailyUpkeep"));
            item.put("happiness", info.get("happiness"));
            item.put("resourceCost", info.get("resourceCost"));
            item.put("harvestFood", info.get("harvestFood"));
            item.put("harvestGold", info.get("harvestGold"));
            item.put("harvestDays", info.get("harvestDays"));
            item.put("productionItem", info.get("productionItem"));
            item.put("productionAmount", info.get("productionAmount"));
            item.put("productionDays", info.get("productionDays"));

            menu.add(item);
        }
        return menu;
    }

    private int getResource(Holdfast h, String resource) {
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

    private void deductResource(Holdfast h, String resource, int amount) {
        switch (resource) {
            case "wood":  h.setWood(h.getWood() - amount);   break;
            case "stone": h.setStone(h.getStone() - amount); break;
            case "iron":  h.setIron(h.getIron() - amount);   break;
            case "food":  h.setFood(h.getFood() - amount);   break;
            case "beer":  h.setBeer(h.getBeer() - amount);   break;
            case "wine":  h.setWine(h.getWine() - amount);   break;
            case "tools": h.setTools(h.getTools() - amount); break;
        }
    }

    private boolean checkRequirement(Holdfast h, String req) {
        switch (req) {
            case "chapel": return h.getBuildingCount("chapel") > 0;
            case "wood_fort": return h.getCastleType().equals("wood_fort");
            case "stone_fort": return h.getCastleType().equals("stone_fort");
            case "aqueduct": return h.getBuildingCount("aqueduct") > 0;
            case "canal_small": return h.getBuildingCount("canal_small") > 0;
            case "library": return h.getBuildingCount("library") > 0;
            default: return true;
        }
    }

    private double round2(double val) {
        return Math.round(val * 100.0) / 100.0;
    }
}
