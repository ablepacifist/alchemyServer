package alchemy.logic;

import alchemy.data.ILexiconIdentityClient;
import alchemy.data.IStubDatabase;
import alchemy.object.Holdfast;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/** The status carries every derived value the clients show, so they calculate nothing. */
@SuppressWarnings("unchecked")
class HoldfastStatusViewTest {

    private IStubDatabase db;
    private HoldfastManager manager;
    private Holdfast h;

    @BeforeEach
    void setUp() {
        db = mock(IStubDatabase.class);
        h = new Holdfast("party", "Lockwood");
        when(db.getHoldfast("party")).thenReturn(h);
        manager = new HoldfastManager(db, mock(ILexiconIdentityClient.class));
    }

    private Map<String, Object> status() {
        return manager.getStatus("party");
    }

    @Test
    void calendarGivesWeekAndDayOfWeek() {
        h.setDaysElapsed(43);
        Map<String, Object> cal = (Map<String, Object>) status().get("calendar");
        assertEquals(43, cal.get("day"));
        assertEquals(7, cal.get("week"));
        assertEquals(6, cal.get("weeksElapsed"));
        assertEquals(1, cal.get("dayOfWeek"));
    }

    @Test
    void cropProgressIsWorkedOutPerField() {
        h.setDaysElapsed(20);
        h.setBuildingCount("wheat_field", 2);
        h.getWheatFieldPlantDays().add(13);   // 7 of 14 days
        h.getWheatFieldPlantDays().add(0);    // past harvest: capped
        List<Map<String, Object>> crops = (List<Map<String, Object>>) status().get("crops");
        assertEquals(1, crops.size());
        Map<String, Object> wheat = crops.get(0);
        assertEquals("wheat_field", wheat.get("type"));
        assertEquals(14, wheat.get("harvestDays"));
        List<Map<String, Object>> fields = (List<Map<String, Object>>) wheat.get("fields");
        assertEquals(7, fields.get(0).get("grownDays"));
        assertEquals(50, fields.get(0).get("percent"));
        assertEquals(false, fields.get(0).get("ready"));
        assertEquals(14, fields.get(1).get("grownDays"));
        assertEquals(100, fields.get(1).get("percent"));
        assertEquals(true, fields.get(1).get("ready"));
    }

    @Test
    void vineyardsHaveNoProgressBar() {
        h.setBuildingCount("vineyard", 1);
        h.getVineyardPlantDays().add(0);
        Map<String, Object> vineyard = ((List<Map<String, Object>>) status().get("crops")).get(0);
        assertEquals(false, vineyard.get("showProgress"));
        assertNull(vineyard.get("harvestDays"));
        assertTrue(((List<?>) vineyard.get("fields")).isEmpty());
    }

    @Test
    void fallowFieldsCarryTheReplantCostAndWhetherItIsAffordable() {
        h.setBuildingCount("rye_field", 3);
        h.getRyeFieldPlantDays().add(0);
        h.setGold(20);
        List<Map<String, Object>> fallow = (List<Map<String, Object>>) status().get("fallowFields");
        assertEquals(1, fallow.size());
        assertEquals(2, fallow.get(0).get("fallow"));
        assertEquals(24, fallow.get(0).get("replantCost"));
        assertEquals(false, fallow.get(0).get("canAfford"));
    }

    @Test
    void perennialsAreNeverFallow() {
        h.setBuildingCount("orchard", 2);
        assertTrue(((List<?>) status().get("fallowFields")).isEmpty());
    }

    @Test
    void buildMenuSaysWhatIsAffordable() {
        h.setGold(1000);
        h.setWood(1);
        Map<String, Object> tower = menuItem("guard_tower");
        assertEquals(false, tower.get("canAffordResources"));
        assertEquals(true, tower.get("canAffordGold"));
        assertEquals(false, tower.get("canBuild"));
        Map<String, Object> wood = ((List<Map<String, Object>>) tower.get("resourceRequirements")).get(0);
        assertEquals("wood", wood.get("resource"));
        assertEquals(4, wood.get("needed"));
        assertEquals(1, wood.get("have"));
        assertEquals(false, wood.get("enough"));

        h.setWood(10);
        assertEquals(true, menuItem("guard_tower").get("canBuild"));
        h.setGold(0);
        assertEquals(false, menuItem("guard_tower").get("canBuild"));
    }

