package alchemy.object;

import java.util.*;

public class Holdfast {
    private int id;
    private String groupName;
    private String holdfastName;
    private double baseGoldPerDay = 40.0;
    private int population = 40;
    private String castleType = "wood_fort";
    private double gold = 0.0;
    private int silver = 0;
    private double happiness = 50.0;
    private double targetHappiness = 50.0;
    private int daysElapsed = 0;
    private int beer = 0;
    private int grain = 0;
    private int wine = 0;
    private int tools = 0;
    private int raidsSurvived = 0;
    private int food = 0;
    private int wood = 0;
    private int stone = 0;
    private int iron = 0;
    private Map<String, Integer> buildings;
    private List<Integer> wheatFieldPlantDays = new ArrayList<>();
    private List<Integer> vegetableGardenPlantDays = new ArrayList<>();
    private List<Integer> orchardPlantDays = new ArrayList<>();
    private List<Integer> vineyardPlantDays = new ArrayList<>();
    private List<Integer> populationGrowthHistory = new ArrayList<>();

    public Holdfast() {
        buildings = new HashMap<>();
        for (String type : BuildingConfig.BUILDING_TYPES.keySet()) {
            buildings.put(type, 0);
        }
    }

    public Holdfast(String groupName, String holdfastName) {
        this();
        this.groupName = groupName;
        this.holdfastName = holdfastName;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getGroupName() { return groupName; }
    public void setGroupName(String groupName) { this.groupName = groupName; }

    public String getHoldfastName() { return holdfastName; }
    public void setHoldfastName(String holdfastName) { this.holdfastName = holdfastName; }

    public double getBaseGoldPerDay() { return baseGoldPerDay; }
    public void setBaseGoldPerDay(double baseGoldPerDay) { this.baseGoldPerDay = baseGoldPerDay; }

    public int getPopulation() { return population; }
    public void setPopulation(int population) { this.population = population; }

    public String getCastleType() { return castleType; }
    public void setCastleType(String castleType) { this.castleType = castleType; }

    public double getGold() { return gold; }
    public void setGold(double gold) { this.gold = gold; }

    public int getSilver() { return silver; }
    public void setSilver(int silver) { this.silver = silver; }

    public double getHappiness() { return happiness; }
    public void setHappiness(double happiness) { this.happiness = happiness; }

    public double getTargetHappiness() { return targetHappiness; }
    public void setTargetHappiness(double targetHappiness) { this.targetHappiness = targetHappiness; }

    public int getDaysElapsed() { return daysElapsed; }
    public void setDaysElapsed(int daysElapsed) { this.daysElapsed = daysElapsed; }

    public int getBeer() { return beer; }
    public void setBeer(int beer) { this.beer = beer; }

    public int getGrain() { return grain; }
    public void setGrain(int grain) { this.grain = grain; }

    public int getWine() { return wine; }
    public void setWine(int wine) { this.wine = wine; }

    public int getTools() { return tools; }
    public void setTools(int tools) { this.tools = tools; }

    public int getRaidsSurvived() { return raidsSurvived; }
    public void setRaidsSurvived(int raidsSurvived) { this.raidsSurvived = raidsSurvived; }

    public int getFood() { return food; }
    public void setFood(int food) { this.food = food; }

    public int getWood() { return wood; }
    public void setWood(int wood) { this.wood = wood; }

    public int getStone() { return stone; }
    public void setStone(int stone) { this.stone = stone; }

    public int getIron() { return iron; }
    public void setIron(int iron) { this.iron = iron; }

    public Map<String, Integer> getBuildings() { return buildings; }
    public void setBuildings(Map<String, Integer> buildings) { this.buildings = buildings; }

    public List<Integer> getWheatFieldPlantDays() { return wheatFieldPlantDays; }
    public void setWheatFieldPlantDays(List<Integer> wheatFieldPlantDays) { this.wheatFieldPlantDays = wheatFieldPlantDays; }

    public List<Integer> getVegetableGardenPlantDays() { return vegetableGardenPlantDays; }
    public void setVegetableGardenPlantDays(List<Integer> days) { this.vegetableGardenPlantDays = days; }

    public List<Integer> getOrchardPlantDays() { return orchardPlantDays; }
    public void setOrchardPlantDays(List<Integer> days) { this.orchardPlantDays = days; }

    public List<Integer> getVineyardPlantDays() { return vineyardPlantDays; }
    public void setVineyardPlantDays(List<Integer> days) { this.vineyardPlantDays = days; }

    public List<Integer> getPopulationGrowthHistory() { return populationGrowthHistory; }
    public void setPopulationGrowthHistory(List<Integer> history) { this.populationGrowthHistory = history; }

    public int getBuildingCount(String type) {
        return buildings.getOrDefault(type, 0);
    }

    public void setBuildingCount(String type, int count) {
        buildings.put(type, count);
    }
}
