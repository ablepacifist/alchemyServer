package alchemy.logic;


import alchemy.logic.PlayerManager;

public interface GameManagerService {

    /**
     * Returns the PlayerManager instance.
     *
     * @return the PlayerManager
     */
    PlayerManager getPlayerManager();

    /**
     * Starts the game by initializing global game state.
     */
    void startGame();

    /**
     * Ends the game and performs cleanup operations.
     */
    void endGame();
    public PotionManager getPotionManager();

    String forage(int playerId);
}
