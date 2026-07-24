package alchemy.logic;

import alchemy.object.Holdfast;
import java.util.List;
import java.util.Map;

public interface HoldfastManagerService {
    Holdfast createHoldfast(String groupName, String holdfastName);
    Holdfast getHoldfast(String groupName);
    List<Holdfast> getAllHoldfasts();
    Map<String, Object> advanceTime(String groupName, int days);
    Map<String, Object> build(String groupName, String buildingType);
    boolean withdraw(String groupName, double gold, int beer, int wine, int grain, int tools);
    Holdfast deposit(String groupName, double gold);
    void deleteHoldfast(String groupName);
    Map<String, Object> getStatus(String groupName);
    Holdfast importHoldfast(Holdfast holdfast);
    List<Map<String, Object>> getEvents(String groupName);
    Map<String, Object> replant(String groupName, String fieldType);
    Holdfast toggleFoodMarket(String groupName);
}
