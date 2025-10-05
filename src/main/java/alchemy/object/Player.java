package alchemy.object;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class Player implements Serializable {

    private final int id; // Unique identifier for the player
    private final String username; // Player's username
    private final String password; // Player's password
    private final IInventory inventory; // Player's unique inventory
    private final IKnowledgeBook knowledgeBook; // Player's unique knowledge book
    private int level;
    
    // Additional user profile fields for unified system
    private String email; // Email address for account recovery and communication
    private String displayName; // Friendly display name (can be different from username)
    private LocalDateTime registrationDate; // When the account was created
    private LocalDateTime lastLoginDate; // When the user last logged in

    // Constructor with all fields including new user profile fields
    public Player(int id, String username, String password, IInventory inventory, IKnowledgeBook knowledgeBook, 
                  int level, String email, String displayName, LocalDateTime registrationDate, LocalDateTime lastLoginDate) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.inventory = inventory;
        this.knowledgeBook = knowledgeBook;
        this.level = level;
        this.email = email;
        this.displayName = displayName;
        this.registrationDate = registrationDate;
        this.lastLoginDate = lastLoginDate;
    }
    
    // Constructor with level but default user profile fields
    public Player(int id, String username, String password, IInventory inventory, IKnowledgeBook knowledgeBook, int level) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.inventory = inventory;
        this.knowledgeBook = knowledgeBook;
        this.level = level;
        this.email = null; // Can be set later
        this.displayName = username; // Default to username
        this.registrationDate = LocalDateTime.now();
        this.lastLoginDate = null; // Set on first login
    }
    
    // Constructor with default level and user profile fields
    public Player(int id, String username, String password, IInventory inventory, IKnowledgeBook knowledgeBook) {
        this(id, username, password, inventory, knowledgeBook, 1);
    }
    public int getLevel() {
        return level;
    }
    public boolean levelUp() {
        if (level < 10) {
            level++;
            //update player level
            return true;
        }
        return false;  // Already at max level.
    }
    public int getId() {
        return id;
    }


    public String getUsername() {
        return username;
    }


    public String getPassword() {
        return password;
    }


    public IInventory getInventory() {
        return inventory;
    }


    public IKnowledgeBook getKnowledgeBook() {
        return knowledgeBook;
    }

    // Getters and setters for new user profile fields
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }

    public LocalDateTime getLastLoginDate() {
        return lastLoginDate;
    }

    public void setLastLoginDate(LocalDateTime lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    @Override
    public String toString() {
        return "Player{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", displayName='" + displayName + '\'' +
                ", email='" + email + '\'' +
                ", level=" + level +
                ", registrationDate=" + registrationDate +
                ", lastLoginDate=" + lastLoginDate +
                ", inventory=" + inventory +
                ", knowledgeBook=" + knowledgeBook +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Player)) return false;
        Player player = (Player) o;
        return id == player.id &&
                level == player.level &&
                Objects.equals(username, player.username) &&
                Objects.equals(password, player.password) &&
                Objects.equals(email, player.email) &&
                Objects.equals(displayName, player.displayName) &&
                Objects.equals(registrationDate, player.registrationDate) &&
                Objects.equals(lastLoginDate, player.lastLoginDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, password, level, email, displayName, registrationDate, lastLoginDate);
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
