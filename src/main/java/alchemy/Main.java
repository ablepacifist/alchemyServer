package alchemy;
 
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
// this will let sprnig boot search the appilcation for classes that are used in the api
@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
//
/* 

import alchemy.data.HSQLDatabase;
import alchemy.data.IStubDatabase;
import alchemy.logic.GameManager;
import alchemy.logic.PlayerManager;
import alchemy.logic.PotionManager;
import alchemy.object.Player;

public class Main {
    //make a logger
    //private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        IStubDatabase db = null;
        // Initialize  database instance.
        try{
            db = new HSQLDatabase("alchemydb");
        }catch(Exception ohno){
            System.err.print("error when starting the database. Is the database down?");
        }
        // Now pass the database instance to Managers.
        PlayerManager playerManager = new PlayerManager(db);
        PotionManager potionManager = new PotionManager(db);
        GameManager gameManager   = new GameManager(playerManager,potionManager); 

        //Testing a couple of operations.
        try {
            // For instance, register a player; if the username already exists, you'll receive an error.
            playerManager.registerPlayer("testUser", "testPass", "testPass");
        } catch (IllegalArgumentException ex) {
            //logger.error("Registration error: {}", ex.getMessage());
        }

        try {
            Player player = playerManager.loginPlayer("testUser", "testPass");
            System.out.println("Logged in player: " + player);
        } catch (IllegalArgumentException ex) {
            System.err.println("Login error: " + ex.getMessage());
        }
    }
}


 */
