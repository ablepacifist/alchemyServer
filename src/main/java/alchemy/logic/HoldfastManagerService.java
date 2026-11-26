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

    /** Lexicon permission that allows advancing a holdfast's clock. */
    String PERMISSION_ADVANCE_TIME = "HOLDFAST_ADVANCE_TIME";
    /** Lexicon permission that allows deleting a holdfast. */
    String PERMISSION_DELETE = "HOLDFAST_DELETE";

    /**
     * Advance time on behalf of the caller identified by a Lexicon SSO token.
     * The result always carries an HTTP-style {@code status}: 200 on success, 401 when the
     * caller is not signed in, 403 without {@link #PERMISSION_ADVANCE_TIME}, 404 when the
     * holdfast does not exist.
     */
    Map<String, Object> advanceTimeAs(String ssoToken, String groupName, int days);

    /** Delete on behalf of the caller; same {@code status} contract as {@link #advanceTimeAs}. */
    Map<String, Object> deleteHoldfastAs(String ssoToken, String groupName);
}
