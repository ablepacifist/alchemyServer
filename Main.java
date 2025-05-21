import java.sql.SQLException;
import java.io.IOException;
import data.HSQLDatabase;

public class Main {
    public static void main(String[] args) {
        // Define the database file path.
        // This will create files like "alchemydb.script", "alchemydb.properties", etc.
        // You can use an absolute path or a relative one.
        String dbPath = "alchemydb";  
        
        try {
            // Create (and initialize) the database.
            HSQLDatabase database = new HSQLDatabase(dbPath);
            System.out.println("Database has been initialized successfully.");
            
            // Optionally, you can call test methods here
            // For example: database.testQuery();
        } catch (SQLException | IOException ex) {
            ex.printStackTrace();
        }
    }
}

