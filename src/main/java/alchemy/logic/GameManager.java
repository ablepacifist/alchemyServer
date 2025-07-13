package alchemy.logic;

import alchemy.data.IStubDatabase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GameManager implements GameManagerService {

    private final PlayerManager playerManager;
    private final PotionManager potionManager;

    @Autowired
    public GameManager(PlayerManager playerManager, PotionManager potionManager) {
        this.playerManager = playerManager;
        this.potionManager = potionManager;
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
        System.out.println("Game started!");
    }

    @Override
    public void endGame() {
        System.out.println("Game ended!");
    }

    @Override
    public String forage(int playerId) {
        return playerManager.forage(playerId);
    }
}
