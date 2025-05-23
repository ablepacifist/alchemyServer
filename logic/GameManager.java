package logic;

import data.IStubDatabase;

/**
 * New GameManager implementation that doesn’t depend on any application-level classes.
 * It must be initialized with an IStubDatabase instance.
 */
public class GameManager implements GameManagerService {
    
    // Singleton instance; you may change this pattern if you prefer another DI method.
    private static GameManager instance;

    private final PlayerManager playerManager;
    private final PotionManager potionManager;

    // Private constructor prevents direct instantiation.
    public GameManager(PlayerManager playerManager, PotionManager potionManager) {
        this.playerManager = playerManager;
        this.potionManager = potionManager;
    }

    /**
     * Returns the singleton instance of GameManager.
     * Requires an IStubDatabase instance to be passed in during the first call.
     *
     * @param db the database instance to use.
     * @return the singleton GameManager
     */
    public static GameManager getInstance(IStubDatabase db) {
        if (instance == null) {
            // Create manager instances using the provided database.
            PlayerManager pm = new PlayerManager(db);
            PotionManager pom = new PotionManager(db);
            instance = new GameManager(pm, pom);
        }
        return instance;
    }

    @Override
    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    @Override
    public PotionManager getPotionManager() {
        return potionManager;
    }

    @Override
    public void startGame() {
        // Initialize global game state here.
        System.out.println("Game started!");
    }

    @Override
    public void endGame() {
        // Cleanup tasks here.
        System.out.println("Game ended!");
    }

    @Override
    public String forage(int playerId) {
        return playerManager.forage(playerId);
    }
}
