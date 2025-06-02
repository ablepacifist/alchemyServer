package test;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import alchemy.data.IStubDatabase;
import alchemy.logic.GameManager;
import alchemy.logic.GameManagerService;
import alchemy.logic.PlayerManager;
import alchemy.logic.PotionManager;

public class GameManagerTest {

    private DummyDatabase dummyDb;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @Before
    public void setUp() {
        // Redirect System.out to capture output for testing startGame and endGame.
        System.setOut(new PrintStream(outContent));
        
        // Create a fresh dummy database instance.
        dummyDb = new DummyDatabase();
        
        // If your GameManager singleton needs to be reset between tests,
        // add a reset mechanism here (for example, a resetInstance() method).
    }

    @Test
    public void testGetInstanceSingleton() {
        // Use the dummy database instead of a boolean flag.
        GameManagerService gm1 = GameManager.getInstance(dummyDb);
        GameManagerService gm2 = GameManager.getInstance(dummyDb);
        assertNotNull("GameManager instance should not be null", gm1);
        assertSame("GameManager should be a singleton", gm1, gm2);
    }

    @Test
    public void testGetPlayerManagerAndPotionManager() {
        GameManagerService gm = GameManager.getInstance(dummyDb);
        assertNotNull("PlayerManager should not be null", gm.getPlayerManager());
        assertNotNull("PotionManager should not be null", gm.getPotionManager());
        assertTrue("PlayerManager should be an instance of PlayerManager",
                gm.getPlayerManager() instanceof PlayerManager);
        assertTrue("PotionManager should be an instance of PotionManager",
                gm.getPotionManager() instanceof PotionManager);
    }

    @Test
    public void testStartGame() {
        GameManagerService gm = GameManager.getInstance(dummyDb);
        gm.startGame();
        String output = outContent.toString();
        assertTrue("startGame should print 'Game started!'", output.contains("Game started!"));
    }

    @Test
    public void testEndGame() {
        GameManagerService gm = GameManager.getInstance(dummyDb);
        // Clear previous captured output.
        outContent.reset();
        gm.endGame();
        String output = outContent.toString();
        assertTrue("endGame should print 'Game ended!'", output.contains("Game ended!"));
    }

    @After
    public void restoreStreams() {
        // Restore System.out back to its original stream.
        System.setOut(originalOut);
    }
}
