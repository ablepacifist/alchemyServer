package alchemy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}




/* 
public class Main {
    // Optional: A logger for application-wide messages.
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        IStubDatabase db = null;
        // Initialize your database instance.
        // You can pass a configuration parameter (like a path or a JDBC URL fragment) that your HSQLDatabase uses.
        try{
            db = new HSQLDatabase("alchemydb");
        }catch(Exception ohno){
            System.err.print("error when starting the database. Is the database down?");
        }
        // Now pass the database instance to your PlayerManager.
        PlayerManager playerManager = new PlayerManager(db);
        PotionManager potionManager = new PotionManager(db);
        GameManager gameManager   = new GameManager(playerManager,potionManager); 

        // Optional: Testing a couple of operations.
        try {
            // For instance, register a player; if the username already exists, you'll receive an error.
            playerManager.registerPlayer("testUser", "testPass", "testPass");
        } catch (IllegalArgumentException ex) {
            logger.error("Registration error: {}", ex.getMessage());
        }

        try {
            Player player = playerManager.loginPlayer("testUser", "testPass");
            System.out.println("Logged in player: " + player);
        } catch (IllegalArgumentException ex) {
            System.err.println("Login error: " + ex.getMessage());
        }

        // You can continue wiring up other managers similarly.
        // For example, say you have IngredientManager, PotionManager, etc., pass them the same `db` instance.
    }
}


*/
