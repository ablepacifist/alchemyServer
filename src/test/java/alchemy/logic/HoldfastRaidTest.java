package alchemy.logic;

import alchemy.data.IStubDatabase;
import alchemy.data.ILexiconIdentityClient;
import alchemy.object.Holdfast;
import org.junit.jupiter.api.Test;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Raid odds and damage. The status shows a daily chance; these tests check that the day loop
 * raids at that rate, and that a raid does at most half the old damage.
 */
class HoldfastRaidTest {

    private Holdfast current;

    private HoldfastManager manager(Random random) {
        IStubDatabase db = mock(IStubDatabase.class);
        when(db.getHoldfast("party")).thenAnswer(inv -> current);
        return new HoldfastManager(db, mock(ILexiconIdentityClient.class), random);
    }

    private static Holdfast freshHoldfast() {
        Holdfast h = new Holdfast("party", "Lockwood");
        h.setGold(1000);
        h.setFood(5000);
        h.setPopulation(400);
        h.setBuildingCount("tavern", 3);
        h.setBuildingCount("guard_tower", 2);
        return h;
    }

    @Test
    void raidChanceIsHalfWhatItWas() {
        assertEquals(4.0, HoldfastRules.raidChance(0), 1e-9, "no protection: 4%/day (was 8)");
        assertEquals(4.0 - 100 * 0.0375, HoldfastRules.raidChance(100), 1e-9);
        assertEquals(0.25, HoldfastRules.raidChance(1000), 1e-9, "floor 0.25% (was 0.5)");
        for (double p = 0; p <= 200; p += 10) {
            double old = Math.min(8, Math.max(0.5, 8 - p * 0.075));
            assertEquals(old / 2, HoldfastRules.raidChance(p), 1e-9, "half the old chance at protection " + p);
        }
    }

    @Test
    void weeklyChanceAddsUpTheDailyOne() {
        assertEquals(24.86, HoldfastRules.chanceOverDays(4.0, 7), 0.01);
        assertEquals(4.0, HoldfastRules.chanceOverDays(4.0, 1), 1e-9);
        assertEquals(0.0, HoldfastRules.chanceOverDays(0.0, 30), 1e-9);
    }

    @Test
    void statusShowsTheDailyAndWeeklyChance() {
        current = freshHoldfast();
        Map<String, Object> status = manager(new Random(1)).getStatus("party");
        double daily = (double) status.get("raidChance");
        double weekly = (double) status.get("raidChanceWeek");
        assertTrue(daily >= 0.25 && daily <= 4.0, "daily " + daily);
        assertEquals(HoldfastRules.chanceOverDays(daily, 7), weekly, 0.01);
    }

    /** Over many single days, raids happen as often as the status said they would. */
    @Test
    void raidsHappenAtTheRateTheStatusShows() {
        Random random = new Random(20261124);
        HoldfastManager m = manager(random);
        current = freshHoldfast();
        double shown = (double) m.getStatus("party").get("raidChance");

        int trials = 40_000;
        int raids = 0;
        for (int i = 0; i < trials; i++) {
            current = freshHoldfast();
            @SuppressWarnings("unchecked")
            List<String> events = (List<String>) m.advanceTime("party", 1).get("events");
            if (events.stream().anyMatch(e -> e.contains("BANDIT RAID"))) raids++;
        }
        double observed = 100.0 * raids / trials;
        assertEquals(shown, observed, 0.35, "shown " + shown + "%/day, observed " + observed + "%");
    }

    /** A raid at the worst end of every roll: at most 20% of gold, 2 buildings, 7.5% of people. */
    @Test
    void aRaidDoesAtMostHalfTheOldDamage() {
        current = freshHoldfast();
        int buildingsBefore = current.getBuildingCount("tavern") + current.getBuildingCount("guard_tower");
        HoldfastManager m = manager(new ScriptedRandom(0.0, 0.999999, 0.999999));

        @SuppressWarnings("unchecked")
        List<String> events = (List<String>) m.advanceTime("party", 1).get("events");

        String raid = events.stream().filter(e -> e.contains("BANDIT RAID")).findFirst().orElseThrow();
        Matcher stolenMatch = Pattern.compile("stole ([0-9.]+)g").matcher(raid);
        assertTrue(stolenMatch.find(), raid);
        double stolen = Double.parseDouble(stolenMatch.group(1));
        double goldBeforeRaid = current.getGold() + stolen;
        assertTrue(stolen <= goldBeforeRaid * 0.20 + 1e-6, "stole " + stolen + " of " + goldBeforeRaid);
        assertTrue(stolen >= goldBeforeRaid * 0.15 - 1, "stole " + stolen + " of " + goldBeforeRaid);

        int buildingsAfter = current.getBuildingCount("tavern") + current.getBuildingCount("guard_tower");
        assertEquals(2, buildingsBefore - buildingsAfter, "the worst roll destroys 2 (was up to 3)");

        int casualties = events.stream().filter(e -> e.contains("casualties"))
                .map(e -> Integer.parseInt(e.replaceAll("\\D+", ""))).findFirst().orElse(0);
        assertTrue(casualties <= (current.getPopulation() + casualties) * 0.075, "casualties " + casualties);
    }

    @Test
    void theLuckiestRaidDestroysNothing() {
        current = freshHoldfast();
        int before = current.getBuildingCount("tavern") + current.getBuildingCount("guard_tower");
        ScriptedRandom r = new ScriptedRandom(0.0, 0.0, 0.0);
        r.intsAtMax = false;
        manager(r).advanceTime("party", 1);
        assertEquals(before, current.getBuildingCount("tavern") + current.getBuildingCount("guard_tower"));
    }

    /** Returns the given doubles in order (then 0.999999: no raid, no growth), and extreme ints. */
    private static final class ScriptedRandom extends Random {
        private final Deque<Double> doubles = new ArrayDeque<>();
        boolean intsAtMax = true;

        ScriptedRandom(double... values) {
            for (double v : values) doubles.add(v);
        }

        @Override
        public double nextDouble() {
            return doubles.isEmpty() ? 0.999999 : doubles.poll();
        }

        @Override
        public int nextInt(int bound) {
            return intsAtMax ? bound - 1 : 0;
        }
    }
}