    private Map<String, Object> menuItem(String type) {
        for (Map<String, Object> item : (List<Map<String, Object>>) status().get("buildingMenu")) {
            if (type.equals(item.get("type"))) return item;
        }
        throw new AssertionError(type + " not in menu");
    }

    @Test
    void foodLevelMatchesTheServersLowFoodWarning() {
        h.setPopulation(50);           // eats 10 a day; warns under 140
        h.setFood(100);
        assertEquals("warn", ((Map<String, Object>) status().get("food")).get("level"));
        h.setFood(200);
        assertEquals("ok", ((Map<String, Object>) status().get("food")).get("level"));
        h.setFood(0);
        Map<String, Object> food = (Map<String, Object>) status().get("food");
        assertEquals("danger", food.get("level"));
        assertEquals(10, food.get("dailyNeed"));
        assertEquals(100.0, food.get("rationCostPerDay"));
    }

    @Test
    void foodMarketFiguresComeFromTheSameRulesAsTheSimulation() {
        h.setBuildingCount("food_market", 2);
        h.setPopulation(36);
        h.setFood(105);
        Map<String, Object> market = (Map<String, Object>) status().get("foodMarket");
        assertEquals(true, market.get("available"));
        assertEquals(10, market.get("dailyCap"));
        assertEquals(8.0, market.get("dailyGold"));
        assertEquals(100, market.get("reserve"));
        assertEquals(5, market.get("sellableToday"));
    }

    @Test
    void builtBuildingsListOnlyOwnedOnesWithNames() {
        h.setBuildingCount("tavern", 2);
        List<Map<String, Object>> built = (List<Map<String, Object>>) status().get("builtBuildings");
        assertTrue(built.stream().anyMatch(b -> "tavern".equals(b.get("type")) && "Tavern".equals(b.get("name")) && Integer.valueOf(2).equals(b.get("count"))));
        assertTrue(built.stream().allMatch(b -> (int) b.get("count") > 0));
    }

    @Test
    void populationTrendAndCastleName() {
        h.getPopulationGrowthHistory().clear();
        assertNull(status().get("populationTrend"));
        h.getPopulationGrowthHistory().add(20);
        h.getPopulationGrowthHistory().add(30);
        assertEquals("rising", status().get("populationTrend"));
        h.setCastleType("stone_castle");
        assertEquals("Stone Castle", status().get("castleName"));
    }

    @Test
    void eventsCarryATone() {
        assertEquals("danger", HoldfastRules.eventTone("DAY 3 - BANDIT RAID! Bandits stole 4.0g!"));
        assertEquals("danger", HoldfastRules.eventTone("  Population casualties: 2"));
        assertEquals("warn", HoldfastRules.eventTone("DAY 7 - Food supplies low! 3 remaining"));
        assertEquals("success", HoldfastRules.eventTone("wheat field harvested! +20 food"));
        assertEquals("normal", HoldfastRules.eventTone("Day 10: Net +1.0g | Total: 50.0g"));

        List<Map<String, Object>> rows = new ArrayList<>();
        Map<String, Object> row = new HashMap<>();
        row.put("id", 1);
        row.put("day", 7);
        row.put("message", "Mines produced 2 stone");
        rows.add(row);
        when(db.getHoldfastEvents(anyInt())).thenReturn(rows);
        assertEquals("success", manager.getEvents("party").get(0).get("tone"));

        Map<String, Object> advanced = manager.advanceTime("party", 2);
        List<Map<String, Object>> entries = (List<Map<String, Object>>) advanced.get("eventEntries");
        assertEquals(((List<?>) advanced.get("events")).size(), entries.size());
        assertNotNull(entries.get(0).get("tone"));
    }

    @Test
    void replantUsesTheSharedSeedCosts() {
        h.setBuildingCount("wheat_field", 2);
        h.setGold(100);
        Map<String, Object> result = manager.replant("party", "wheat_field");
        assertEquals(true, result.get("success"));
        assertEquals(80.0, h.getGold(), 0.001);
        assertEquals(2, h.getWheatFieldPlantDays().size());
        assertEquals(false, manager.replant("party", "orchard").get("success"));
    }
}
